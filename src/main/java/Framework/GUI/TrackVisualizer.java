package Framework.GUI;

import Common.TrainModel;
import Utilities.BasicBlock;
import Utilities.Enums.BlockType;
import Utilities.Enums.Lines;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class TrackVisualizer extends Pane {
    private ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> trackLayout;
    private ConcurrentHashMap<Integer, Rectangle> blockRectangles;
    private ConcurrentHashMap<TrainModel, Rectangle> trainRectangles;

    public TrackVisualizer(ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> trackLayout) {
        this.trackLayout = trackLayout;
        this.blockRectangles = new ConcurrentHashMap<>();
        this.trainRectangles = new ConcurrentHashMap<>();
        drawTrackLayout();
    }

    private void drawTrackLayout() {
        for (ConcurrentSkipListMap<Integer, BasicBlock> blocks : trackLayout.values()) {
            for (BasicBlock block : blocks.values()) {
                Rectangle rectangle = createRectangle(block);
                blockRectangles.put(block.blockNumber(), rectangle);
                getChildren().add(rectangle);
            }
        }
    }

    private Rectangle createRectangle(BasicBlock block) {
        double width = block.blockLength() * 10;
        double height = 10;

        Rectangle rectangle = new Rectangle(width, height);
        rectangle.setLayoutX(block.blockNumber() * 20);
        rectangle.setLayoutY(50);

        // Set the fill color based on the track line
        Color fillColor = getTrackColor(block.trackLine());
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.GRAY);

        // Set the fill color based on the block type
        if (block.blockType() == BlockType.SWITCH) {
            rectangle.setFill(Color.BLUE);
        } else if (block.blockType() == BlockType.STATION) {
            rectangle.setFill(Color.RED);
        } else if (block.blockType() == BlockType.CROSSING) {
            rectangle.setFill(Color.YELLOW);
        }

        return rectangle;
    }

    private Color getTrackColor(String trackLine) {
        if (trackLine.equalsIgnoreCase("GREEN")) {
            return Color.GREEN.deriveColor(0, 1, 1, 0.5);
        } else if (trackLine.equalsIgnoreCase("RED")) {
            return Color.RED.deriveColor(0, 1, 1, 0.5);
        }
        return Color.TRANSPARENT;
    }

    public void updateTrainPosition(TrainModel train, int blockNumber, int positionInBlock) {
        Rectangle trainRectangle = trainRectangles.get(train);
        if (trainRectangle == null) {
            trainRectangle = createTrainRectangle();
            trainRectangles.put(train, trainRectangle);
            getChildren().add(trainRectangle);
        }

        Rectangle blockRectangle = blockRectangles.get(blockNumber);
        if (blockRectangle != null) {
            double blockWidth = blockRectangle.getWidth();
            double trainWidth = trainRectangle.getWidth();
            double trainX = blockRectangle.getLayoutX() + (positionInBlock / 100) * (blockWidth - trainWidth);
            trainRectangle.setLayoutX(trainX);
            trainRectangle.setLayoutY(blockRectangle.getLayoutY());

            // Set the fill color of the occupied block
            blockRectangle.setFill(getTrackColor(trackLayout.get(Lines.valueOf(train.getTrackLine())).get(blockNumber).trackLine()));
        }
    }

    private Rectangle createTrainRectangle() {
        double width = 10;
        double height = 10;

        Rectangle rectangle = new Rectangle(width, height);
        rectangle.setFill(Color.LIME);

        return rectangle;
    }

    public void removeTrainPosition(TrainModel train, int blockNumber) {
        Rectangle trainRectangle = trainRectangles.remove(train);
        if (trainRectangle != null) {
            getChildren().remove(trainRectangle);
        }

        // Reset the fill color of the previously occupied block
        Rectangle blockRectangle = blockRectangles.get(blockNumber);
        if (blockRectangle != null) {
            blockRectangle.setFill(Color.TRANSPARENT);
        }
    }

    public void updateSwitchState(int blockNumber, boolean state) {
        Rectangle blockRectangle = blockRectangles.get(blockNumber);
        if (blockRectangle != null) {
            if (state) {
                blockRectangle.setFill(Color.BLUE);
            } else {
                blockRectangle.setFill(Color.TRANSPARENT);
            }
        }
    }

    public void updateSegmentState(List<Integer> segmentBlocks, boolean state) {
        for (int blockNumber : segmentBlocks) {
            Rectangle blockRectangle = blockRectangles.get(blockNumber);
            if (blockRectangle != null) {
                if (state) {
                    blockRectangle.setStroke(Color.GREEN);
                } else {
                    blockRectangle.setStroke(Color.GRAY);
                }
            }
        }
    }
}