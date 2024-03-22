package Utilities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public record SwitchSection(ArrayList<BasicBlock> blocks, BasicBlock startSwitch, BasicBlock endSwitch, Direction direction) {
    public enum Direction {
        UNIDIRECTIONAL,
        BIDIRECTIONAL,
        UNKNOWN
    }
}
