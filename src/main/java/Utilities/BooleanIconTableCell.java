package Utilities;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BooleanIconTableCell<T> extends TableCell<T, Boolean> {
    private ImageView trueIcon;
    private ImageView falseIcon;

    /**
     * Create a new TableCell with two icons to display for true and false values. Providing a null value for either
     * icon path will result in no icon being displayed for that value.
     * @param falseIconPath path to the icon to display for false values (null or empty string for no icon)
     * @param trueIconPath path to the icon to display for true values (null or empty string for no icon)
     * @param sizeX display width of the icon
     * @param sizeY display height of the icon
     */
    public BooleanIconTableCell(String falseIconPath, String trueIconPath, int sizeX, int sizeY) {
        if(falseIconPath != null && !falseIconPath.isEmpty()) {
            falseIcon = new ImageView(new Image(getClass().getResourceAsStream(falseIconPath)));
            falseIcon.setFitWidth(sizeX);
            falseIcon.setFitHeight(sizeY);
            falseIcon.setPreserveRatio(true);
        }

        if(trueIconPath != null && !trueIconPath.isEmpty()) {
            trueIcon = new ImageView(new Image(getClass().getResourceAsStream(trueIconPath)));
            trueIcon.setFitWidth(sizeX);
            trueIcon.setFitHeight(sizeY);
            trueIcon.setPreserveRatio(true);
        }

        setGraphic(falseIcon);
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            setGraphic(item ? trueIcon : falseIcon);
        }
    }
}
