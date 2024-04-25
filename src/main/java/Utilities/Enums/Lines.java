package Utilities.Enums;

public enum Lines {
        RED,
        GREEN,
        NULL;

        public static Lines getLine(String line) {
            return switch (line) {
                case "RED", "Red", "red", "r" -> RED;
                case "GREEN", "Green", "green", "g" -> GREEN;
                default -> NULL;
            };
        }
}
