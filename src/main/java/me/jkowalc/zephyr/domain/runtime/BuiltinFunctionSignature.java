package me.jkowalc.zephyr.domain.runtime;

import me.jkowalc.zephyr.domain.FunctionRepresentation;
import me.jkowalc.zephyr.domain.type.StaticType;

import java.util.List;

public record BuiltinFunctionSignature(String name, List<StaticType> parameterTypes, StaticType returnType) implements FunctionRepresentation {
    @Override
    public StaticType getReturnType() {
        return returnType;
    }

    @Override
    public List<StaticType> getParameterTypes() {
        return parameterTypes;
    }
}

