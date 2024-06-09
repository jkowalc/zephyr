package me.jkowalc.zephyr.domain.node.program;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.statement.StatementBlock;
import me.jkowalc.zephyr.domain.node.statement.VariableDefinition;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class FunctionDefinition extends Node {
    private final String name;
    private final List<VariableDefinition> parameters;
    private final StatementBlock body;
    private final String returnType;

    public FunctionDefinition(String name, List<VariableDefinition> parameters, StatementBlock body, String returnType) {
        super(null, null);
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
    }
    public FunctionDefinition(TextPosition startPosition, String name, List<VariableDefinition> parameters, StatementBlock body, String returnType) {
        super(startPosition, body.getEndPosition());
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
