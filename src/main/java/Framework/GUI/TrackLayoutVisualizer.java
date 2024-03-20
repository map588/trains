package Framework.GUI;

import Utilities.BasicBlock;
import Utilities.BlockParser;
import Utilities.Enums.Lines;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static javafx.application.Application.launch;

public class TrackLayoutVisualizer extends Application {
    private static final double NODE_RADIUS = 20;
    private static final double NODE_OFFSET = 50;
    private static final double EDGE_OFFSET = 20;
    private static final double WINDOW_MARGIN = 100;
    private Map<Integer, Point2D> nodePositions = new HashMap<>();


    private ConcurrentHashMap<Lines, ConcurrentLinkedQueue<BasicBlock>> trackLayout;
    private TabPane tabPane;

    public TrackLayoutVisualizer() {
        // Convert the ArrayDeque to ConcurrentLinkedQueue
        ConcurrentHashMap<Lines, ArrayDeque<BasicBlock>> parsedTrackLayout = BlockParser.parseCSV();
        trackLayout = new ConcurrentHashMap<>();
        for (Map.Entry<Lines, ArrayDeque<BasicBlock>> entry : parsedTrackLayout.entrySet()) {
            Lines line = entry.getKey();
            ArrayDeque<BasicBlock> basicBlocks = entry.getValue();
            trackLayout.put(line, new ConcurrentLinkedQueue<>(basicBlocks));
        }
    }

