package me.jkowalc.zephyr.domain.node;

import java.util.Map;

public record Program (
        Map<String, FunctionDefinition> functions,
        Map<String, TypeDefinition> types
) {
}