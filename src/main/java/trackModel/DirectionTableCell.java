package trackModel;

import Utilities.HelperObjects.BooleanIconTableCell;

public class DirectionTableCell extends BooleanIconTableCell<TrackBlockSubject> {

    public DirectionTableCell(String falseIconPath, String trueIconPath, int sizeX, int sizeY) {
        super(falseIconPath, trueIconPath, sizeX, sizeY);
    }

    @Override
    public void updateItem(Boolean item, boolean empty) {
        TrackBlockSubject block = this.getTableRow().getItem();
        if(block != null && block.isIsOccupied()) {
            super.updateItem(item, empty);
        }
        else {
            setGraphic(null);
        }
    }
}
