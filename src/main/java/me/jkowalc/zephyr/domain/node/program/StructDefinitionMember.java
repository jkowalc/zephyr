package me.jkowalc.zephyr.domain.node.program;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class StructDefinitionMember extends Node {
    private final String name;
    private final String typeName;

    public StructDefinitionMember(String name, String typeName) {
        super(null, null);
        this.name = name;
        this.typeName = typeName;
    }

    public StructDefinitionMember(TextPosition startPosition, TextPosition endPosition, String name, String typeName) {
        super(startPosition, endPosition);
        this.name = name;
        this.typeName = typeName;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
