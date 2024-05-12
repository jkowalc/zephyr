package me.jkowalc.zephyr.domain.node.program;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class Program extends Node {
    private final Map<String, FunctionDefinition> functions;
    private final Map<String, TypeDefinition> types;

    public Program(Map<String, FunctionDefinition> functions, Map<String, TypeDefinition> types) {
        super(null, null);
        this.functions = functions;
        this.types = types;
    }
    public Program(TextPosition startPosition, TextPosition endPosition, Map<String, FunctionDefinition> functions, Map<String, TypeDefinition> types) {
        super(startPosition, endPosition);
        this.functions = functions;
        this.types = types;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}