package me.jkowalc.zephyr.domain.node.program;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.statement.StatementBlock;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class FunctionDefinition extends Node {
    private final String name;
    private final StatementBlock block;

    public FunctionDefinition(String name, StatementBlock block) {
        super(null, null);
        this.name = name;
        this.block = block;
    }
    public FunctionDefinition(TextPosition startPosition, TextPosition endPosition, String name, StatementBlock block) {
        super(startPosition, endPosition);
        this.name = name;
        this.block = block;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
