package me.jkowalc.zephyr.domain.node.statement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class MatchStatement extends Node implements Statement {
    private final Expression expression;
    private final List<MatchCase> cases;

    public MatchStatement(Expression expression, List<MatchCase> cases) {
        super(null, null);
        this.expression = expression;
        this.cases = cases;
    }

    public MatchStatement(TextPosition startPosition, TextPosition endPosition, Expression expression, List<MatchCase> cases) {
        super(startPosition, endPosition);
        this.expression = expression;
        this.cases = cases;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
