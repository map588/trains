package Framework.GUI;

import Utilities.BasicBlock;
import Utilities.BlockParser;
import Utilities.Enums.Lines;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Node;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TrackLayoutVisualizer extends Application {
    private static final double NODE_RADIUS = 20;
    private static final double NODE_OFFSET = 50;

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    private final Map<Integer, Point2D> nodePositions = new HashMap<>();
    private final ConcurrentHashMap<Lines, ConcurrentLinkedQueue<BasicBlock>> trackLayout;
    private TabPane tabPane;

    public TrackLayoutVisualizer() {
        // Convert the ArrayDeque to ConcurrentLinkedQueue
       ConcurrentHashMap<Lines, ArrayDeque<BasicBlock>> parsedTrackLayout = new ConcurrentHashMap<>(BlockParser.parseCSV());
        trackLayout = new ConcurrentHashMap<>();

        for (Map.Entry<Lines, ArrayDeque<BasicBlock>> entry : parsedTrackLayout.entrySet()) {
            Lines line = entry.getKey();
            ArrayDeque<BasicBlock> basicBlocks = entry.getValue();

            if (basicBlocks == null) {
                System.out.println("No blocks found for line: " + line);
                continue; // Skip this iteration if no blocks are found
            }

            ConcurrentLinkedQueue<BasicBlock> concurrentBasicBlocks = new ConcurrentLinkedQueue<>(basicBlocks);
            trackLayout.put(line, concurrentBasicBlocks);

        }
        System.out.println("Track layout initialization complete. Contents:");
        trackLayout.forEach((line, blocks) -> System.out.println(line + ": " + blocks.size() + " blocks"));

    }

    @Override
    public void start(Stage primaryStage) {
        tabPane = new TabPane();

        for (Map.Entry<Lines, ConcurrentLinkedQueue<BasicBlock>> entry : trackLayout.entrySet()) {

            Lines line = entry.getKey();
            ConcurrentLinkedQueue<BasicBlock> basicBlocks = entry.getValue();

            if (basicBlocks == null) {
                System.out.println("No blocks found for line: " + line);
                continue; // Skip this iteration if no blocks are found
            }

            Pane linePane = new Pane();
            drawTrackLine(linePane, basicBlocks);

            Tab lineTab = new Tab(line.toString());
            lineTab.setContent(linePane);
            tabPane.getTabs().add(lineTab);

            // Generate the textual representation for each line
            String textualRepresentation = generateTextualRepresentation(linePane);
            // Save the textual representation to a file
            String filename = "textual_representation_" + line.toString() + ".txt";
            saveTextualRepresentation(textualRepresentation, filename);
        }

        Scene scene = new Scene(tabPane, WIDTH, HEIGHT);

        primaryStage.setTitle("Track Layout Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    private void drawTrackLine(Pane linePane, ConcurrentLinkedQueue<BasicBlock> basicBlocks) {
        double x = NODE_OFFSET;
        double y = NODE_OFFSET;
        int directionX = 1;
        int directionY = 0;
        double leftBoundary = NODE_OFFSET;
        double topBoundary = NODE_OFFSET;
        double rightBoundary = WIDTH - NODE_OFFSET;
        double bottomBoundary = HEIGHT - NODE_OFFSET;
        Set<Integer> placedBlocks = new HashSet<>();
        Queue<AlternateBranch> alternateBranches = new ConcurrentLinkedDeque<>();
        BasicBlock prevBlock = null;

        while (!basicBlocks.isEmpty() || !alternateBranches.isEmpty()) {
            if (!basicBlocks.isEmpty()) {
                BasicBlock block = basicBlocks.poll();

                if (!placedBlocks.contains(block.blockNumber())) {
                    Point2D position = new Point2D(x, y);
                    drawNode(linePane, block, position);
                    placedBlocks.add(block.blockNumber());
                    nodePositions.put(block.blockNumber(), position);

                    if (prevBlock != null) {
                        Point2D prevPosition = new Point2D(x - directionX * NODE_OFFSET, y - directionY * NODE_OFFSET);
                        drawEdge(linePane, prevPosition, position, BasicBlock.Direction.TO_SWITCH);
                    }

                    if (block.blockType() == BasicBlock.BlockType.SWITCH) {
                        BasicBlock.NodeConnection connection = block.nodeConnection().get();
                        if (connection.altChildID().isPresent()) {
                            int altChildID = connection.altChildID().get();
                            BasicBlock altChildBlock = findBlockByNumber(altChildID, basicBlocks);
                            if (altChildBlock != null) {
                                Point2D altPosition = calculateAlternatePosition(position, directionX, directionY);
                                alternateBranches.add(new AlternateBranch(altChildBlock, altPosition, directionX, directionY, leftBoundary, topBoundary, rightBoundary, bottomBoundary));
                            }
                        }
                    }
                }

                prevBlock = block;
                x += directionX * NODE_OFFSET;
                y += directionY * NODE_OFFSET;

                if (x < leftBoundary || x > rightBoundary || y < topBoundary || y > bottomBoundary) {

                    if(directionX == 0 && directionY == 1) {
                        directionX = 1;
                        directionY = 0;

                        if(!alternateBranches.isEmpty()) {
                            alternateBranches.peek().directionX = 0;
                            alternateBranches.peek().directionY = 1;
                        }

                    } else if(directionX == 1 && directionY == 0) {
                        directionX = 0;
                        directionY = -1;

                        if(!alternateBranches.isEmpty()) {
                            alternateBranches.peek().directionX = 1;
                            alternateBranches.peek().directionY = 0;
                        }

                    } else if(directionX == 0 && directionY == -1) {
                        directionX = -1;
                        directionY = 0;

                        if(!alternateBranches.isEmpty()) {
                            alternateBranches.peek().directionX = 0;
                            alternateBranches.peek().directionY = -1;
                        }

                    } else if(directionX == -1 && directionY == 0) {
                        directionX = 0;
                        directionY = 1;

                        if(!alternateBranches.isEmpty()) {
                            alternateBranches.peek().directionX = -1;
                            alternateBranches.peek().directionY = 0;
                        }
                    }

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
            }

            if (!alternateBranches.isEmpty()) {
                AlternateBranch branch = alternateBranches.poll();
                drawAlternateBranch(linePane, branch.block, branch.position, branch.directionX, branch.directionY, placedBlocks, branch.leftBoundary, branch.topBoundary, branch.rightBoundary, branch.bottomBoundary);
            }
        }
    }

    private void drawAlternateBranch(Pane linePane, BasicBlock block, Point2D position, int directionX, int directionY,
                                     Set<Integer> placedBlocks, double leftBoundary, double topBoundary,
                                     double rightBoundary, double bottomBoundary) {
        if (!placedBlocks.contains(block.blockNumber())) {
            drawNode(linePane, block, position);
            placedBlocks.add(block.blockNumber());
            nodePositions.put(block.blockNumber(), position);

            BasicBlock nextBlock = findNextBlock(block, trackLayout.get(block.trackLine()));
            double x = position.getX();
            double y = position.getY();

            while (nextBlock != null && !placedBlocks.contains(nextBlock.blockNumber())) {
                double nextX = x + directionX * NODE_OFFSET;
                double nextY = y + directionY * NODE_OFFSET;

                if (nextX < leftBoundary || nextX > rightBoundary || nextY < topBoundary || nextY > bottomBoundary) {
                    int tempX = directionX;
                    directionX = -directionY;
                    directionY = tempX;
                    nextX = x + directionX * NODE_OFFSET;
                    nextY = y + directionY * NODE_OFFSET;

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

                Point2D nextPosition = new Point2D(nextX, nextY);
                drawNode(linePane, nextBlock, nextPosition);
                placedBlocks.add(nextBlock.blockNumber());
                nodePositions.put(nextBlock.blockNumber(), nextPosition);

                drawEdge(linePane, position, nextPosition, BasicBlock.Direction.TO_SWITCH);

                position = nextPosition;
                x = nextX;
                y = nextY;
                block = nextBlock;
                nextBlock = findNextBlock(block, trackLayout.get(block.trackLine()));
            }

            if (nextBlock != null && placedBlocks.contains(nextBlock.blockNumber())) {
                Point2D nextPosition = nodePositions.get(nextBlock.blockNumber());
                drawEdge(linePane, position, nextPosition, BasicBlock.Direction.TO_SWITCH);
            }
        }
    }

    private Point2D calculateAlternatePosition(Point2D currentPosition, int directionX, int directionY) {
        double offsetX = directionY * NODE_OFFSET;
        double offsetY = -directionX * NODE_OFFSET;
        double x = currentPosition.getX() + offsetX + directionX * NODE_OFFSET;
        double y = currentPosition.getY() + offsetY + directionY * NODE_OFFSET;
        return new Point2D(x, y);
    }

    class AlternateBranch {
        BasicBlock block;
        Point2D position;
        int directionX;
        int directionY;
        double leftBoundary;
        double topBoundary;
        double rightBoundary;
        double bottomBoundary;

        AlternateBranch(BasicBlock block, Point2D position, int directionX, int directionY,
                        double leftBoundary, double topBoundary, double rightBoundary, double bottomBoundary) {
            this.block = block;
            this.position = position;
            this.directionX = directionX;
            this.directionY = directionY;
            this.leftBoundary = leftBoundary;
            this.topBoundary = topBoundary;
            this.rightBoundary = rightBoundary;
            this.bottomBoundary = bottomBoundary;
        }
    }
    private Point2D calculateNextPosition(Point2D currentPosition, int directionX, int directionY) {
        double x = currentPosition.getX() + directionX * NODE_OFFSET;
        double y = currentPosition.getY() + directionY * NODE_OFFSET;
        return new Point2D(x, y);
    }

    //_________________________________________________________

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
        double startX = startPosition.getX();
        double startY = startPosition.getY();
        double endX = endPosition.getX();
        double endY = endPosition.getY();

        double deltaX = endX - startX;
        double deltaY = endY - startY;
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double unitX = deltaX / length;
        double unitY = deltaY / length;

        double offsetStartX = startX + unitX * NODE_RADIUS;
        double offsetStartY = startY + unitY * NODE_RADIUS;
        double offsetEndX = endX - unitX * NODE_RADIUS;
        double offsetEndY = endY - unitY * NODE_RADIUS;

        Line edge = new Line(offsetStartX, offsetStartY, offsetEndX, offsetEndY);
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
            case TO_SWITCH -> Color.BLUE;
            case FROM_SWITCH -> Color.RED;
            case BIDIRECTIONAL -> Color.PURPLE;
        };
    }


    private BasicBlock findNextBlock(BasicBlock block, ConcurrentLinkedQueue<BasicBlock> basicBlocks) {
        if (basicBlocks == null) {
            return null; // Or other appropriate handling
        }
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

    private boolean isWithinBounds(Point2D position) {
        return position.getX() >= 0 && position.getX() <= WIDTH && position.getY() >= 0 && position.getY() <= HEIGHT;
    }


    private String generateTextualRepresentation(Pane linePane) {
        StringBuilder sb = new StringBuilder();
        sb.append("Track Layout Textual Representation:\n");
        sb.append("-------------------------------------\n");

        for (Node node : linePane.getChildren()) {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;
                double x = circle.getCenterX();
                double y = circle.getCenterY();
                double radius = circle.getRadius();
                Color color = (Color) circle.getFill();

                sb.append(String.format("Node: (%.0f, %.0f), Radius: %.0f, Color: %s\n", x, y, radius, colorToString(color)));
            } else if (node instanceof Line) {
                Line line = (Line) node;
                double startX = line.getStartX();
                double startY = line.getStartY();
                double endX = line.getEndX();
                double endY = line.getEndY();
                Color color = (Color) line.getStroke();

                sb.append(String.format("Edge: (%.0f, %.0f) -> (%.0f, %.0f), Color: %s\n", startX, startY, endX, endY, colorToString(color)));
            } else if (node instanceof Text) {
                Text text = (Text) node;
                double x = text.getX();
                double y = text.getY();
                String content = text.getText();

                sb.append(String.format("Text: (%.0f, %.0f), Content: %s\n", x, y, content));
            }
        }

        return sb.toString();
    }

    private String colorToString(Color color) {
        if (color.equals(Color.GRAY)) {
            return "GRAY";
        } else if (color.equals(Color.GREEN)) {
            return "GREEN";
        } else if (color.equals(Color.ORANGE)) {
            return "ORANGE";
        } else if (color.equals(Color.YELLOW)) {
            return "YELLOW";
        } else if (color.equals(Color.BLACK)) {
            return "BLACK";
        } else if (color.equals(Color.BLUE)) {
            return "BLUE";
        } else if (color.equals(Color.RED)) {
            return "RED";
        } else if (color.equals(Color.PURPLE)) {
            return "PURPLE";
        } else {
            return "UNKNOWN";
        }
    }

    private void saveTextualRepresentation(String textualRepresentation, String filename) {
        try {
            Path filePath = Path.of(filename);
            Files.writeString(filePath, textualRepresentation, StandardCharsets.UTF_8);
            System.out.println("Textual representation saved to: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save textual representation: " + e.getMessage());
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}