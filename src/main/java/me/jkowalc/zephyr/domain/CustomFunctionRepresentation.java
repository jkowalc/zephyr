package me.jkowalc.zephyr.domain;

import me.jkowalc.zephyr.domain.node.program.FunctionDefinition;
import me.jkowalc.zephyr.domain.type.Type;
import me.jkowalc.zephyr.domain.type.TypeCategory;

import java.util.List;


public record CustomFunctionRepresentation(FunctionDefinition functionDefinition) implements FunctionRepresentation {
    @Override
    public Type getReturnType() {
        return functionDefinition.getReturnType() == null
                ? new Type(TypeCategory.VOID)
                : new Type(TypeCategory.fromString(functionDefinition.getReturnType()));
    }

    @Override
    public List<Type> getParameterTypes() {
        return functionDefinition.getParameters().stream().map(parameter ->
            new Type(TypeCategory.fromString(parameter.getTypeName()), parameter.isMutable(), parameter.isReference())
        ).toList();
    }
}
