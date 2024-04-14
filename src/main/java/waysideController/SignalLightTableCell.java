package waysideController;

import Utilities.BooleanIconTableCell;
import javafx.beans.property.BooleanProperty;

import static waysideController.Properties.hasLight_p;

public class SignalLightTableCell extends BooleanIconTableCell<WaysideBlockSubject> {

    public SignalLightTableCell(String falseIconPath, String trueIconPath, int sizeX, int sizeY) {
        super(falseIconPath, trueIconPath, sizeX, sizeY);
    }

    @Override
    public void updateItem(Boolean item, boolean empty) {
        WaysideBlockSubject block = this.getTableRow().getItem();
        if(block != null && block.getBooleanProperty(hasLight_p).get()) {
            super.updateItem(item, empty);
        }
        else {
            setGraphic(null);
        }
    }
}
