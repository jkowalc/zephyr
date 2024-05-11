package me.jkowalc.zephyr.domain.node.expression;


import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public final class VariableReference extends Node implements Expression {
    private final String name;

    public VariableReference(String name) {
        super(null, null);
        this.name = name;
    }
    public VariableReference(TextPosition startPosition, TextPosition endPosition, String name) {
        super(startPosition, endPosition);
        this.name = name;
    }
}
