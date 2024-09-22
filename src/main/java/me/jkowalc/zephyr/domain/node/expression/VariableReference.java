package me.jkowalc.zephyr.domain.node.expression;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class VariableReference extends Node implements Expression, Assignable {
    private final String name;

    public VariableReference(String name) {
        super(null);
        this.name = name;
    }
    public VariableReference(TextPosition startPosition, String name) {
        super(startPosition);
        this.name = name;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
    public String toString() {
        return name;
    }
}
