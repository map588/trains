package waysideController;

public record PLCChange(
        String changeType,
        int blockID,
        boolean changeValue) {}
