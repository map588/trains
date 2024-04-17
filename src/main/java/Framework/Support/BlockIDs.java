package Framework.Support;

import Utilities.Enums.Lines;

import java.util.Objects;

public record BlockIDs(int blockIdNum, Lines line) {

    // Static factory method
    public static BlockIDs of(int blockIdNum, Lines line) {
        return new BlockIDs(blockIdNum, line);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockIDs id = (BlockIDs) o;
        return Objects.equals(blockIdNum, id.blockIdNum) && Objects.equals(line, id.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockIdNum + line.ordinal()* 200);
    }
}