    @Override
    public void start(Stage primaryStage) {
        tabPane = new TabPane();

        for (Map.Entry<Lines, ConcurrentLinkedQueue<BasicBlock>> entry : trackLayout.entrySet()) {
            Lines line = entry.getKey();
            ConcurrentLinkedQueue<BasicBlock> basicBlocks = entry.getValue();

            Pane linePane = new Pane();
            drawTrackLine(linePane, basicBlocks);

            Tab lineTab = new Tab(line.toString());
            lineTab.setContent(linePane);
            tabPane.getTabs().add(lineTab);
        }

        Scene scene = new Scene(tabPane, 1920, 1080);
        primaryStage.setTitle("Track Layout Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void drawTrackLine(Pane linePane, ConcurrentLinkedQueue<BasicBlock> basicBlocks) {
        double x = NODE_OFFSET;
        double y = NODE_OFFSET;
        double directionX = NODE_OFFSET;
        double directionY = 0;

        double leftBoundary = 0;
        double topBoundary = 0;
        double rightBoundary = linePane.getWidth();
        double bottomBoundary = linePane.getHeight();
        Set<Integer> placedBlocks = new HashSet<>();
        Stack<BranchState> branchStates = new Stack<>();

        branchStates.push(new BranchState(x, y, directionX, directionY, leftBoundary, topBoundary, rightBoundary, bottomBoundary));

        while (!basicBlocks.isEmpty()) {
            BranchState currentState = branchStates.peek();
            drawSection(linePane, basicBlocks, placedBlocks, currentState, branchStates);
        }
    }

    private void drawSection(Pane linePane, ConcurrentLinkedQueue<BasicBlock> basicBlocks, Set<Integer> placedBlocks,
                             BranchState currentState, Stack<BranchState> branchStates) {
        double x = currentState.x;
        double y = currentState.y;
        double directionX = currentState.directionX;
        double directionY = currentState.directionY;
        double leftBoundary = currentState.leftBoundary;
        double topBoundary = currentState.topBoundary;
        double rightBoundary = currentState.rightBoundary;
        double bottomBoundary = currentState.bottomBoundary;

        BasicBlock prevBlock = null;

        while (!basicBlocks.isEmpty()) {
            BasicBlock block = basicBlocks.poll();

            if (!placedBlocks.contains(block.blockNumber())) {
                Point2D position = new Point2D(x, y);
                drawNode(linePane, block, position);
                placedBlocks.add(block.blockNumber());
                nodePositions.put(block.blockNumber(), position);

                if (prevBlock != null) {
                    Point2D prevPosition = new Point2D(x - directionX, y - directionY);
                    drawEdge(linePane, prevPosition, position, BasicBlock.Direction.TO_NODE);
                }

                if (block.blockType() == BasicBlock.BlockType.SWITCH) {
                    BasicBlock.NodeConnection connection = block.nodeConnection().get();
                    if (connection.altChildID().isPresent()) {
                        int altChildID = connection.altChildID().get();
                        BasicBlock altChildBlock = findBlockByNumber(altChildID, basicBlocks);
                        if (altChildBlock != null && !placedBlocks.contains(altChildID)) {
                            double altLeftBoundary = leftBoundary + NODE_OFFSET;
                            double altTopBoundary = topBoundary + NODE_OFFSET;
                            double altRightBoundary = rightBoundary - NODE_OFFSET;
                            double altBottomBoundary = bottomBoundary - NODE_OFFSET;
                            Point2D altPosition = calculateAlternatePosition(position, directionX, directionY);
                            branchStates.push(new BranchState(altPosition.getX(), altPosition.getY(), directionX, directionY,
                                    altLeftBoundary, altTopBoundary, altRightBoundary, altBottomBoundary));
                            drawAlternateBranch(linePane, altChildBlock, altPosition, block, basicBlocks, placedBlocks, branchStates);
                        }
                    }
                }
            }

            prevBlock = block;
            x += directionX;
            y += directionY;

            boolean shouldTurnRight = directionX > 0 && x + NODE_OFFSET > rightBoundary - WINDOW_MARGIN;
            boolean shouldTurnLeft = directionX < 0 && x - NODE_OFFSET < leftBoundary + WINDOW_MARGIN;
            boolean shouldTurnDown = directionY > 0 && y + NODE_OFFSET > bottomBoundary - WINDOW_MARGIN;
            boolean shouldTurnUp = directionY < 0 && y - NODE_OFFSET < topBoundary + WINDOW_MARGIN;

            boolean shouldTurn = shouldTurnRight || shouldTurnLeft || shouldTurnDown || shouldTurnUp;

            if (shouldTurn) {
                double controlX = x;
                double controlY = y;
                Point2D position = new Point2D(x, y);
                Point2D prevPosition = new Point2D(x - directionX, y - directionY);
                drawCurvedEdge(linePane, prevPosition, position, new Point2D(controlX, controlY), BasicBlock.Direction.TO_NODE);
                double tempX = directionX;
                directionX = -directionY;
                directionY = tempX;

                if (directionX > 0) {
                    leftBoundary = x;
                } else if (directionX < 0) {
                    rightBoundary = x;
                } else if (directionY > 0) {
                    topBoundary = y;
                } else if (directionY < 0) {
                    bottomBoundary = y;
                }
            }

            if (basicBlocks.isEmpty() || basicBlocks.peek().blockType() == BasicBlock.BlockType.SWITCH) {
                break;
            }
        }
    }

    private void drawAlternateBranch(Pane linePane, BasicBlock block, Point2D position, BasicBlock parent,
                                     ConcurrentLinkedQueue<BasicBlock> basicBlocks, Set<Integer> placedBlocks,
                                     Stack<BranchState> branchStates) {
        double x = position.getX();
        double y = position.getY();
        double directionX = branchStates.peek().directionX;
        double directionY = branchStates.peek().directionY;
        double leftBoundary = branchStates.peek().leftBoundary;
        double topBoundary = branchStates.peek().topBoundary;
        double rightBoundary = branchStates.peek().rightBoundary;
        double bottomBoundary = branchStates.peek().bottomBoundary;

        BasicBlock prevBlock = null;

        while (!basicBlocks.isEmpty()) {
            BasicBlock currentBlock = basicBlocks.peek();

            if (!placedBlocks.contains(currentBlock.blockNumber())) {
                Point2D currentPosition = new Point2D(x, y);
                drawNode(linePane, currentBlock, currentPosition);
                placedBlocks.add(currentBlock.blockNumber());
                nodePositions.put(currentBlock.blockNumber(), currentPosition);

                if (prevBlock != null) {
                    Point2D prevPosition = new Point2D(x - directionX, y - directionY);
                    drawEdge(linePane, prevPosition, currentPosition, BasicBlock.Direction.TO_NODE);
                }

                if (currentBlock.blockType() == BasicBlock.BlockType.SWITCH) {
                    BasicBlock.NodeConnection connection = currentBlock.nodeConnection().get();
                    if (connection.altChildID().isPresent()) {
                        int altChildID = connection.altChildID().get();
                        BasicBlock altChildBlock = findBlockByNumber(altChildID, basicBlocks);
                        if (altChildBlock != null && !placedBlocks.contains(altChildID)) {
                            double altLeftBoundary = leftBoundary + NODE_OFFSET;
                            double altTopBoundary = topBoundary + NODE_OFFSET;
                            double altRightBoundary = rightBoundary - NODE_OFFSET;
                            double altBottomBoundary = bottomBoundary - NODE_OFFSET;
                            Point2D altPosition = calculateAlternatePosition(currentPosition, directionX, directionY);
                            branchStates.push(new BranchState(altPosition.getX(), altPosition.getY(), directionX, directionY,
                                    altLeftBoundary, altTopBoundary, altRightBoundary, altBottomBoundary));
                            drawAlternateBranch(linePane, altChildBlock, altPosition, currentBlock, basicBlocks, placedBlocks, branchStates);
                        }
                    }
                }

                prevBlock = currentBlock;
                x += directionX;
                y += directionY;

                boolean shouldTurnRight = directionX > 0 && x + NODE_OFFSET > rightBoundary - WINDOW_MARGIN;
                boolean shouldTurnLeft = directionX < 0 && x - NODE_OFFSET < leftBoundary + WINDOW_MARGIN;
                boolean shouldTurnDown = directionY > 0 && y + NODE_OFFSET > bottomBoundary - WINDOW_MARGIN;
                boolean shouldTurnUp = directionY < 0 && y - NODE_OFFSET < topBoundary + WINDOW_MARGIN;

                boolean shouldTurn = shouldTurnRight || shouldTurnLeft || shouldTurnDown || shouldTurnUp;

                if (shouldTurn) {
                    double controlX = x;
                    double controlY = y;
                    Point2D currPosition = new Point2D(x, y);
                    Point2D prevPosition = new Point2D(x - directionX, y - directionY);
                    drawCurvedEdge(linePane, prevPosition, currPosition, new Point2D(controlX, controlY), BasicBlock.Direction.TO_NODE);
                    double tempX = directionX;
                    directionX = -directionY;
                    directionY = tempX;

                    if (directionX > 0) {
                        leftBoundary = x;
                    } else if (directionX < 0) {
                        rightBoundary = x;
                    } else if (directionY > 0) {
                        topBoundary = y;
                    } else if (directionY < 0) {
                        bottomBoundary = y;
                    }
                }

                basicBlocks.poll();
            } else {
                break;
            }
        }

        branchStates.pop();
    }

    private Point2D calculateAlternatePosition(Point2D currentPosition, double directionX, double directionY) {
        double offsetX = currentPosition.getX() + directionY * NODE_OFFSET;
        double offsetY = currentPosition.getY() - directionX * NODE_OFFSET;
        return new Point2D(offsetX, offsetY);
    }

    class BranchState {
        double x;
        double y;
        double directionX;
        double directionY;
        double leftBoundary;
        double topBoundary;
        double rightBoundary;
        double bottomBoundary;

        BranchState(double x, double y, double directionX, double directionY,
                    double leftBoundary, double topBoundary, double rightBoundary, double bottomBoundary) {
            this.x = x;
            this.y = y;
            this.directionX = directionX;
            this.directionY = directionY;
            this.leftBoundary = leftBoundary;
            this.topBoundary = topBoundary;
            this.rightBoundary = rightBoundary;
            this.bottomBoundary = bottomBoundary;
        }
    }

    private void drawCurvedEdge(Pane linePane, Point2D startPosition, Point2D endPosition, Point2D controlPoint, BasicBlock.Direction direction) {
        QuadCurve curve = new QuadCurve(
                startPosition.getX(), startPosition.getY(),
                controlPoint.getX(), controlPoint.getY(),
                endPosition.getX(), endPosition.getY()
        );
        curve.setStroke(getEdgeColor(direction));
        curve.setStrokeWidth(2);
        curve.setFill(null);
        linePane.getChildren().add(curve);
    }

    private Point2D calculateNextPosition(Point2D currentPosition, BasicBlock nextBlock) {
        double x = currentPosition.getX() + NODE_OFFSET;
        double y = currentPosition.getY();
        return new Point2D(x, y);
    }

    private BasicBlock findBlockByNumber(int blockNumber, ConcurrentLinkedQueue<BasicBlock> basicBlocks) {
        for (BasicBlock block : basicBlocks) {
            if (block.blockNumber() == blockNumber) {
                return block;
            }
        }
        return null;
    }

    private void drawNode(Pane linePane, BasicBlock block, Point2D position) {
        Circle nodeCircle = new Circle(position.getX(), position.getY(), NODE_RADIUS);
        nodeCircle.setFill(getNodeColor(block));
        linePane.getChildren().add(nodeCircle);

        Text label = new Text(String.valueOf(block.blockNumber()));
        label.setX(position.getX() - label.getLayoutBounds().getWidth() / 2);
        label.setY(position.getY() + NODE_RADIUS + 10);
        linePane.getChildren().add(label);
    }

    private void drawEdge(Pane linePane, Point2D startPosition, Point2D endPosition, BasicBlock.Direction direction) {
        Line edge = new Line(startPosition.getX(), startPosition.getY(), endPosition.getX(), endPosition.getY());
        edge.setStroke(getEdgeColor(direction));
        edge.setStrokeWidth(2);
        linePane.getChildren().add(edge);
    }

    private Color getNodeColor(BasicBlock block) {
        return switch (block.blockType()) {
            case REGULAR -> Color.GRAY;
            case SWITCH -> Color.GREEN;
            case STATION -> Color.ORANGE;
            case CROSSING -> Color.YELLOW;
            case YARD -> Color.BLACK;
        };
    }

    private Color getEdgeColor(BasicBlock.Direction direction) {
        return switch (direction) {
            case TO_NODE -> Color.BLUE;
            case FROM_NODE -> Color.RED;
            case BIDIRECTIONAL -> Color.PURPLE;
        };
    }


    private BasicBlock findNextBlock(BasicBlock block, ConcurrentLinkedQueue<BasicBlock> basicBlocks) {
        for (BasicBlock nextBlock : basicBlocks) {
            if (nextBlock.blockNumber() == block.blockNumber() + 1) {
                return nextBlock;
            }
        }
        return null;
    }

    private BasicBlock findReconnectBlock(BasicBlock block, ConcurrentLinkedQueue<BasicBlock> basicBlocks) {
        for (BasicBlock reconnectBlock : basicBlocks) {
            if (reconnectBlock.nodeConnection().isPresent()) {
                BasicBlock.NodeConnection connection = reconnectBlock.nodeConnection().get();
                if (connection.altChildID().isPresent() && connection.altChildID().get() == block.blockNumber()) {
                    return reconnectBlock;
                }
            }
        }
        return null;
    }

    class AlternateBranch {
        BasicBlock block;
        Point2D position;
        BasicBlock parent;

        AlternateBranch(BasicBlock block, Point2D position, BasicBlock parent) {
            this.block = block;
            this.position = position;
            this.parent = parent;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}