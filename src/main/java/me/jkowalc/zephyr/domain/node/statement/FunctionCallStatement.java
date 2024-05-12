package me.jkowalc.zephyr.domain.node.statement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.util.ASTVisitor;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class FunctionCallStatement extends Node implements Statement {
    private final FunctionCall functionCall;
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public FunctionCallStatement(FunctionCall functionCall) {
        super(functionCall.getStartPosition(), functionCall.getEndPosition());
        this.functionCall = functionCall;
    }
}
