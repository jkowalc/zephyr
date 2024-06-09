package me.jkowalc.zephyr.domain.type;

public enum TypeCategory {
    INT,
    FLOAT,
    STRING,
    BOOL,
    VOID,
    NAMED_TYPE,
    STRUCT,
    ANY;

    public static TypeCategory fromString(String type) {
        return switch (type) {
            case "int" -> INT;
            case "float" -> FLOAT;
            case "string" -> STRING;
            case "bool" -> BOOL;
            case null -> VOID;
            default -> NAMED_TYPE;
        };
    }
}
