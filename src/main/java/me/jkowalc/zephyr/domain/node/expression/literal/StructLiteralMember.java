package me.jkowalc.zephyr.domain.node.expression.literal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public class StructLiteralMember extends Node {
    private final String fieldName;
    private final Literal fieldValue;

    public StructLiteralMember(String fieldName, Literal fieldValue) {
        super(null, null);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    public StructLiteralMember(TextPosition startPosition, String fieldName, Literal fieldValue) {
        super(startPosition, fieldValue.getEndPosition());
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
