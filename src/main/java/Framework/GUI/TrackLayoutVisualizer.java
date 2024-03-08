package Framework.GUI;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import Utilities.BasicBlock;
import Utilities.BlockParser;
import Utilities.Enums.*;

import javax.swing.text.Utilities;

public class TrackLayoutVisualizer extends Application {
    private static final double NODE_RADIUS = 10;
    private static final double NODE_SPACING = 40;
    private static final double LINE_SPACING = 150;

    private Map<Integer, Point2D> nodePositions = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        HashMap<Lines, ArrayDeque<BasicBlock>> trackLayout = BlockParser.parseCSV();

        double lineOffset = 0;
        for (Map.Entry<Lines, ArrayDeque<BasicBlock>> entry : trackLayout.entrySet()) {
            Lines line = entry.getKey();
            ArrayDeque<BasicBlock> basicBlocks = entry.getValue();

            // Calculate node positions for the current line
            calculateNodePositions(basicBlocks, lineOffset);

            // Draw nodes for the current line
            for (BasicBlock block : basicBlocks) {
                Point2D position = nodePositions.get(block.blockNumber());
                Circle node = new Circle(position.getX(), position.getY(), NODE_RADIUS);
                node.setFill(getNodeColor(block));
                root.getChildren().add(node);

                Text label = new Text(String.valueOf(block.blockNumber()));
                label.setX(position.getX() - label.getLayoutBounds().getWidth() / 2);
                label.setY(position.getY() + NODE_RADIUS + 10);
                root.getChildren().add(label);
            }

            // Draw edges for the current line
            BasicBlock prevBlock = null;
            BasicBlock.Direction prevDirection = null;
            for (BasicBlock block : basicBlocks) {
                if (prevBlock != null && prevDirection != null) {
                    Point2D startPosition = nodePositions.get(prevBlock.blockNumber());
                    Point2D endPosition = nodePositions.get(block.blockNumber());
                    drawEdge(root, startPosition, endPosition, prevDirection);
                }

                if (block.nodeConnection().isPresent()) {
                    BasicBlock.NodeConnection connection = block.nodeConnection().get();
                    Point2D startPosition = nodePositions.get(block.blockNumber());

                    if (nodePositions.containsKey(connection.defChildID())) {
                        Point2D endPosition = nodePositions.get(connection.defChildID());
                        drawEdge(root, startPosition, endPosition, connection.defDirection());
                        prevDirection = connection.defDirection();
                    } else {
                        System.err.println("Node position not found for block: " + connection.defChildID());
                    }

                    if (connection.altChildID().isPresent()) {
                        int altChildID = connection.altChildID().get();
                        if (nodePositions.containsKey(altChildID)) {
                            Point2D endPosition = nodePositions.get(altChildID);
                            drawEdge(root, startPosition, endPosition, connection.altDirection().get());
                            prevDirection = connection.altDirection().get();
                        } else {
                            System.err.println("Node position not found for block: " + altChildID);
                        }
                    }
                }

                prevBlock = block;
            }

            lineOffset += LINE_SPACING;
        }

        Scene scene = new Scene(root, 1920, 1080);
        primaryStage.setTitle("Track Layout Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void calculateNodePositions(ArrayDeque<BasicBlock> basicBlocks, double lineOffset) {
        double x = NODE_SPACING;
        double y = NODE_SPACING + lineOffset;

        for (BasicBlock block : basicBlocks) {
            nodePositions.put(block.blockNumber(), new Point2D(x, y));
            x += NODE_SPACING;

            if (x >= 800 - NODE_SPACING) {
                x = NODE_SPACING;
                y += NODE_SPACING;
            }
        }
    }

    private Color getNodeColor(BasicBlock block) {
        return switch (block.blockType()) {
            case REGULAR -> Color.GRAY;
            case SWITCH -> Color.BLUE;
            case STATION -> Color.GREEN;
            case CROSSING -> Color.YELLOW;
            case YARD -> Color.BLACK;
        };
    }

    private void drawEdge(Pane root, Point2D startPosition, Point2D endPosition, BasicBlock.Direction direction) {
        Line edge = new Line(startPosition.getX(), startPosition.getY(), endPosition.getX(), endPosition.getY());
        edge.setStroke(getEdgeColor(direction));
        edge.setStrokeWidth(2);
        root.getChildren().add(edge);
    }

    private Color getEdgeColor(BasicBlock.Direction direction) {
        return switch (direction) {
            case TO_NODE -> Color.GREEN;
            case FROM_NODE -> Color.RED;
            case BIDIRECTIONAL -> Color.ORANGE;
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}