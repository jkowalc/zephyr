package me.jkowalc.zephyr.exception;

import lombok.Getter;

@Getter
public class TypeNotDefinedException extends Exception {
    private final String name;
    public TypeNotDefinedException(String name) {
        super("Type " + name + " not defined");
        this.name = name;
    }
}
