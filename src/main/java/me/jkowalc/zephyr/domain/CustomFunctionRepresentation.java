package me.jkowalc.zephyr.domain;

import me.jkowalc.zephyr.domain.node.program.FunctionDefinition;
import me.jkowalc.zephyr.domain.type.StaticType;
import me.jkowalc.zephyr.domain.type.TypeCategory;

import java.util.List;


public record CustomFunctionRepresentation(FunctionDefinition functionDefinition) implements FunctionRepresentation {
    @Override
    public StaticType getReturnType() {
        return functionDefinition.getReturnType() == null
                ? new StaticType(TypeCategory.VOID)
                : new StaticType(TypeCategory.fromString(functionDefinition.getReturnType()));
    }

    @Override
    public List<StaticType> getParameterTypes() {
        return functionDefinition.getParameters().stream().map(parameter ->
            new StaticType(TypeCategory.fromString(parameter.getTypeName()), parameter.isMutable(), parameter.isReference())
        ).toList();
    }
}
