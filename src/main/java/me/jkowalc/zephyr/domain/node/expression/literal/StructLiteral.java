package me.jkowalc.zephyr.domain.node.expression.literal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class StructLiteral extends Node implements Literal {
    private final Map<String, Literal> fields;

    public StructLiteral(Map<String, Literal> fields) {
        super(null, null);
        this.fields = fields;
    }

    public StructLiteral(TextPosition startPosition, TextPosition endPosition, Map<String, Literal> fields) {
        super(startPosition, endPosition);
        this.fields = fields;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}