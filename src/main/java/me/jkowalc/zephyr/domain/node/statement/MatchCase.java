package me.jkowalc.zephyr.domain.node.statement;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public final class MatchCase extends Node {
    private final String pattern;
    private final String variableName;
    private final StatementBlock body;

    public MatchCase(String pattern, String variableName, StatementBlock body) {
        super(null, null);
        this.pattern = pattern;
        this.variableName = variableName;
        this.body = body;
    }
    public MatchCase(TextPosition startPosition, String pattern, String variableName, StatementBlock body) {
        super(startPosition, body.getEndPosition());
        this.pattern = pattern;
        this.variableName = variableName;
        this.body = body;
    }
}
