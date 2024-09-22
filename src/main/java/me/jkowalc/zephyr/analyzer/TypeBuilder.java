package me.jkowalc.zephyr.analyzer;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.program.StructDefinition;
import me.jkowalc.zephyr.domain.node.program.StructDefinitionMember;
import me.jkowalc.zephyr.domain.node.program.TypeDefinition;
import me.jkowalc.zephyr.domain.node.program.UnionDefinition;
import me.jkowalc.zephyr.domain.type.*;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.exception.analizer.DuplicateStructFieldException;
import me.jkowalc.zephyr.exception.analizer.RecursiveTypeDefinitionException;
import me.jkowalc.zephyr.exception.analizer.TypeAlreadyDefinedException;
import me.jkowalc.zephyr.exception.analizer.TypeNotDefinedException;
import me.jkowalc.zephyr.exception.ZephyrInternalException;
import me.jkowalc.zephyr.util.SimpleMap;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TypeBuilder {
    private final Map<String, TypeDefinition> typeDefinitionMap;
    private final List<String> visited = new ArrayList<>();

    @Getter
    private final Map<String, BareStaticType> types = new HashMap<>(
            Map.of(
                    "int", new BareStaticType(TypeCategory.INT),
                    "float", new BareStaticType(TypeCategory.FLOAT),
                    "string", new BareStaticType(TypeCategory.STRING),
                    "bool", new BareStaticType(TypeCategory.BOOL)
            )
    );

    public TypeBuilder(Map<String, TypeDefinition> types) {
        this.typeDefinitionMap = types;
    }
    public TypeBuilder build() throws ZephyrException {
        for(Map.Entry<String, TypeDefinition> entry : typeDefinitionMap.entrySet()) {
            if(this.types.containsKey(entry.getKey())) throw new TypeAlreadyDefinedException(entry.getValue());
            visited.add(entry.getKey());
            this.types.put(entry.getKey(), buildBareTypeFromDefinition(entry.getValue()));
            visited.removeLast();
        }
        return this;
    }
    private TypeDefinition getTypeDefinition(String name, TextPosition position) throws ZephyrException {
        TypeDefinition typeDefinition = typeDefinitionMap.get(name);
        if (typeDefinition == null) {
            throw new TypeNotDefinedException(name, position);
        }
        return typeDefinition;
    }
    private BareStaticType buildBareTypeFromName(String name, TextPosition position) throws ZephyrException {
        if(visited.contains(name)) {
            throw new RecursiveTypeDefinitionException(name, position);
        }
        if(types.containsKey(name)) {
            return types.get(name);
        }
        visited.add(name);
        BareStaticType type = buildBareTypeFromDefinition(getTypeDefinition(name, position));
        visited.removeLast();
        return type;
    }
    private Map<String, BareStaticType> deriveFieldsFromDefinition(StructDefinition structDefinition) throws ZephyrException {
        Map<String, BareStaticType> fields = new SimpleMap<>();
        for(StructDefinitionMember member : structDefinition.getMembers()) {
            if(fields.containsKey(member.getName())) {
                throw new DuplicateStructFieldException(member.getName(), member.getStartPosition());
            }
            fields.put(member.getName(), buildBareTypeFromName(member.getTypeName(), member.getStartPosition()));
        }
        return fields;
    }
    private List<BareStaticType> deriveAlternativesFromDefinition(UnionDefinition unionDefinition) throws ZephyrException {
        List<BareStaticType> alternatives = new ArrayList<>();
        for(String unionMemberName : unionDefinition.getTypeNames()) {
            alternatives.add(buildBareTypeFromName(unionMemberName, unionDefinition.getStartPosition()));
        }
        return alternatives;
    }
    private BareStaticType buildBareTypeFromDefinition(TypeDefinition typeDefinition) throws ZephyrException {
        if(typeDefinition instanceof StructDefinition structDefinition) {
            return new StructStaticType(deriveFieldsFromDefinition(structDefinition));
        } else if(typeDefinition instanceof UnionDefinition unionDefinition) {
            return new UnionStaticType(deriveAlternativesFromDefinition(unionDefinition));
        } else {
            throw new ZephyrInternalException();
        }
    }
}
