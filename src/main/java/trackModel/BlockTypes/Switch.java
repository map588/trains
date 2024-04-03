package trackModel.BlockTypes;

import Utilities.Enums.Direction;
import Utilities.Records.BasicBlock;

public interface Switch {
    BasicBlock.Connection getNorthDef();

    BasicBlock.Connection getSouthDef();

    BasicBlock.Connection getNorthAlt();

    BasicBlock.Connection getSouthAlt();

    boolean getSwitchState();

    void setSwitchState(boolean switchState);

    boolean getAutoState();

    void setSwitchStateAuto(boolean switchStateAuto);

    BasicBlock.Connection getNextBlock(Direction direction);
}
