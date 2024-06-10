package me.jkowalc.zephyr.domain;

import me.jkowalc.zephyr.domain.type.StaticType;

import java.util.List;

public interface FunctionRepresentation {
    StaticType getReturnType();
    List<StaticType> getParameterTypes();
}
