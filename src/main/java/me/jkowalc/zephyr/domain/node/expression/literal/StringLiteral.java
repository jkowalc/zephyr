package me.jkowalc.zephyr.domain.node.expression.literal;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class StringLiteral extends Node implements Literal {
    private final String value;

    public StringLiteral(String value) {
        super(null);
        this.value = value;
    }

    public StringLiteral(TextPosition startPosition, String value) {
        super(startPosition);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}