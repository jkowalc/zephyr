package me.jkowalc.zephyr.domain.node.expression.literal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class StructLiteral extends Node implements Literal {
    private final List<StructLiteralMember> fields;

    public StructLiteral(List<StructLiteralMember> fields) {
        super(null, null);
        this.fields = fields;
    }

    public StructLiteral(TextPosition startPosition, TextPosition endPosition, List<StructLiteralMember> fields) {
        super(startPosition, endPosition);
        this.fields = fields;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}