package Framework.Support;

import Utilities.Enums.Lines;

public record BlockIDs(int blockIdNum, Lines line) {

    // Static factory method
    public static BlockIDs of(int blockIdNum, Lines line) {
        return new BlockIDs(blockIdNum, line);
    }

}
