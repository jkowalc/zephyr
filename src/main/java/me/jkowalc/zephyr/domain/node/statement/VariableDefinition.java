package me.jkowalc.zephyr.domain.node.statement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class VariableDefinition extends Node implements Statement {
    private final String name;
    private final String typeName;
    private final boolean mutable;
    private final boolean reference;
    private final Expression defaultValue;

    public VariableDefinition(String name, String typeName, boolean mutable, boolean reference, Expression defaultValue) {
        super(null);
        this.name = name;
        this.typeName = typeName;
        this.mutable = mutable;
        this.reference = reference;
        this.defaultValue = defaultValue;
    }

    public VariableDefinition(TextPosition startPosition, String name, String typeName, boolean mutable, boolean reference, Expression defaultValue) {
        super(startPosition);
        this.name = name;
        this.typeName = typeName;
        this.mutable = mutable;
        this.reference = reference;
        this.defaultValue = defaultValue;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
