package me.jkowalc.zephyr.analizer;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.program.StructDefinition;
import me.jkowalc.zephyr.domain.node.program.StructDefinitionMember;
import me.jkowalc.zephyr.domain.node.program.TypeDefinition;
import me.jkowalc.zephyr.domain.node.program.UnionDefinition;
import me.jkowalc.zephyr.domain.type.*;
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
    public TypeBuilder build() throws TypeAlreadyDefinedException, TypeNotDefinedException {
        for(Map.Entry<String, TypeDefinition> entry : typeDefinitionMap.entrySet()) {
            if(this.types.containsKey(entry.getKey())) throw new TypeAlreadyDefinedException(entry.getValue());
            this.types.put(entry.getKey(), buildBareTypeFromDefinition(entry.getValue()));
        }
        return this;
    }
    private TypeDefinition getTypeDefinition(String name, TextPosition position) throws TypeNotDefinedException {
        TypeDefinition typeDefinition = typeDefinitionMap.get(name);
        if (typeDefinition == null) {
            throw new TypeNotDefinedException(name, position);
        }
        return typeDefinition;
    }
    private BareStaticType buildBareTypeFromName(String name, TextPosition position) throws TypeNotDefinedException {
        if(types.containsKey(name)) {
            return types.get(name);
        }
        return buildBareTypeFromDefinition(getTypeDefinition(name, position));
    }
    private Map<String, BareStaticType> deriveFieldsFromDefinition(StructDefinition structDefinition) throws TypeNotDefinedException {
        Map<String, BareStaticType> fields = new SimpleMap<>();
        for(StructDefinitionMember member : structDefinition.getMembers()) {
            fields.put(member.getName(), buildBareTypeFromName(member.getTypeName(), member.getStartPosition()));
        }
        return fields;
    }
    private List<BareStaticType> deriveAlternativesFromDefinition(UnionDefinition unionDefinition) throws TypeNotDefinedException {
        List<BareStaticType> alternatives = new ArrayList<>();
        for(String unionMemberName : unionDefinition.getTypeNames()) {
            alternatives.add(buildBareTypeFromName(unionMemberName, unionDefinition.getStartPosition()));
        }
        return alternatives;
    }
    private BareStaticType buildBareTypeFromDefinition(TypeDefinition typeDefinition) throws TypeNotDefinedException {
        if(typeDefinition instanceof StructDefinition structDefinition) {
            return new StructStaticType(deriveFieldsFromDefinition(structDefinition));
        } else if(typeDefinition instanceof UnionDefinition unionDefinition) {
            return new UnionStaticType(deriveAlternativesFromDefinition(unionDefinition));
        } else {
            throw new ZephyrInternalException();
        }
    }
}
