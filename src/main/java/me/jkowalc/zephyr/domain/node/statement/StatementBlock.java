package me.jkowalc.zephyr.domain.node.statement;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

@Getter
public final class StatementBlock extends Node implements Statement {
    private final List<Statement> statements;

    public StatementBlock(List<Statement> statements) {
        super(null, null);
        this.statements = statements;
    }

    public StatementBlock(TextPosition startPosition, TextPosition endPosition, List<Statement> statements) {
        super(startPosition, endPosition);
        this.statements = statements;
    }

}
