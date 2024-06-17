package me.jkowalc.zephyr.domain.node.program;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class StructDefinition extends Node implements TypeDefinition {
    private final String name;
    private final List<StructDefinitionMember> members;

    public StructDefinition(String name, List<StructDefinitionMember> members) {
        super(null, null);
        this.name = name;
        this.members = members;
    }

    public StructDefinition(TextPosition startPosition, TextPosition endPosition, String name, List<StructDefinitionMember> members) {
        super(startPosition, endPosition);
        this.name = name;
        this.members = members;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
