package me.jkowalc.zephyr.domain.node.expression.literal;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class StringLiteral extends Node implements Literal {
    private final String value;

    public StringLiteral(String value) {
        super(null, null);
        this.value = value;
    }

    public StringLiteral(TextPosition startPosition, TextPosition endPosition, String value) {
        super(startPosition, endPosition);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}