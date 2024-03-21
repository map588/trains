package Framework.GUI;

import Utilities.BasicBlock;
import Utilities.BlockParser;
import Utilities.Enums.Lines;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

public class TrackLayoutVisualizer extends Application {
    private static final double NODE_RADIUS = 20;
    private static final double NODE_OFFSET = 50;
    private static final double EDGE_OFFSET = 20;
    private static final double WINDOW_MARGIN = 100;
    private final Map<Integer, Point2D> nodePositions = new HashMap<>();


    private final ConcurrentHashMap<Lines, ConcurrentLinkedQueue<BasicBlock>> trackLayout;
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
        double rightBoundary = 1920;
        double bottomBoundary = 1080;
        Set<Integer> placedBlocks = new HashSet<>();
        Queue<AlternateBranch> alternateBranches = new LinkedList<>();

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

                    boolean shouldTurnRight = directionX > 0 && x + NODE_OFFSET > rightBoundary - WINDOW_MARGIN;
                    boolean shouldTurnLeft = directionX < 0 && x - NODE_OFFSET < leftBoundary + WINDOW_MARGIN;
                    boolean shouldTurnDown = directionY > 0 && y + NODE_OFFSET > bottomBoundary - WINDOW_MARGIN;
                    boolean shouldTurnUp = directionY < 0 && y - NODE_OFFSET < topBoundary + WINDOW_MARGIN;

                    boolean shouldTurn = shouldTurnRight || shouldTurnLeft || shouldTurnDown || shouldTurnUp;

                    if (shouldTurn) {
                        double controlX = x;
                        double controlY = y;
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
                    } else {
                        drawEdge(linePane, prevPosition, position, BasicBlock.Direction.TO_NODE);
                    }
                }

                if (block.blockType() == BasicBlock.BlockType.SWITCH) {
                    BasicBlock.NodeConnection connection = block.nodeConnection().get();
                    if (connection.altChildID().isPresent()) {
                        int altChildID = connection.altChildID().get();
                        BasicBlock altChildBlock = findBlockByNumber(altChildID, basicBlocks);
                        if (altChildBlock != null) {
                            Point2D altPosition = calculateAlternatePosition(position, altChildBlock);
                            alternateBranches.add(new AlternateBranch(altChildBlock, altPosition, block));
                        }
                    }
                }
            }

            prevBlock = block;
            x += directionX;
            y += directionY;

            if (basicBlocks.isEmpty() || basicBlocks.peek().blockType() == BasicBlock.BlockType.SWITCH) {
                while (!alternateBranches.isEmpty()) {
                    AlternateBranch branch = alternateBranches.poll();
                    drawAlternateBranch(linePane, branch.block, branch.position, branch.parent, basicBlocks, placedBlocks);
                }
            }
        }
    }

    private void drawAlternateBranch(Pane linePane, BasicBlock block, Point2D position, BasicBlock parent,
                                     ConcurrentLinkedQueue<BasicBlock> basicBlocks, Set<Integer> placedBlocks) {
        if (!placedBlocks.contains(block.blockNumber())) {
            drawNode(linePane, block, position);
            placedBlocks.add(block.blockNumber());
            nodePositions.put(block.blockNumber(), position);

            Point2D parentPosition = nodePositions.get(parent.blockNumber());
            drawEdge(linePane, parentPosition, position, BasicBlock.Direction.TO_NODE);

            BasicBlock nextBlock = findNextBlock(block, basicBlocks);
            if (nextBlock != null) {
                Point2D nextPosition = calculateNextPosition(position, nextBlock);
                drawAlternateBranch(linePane, nextBlock, nextPosition, block, basicBlocks, placedBlocks);
            } else {
                BasicBlock reconnectBlock = findReconnectBlock(block, basicBlocks);
                if (reconnectBlock != null) {
                    Point2D reconnectPosition = nodePositions.get(reconnectBlock.blockNumber());
                    drawEdge(linePane, position, reconnectPosition, BasicBlock.Direction.TO_NODE);
                }
            }
        }
    }

    private Point2D calculateAlternatePosition(Point2D currentPosition, BasicBlock alternateBlock) {
        double x = currentPosition.getX() + NODE_OFFSET;
        double y = currentPosition.getY() + NODE_OFFSET;
        return new Point2D(x, y);
    }

    private Point2D calculateNextPosition(Point2D currentPosition, BasicBlock nextBlock) {
        double x = currentPosition.getX() + NODE_OFFSET;
        double y = currentPosition.getY();
        return new Point2D(x, y);
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