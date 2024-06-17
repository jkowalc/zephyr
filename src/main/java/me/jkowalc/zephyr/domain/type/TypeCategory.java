package me.jkowalc.zephyr.domain.type;

public enum TypeCategory {
    INT,
    FLOAT,
    STRING,
    BOOL,
    VOID,
    UNION,
    STRUCT;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
