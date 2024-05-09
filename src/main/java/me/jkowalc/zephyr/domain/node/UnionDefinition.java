package me.jkowalc.zephyr.domain.node;

import lombok.Getter;

import java.util.List;

public record UnionDefinition(String name, List<String> typeNames) implements TypeDefinition {
}
