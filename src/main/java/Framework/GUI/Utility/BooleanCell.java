package Framework.GUI.Utility;

import Utilities.BlockInfo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;


public class BooleanCell extends TableCell<BlockInfo, Boolean> {
    private CheckBox checkBox;

    /**
     * A class to create a cell that contains a checkbox
     */
    public BooleanCell() {
        checkBox = new CheckBox();
        checkBox.setDisable(true);
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if(isEditing()) {
                    commitEdit(newValue == null ? false: newValue);

                }
            }
        });
        this.setGraphic(checkBox);
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setEditable(true);
    }
    @Override
    public void startEdit() {
        super.startEdit();
        if(!isEmpty()) {
            checkBox.setDisable(false);
            checkBox.requestFocus();
        }
    }
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        checkBox.setDisable(true);
    }
    @Override
    public void commitEdit(Boolean value) {
        super.commitEdit(value);
        checkBox.setDisable(true);
    }
    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if(!isEmpty()) {
            checkBox.setSelected(item);
        }
    }
}
