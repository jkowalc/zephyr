package me.jkowalc.zephyr.domain;

import me.jkowalc.zephyr.domain.type.Type;

import java.util.List;

public interface FunctionRepresentation {
    Type getReturnType();
    List<Type> getParameterTypes();
}
