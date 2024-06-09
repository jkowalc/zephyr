package me.jkowalc.zephyr.domain.runtime;

import me.jkowalc.zephyr.domain.FunctionRepresentation;
import me.jkowalc.zephyr.domain.type.Type;

import java.util.List;

public record BuiltinFunctionSignature(List<Type> parameterTypes, Type returnType) implements FunctionRepresentation {
    @Override
    public Type getReturnType() {
        return returnType;
    }

    @Override
    public List<Type> getParameterTypes() {
        return parameterTypes;
    }
}

