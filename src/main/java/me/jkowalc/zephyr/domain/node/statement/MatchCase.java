package me.jkowalc.zephyr.domain.node.statement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class MatchCase extends Node {
    private final String pattern;
    private final String variableName;
    private final StatementBlock body;

    public MatchCase(String pattern, String variableName, StatementBlock body) {
        super(null);
        this.pattern = pattern;
        this.variableName = variableName;
        this.body = body;
    }
    public MatchCase(TextPosition startPosition, String pattern, String variableName, StatementBlock body) {
        super(startPosition);
        this.pattern = pattern;
        this.variableName = variableName;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
