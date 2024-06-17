package me.jkowalc.zephyr.domain;

import me.jkowalc.zephyr.domain.node.program.FunctionDefinition;
import me.jkowalc.zephyr.domain.type.StaticType;

import java.util.List;

public record FunctionRepresentation(boolean builtIn, String name, List<StaticType> parameterTypes, StaticType returnType, FunctionDefinition definition) {
    public FunctionRepresentation(boolean builtIn, String name, List<StaticType> parameterTypes, StaticType returnType) {
        this(builtIn, name, parameterTypes, returnType, null);
    }
}
