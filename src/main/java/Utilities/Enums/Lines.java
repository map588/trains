package Utilities.Enums;

public enum Lines {
        RED,
        GREEN,
        NULL;

        public static Lines getLine(String line) {
            return switch (line) {
                case "Red" -> RED;
                case "Green" -> GREEN;
                default -> NULL;
            };
        }
}
