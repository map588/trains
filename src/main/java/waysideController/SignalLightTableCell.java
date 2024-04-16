package waysideController;

import Utilities.BooleanIconTableCell;
import javafx.beans.property.BooleanProperty;

import static waysideController.Properties.hasLight_p;

public class SignalLightTableCell extends BooleanIconTableCell<WaysideBlockSubject> {

    private final String conditionalPropertyName;

    public SignalLightTableCell(String falseIconPath, String trueIconPath, int sizeX, int sizeY, String conditionalPropertyName) {
        super(falseIconPath, trueIconPath, sizeX, sizeY);
        this.conditionalPropertyName = conditionalPropertyName;
    }

    @Override
    public void updateItem(Boolean item, boolean empty) {
        WaysideBlockSubject block = this.getTableRow().getItem();
        if(block != null && block.getBooleanProperty(conditionalPropertyName).get()) {
            super.updateItem(item, empty);
        }
        else {
            setGraphic(null);
        }
    }
}