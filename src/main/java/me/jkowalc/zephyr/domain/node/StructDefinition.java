package me.jkowalc.zephyr.domain.node;

import java.util.List;

public record StructDefinition (
        String name,
        List<StructDefinitionMember> members
) implements TypeDefinition {
}
