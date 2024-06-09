package me.jkowalc.zephyr.analizer;

import me.jkowalc.zephyr.domain.node.program.StructDefinition;
import me.jkowalc.zephyr.domain.node.program.StructDefinitionMember;
import me.jkowalc.zephyr.domain.node.program.TypeDefinition;
import me.jkowalc.zephyr.domain.node.program.UnionDefinition;
import me.jkowalc.zephyr.domain.type.NamedType;
import me.jkowalc.zephyr.domain.type.StructType;
import me.jkowalc.zephyr.domain.type.Type;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.exception.TypeNotDefinedException;

import java.util.Map;


public class TypeChecker {
    private final Map<String, TypeDefinition> types;

    public TypeChecker(Map<String, TypeDefinition> types) {
        this.types = types;
    }

    private TypeDefinition getTypeDefinition(String name) throws TypeNotDefinedException {
        TypeDefinition typeDefinition = types.get(name);
        if (typeDefinition == null) {
            throw new TypeNotDefinedException(name);
        }
        return typeDefinition;
    }

    private Type getFieldTypeFromDefinition(StructDefinition structDefinition, String field) {
        StructDefinitionMember fieldMember = structDefinition.getMembers()
                .stream()
                .filter(member -> member.getName().equals(field))
                .findFirst().orElse(null);
        return fieldMember == null ? null : new NamedType(fieldMember.getTypeName(), false);
    }
    private Type getFieldTypeFromDefinition(UnionDefinition unionDefinition, String field) throws TypeNotDefinedException {
        Type foundType = null;
        for (String unionMemberName : unionDefinition.getTypeNames()) {
            TypeCategory unionMemberTypeCategory = TypeCategory.fromString(unionMemberName);
            if (!unionMemberTypeCategory.equals(TypeCategory.NAMED_TYPE))
                return null;
            TypeDefinition typeDefinition = getTypeDefinition(unionMemberName);
            Type fieldType;
            if(typeDefinition instanceof UnionDefinition childUnionDefinition) {
                fieldType = getFieldTypeFromDefinition(childUnionDefinition, field);
            }
            else if (typeDefinition instanceof StructDefinition structDefinition) {
                fieldType = getFieldTypeFromDefinition(structDefinition, field);
            }
            else fieldType = null;
            if(fieldType == null) return null;
            if(foundType == null) {
                foundType = fieldType;
            }
            else if(!foundType.getCategory().equals(fieldType.getCategory())) {
                return null;
            }
        }
        return foundType;
    }
    private Type getFieldTypeFromDefinition(TypeDefinition typeDefinition, String field) throws TypeNotDefinedException {
        if (typeDefinition instanceof StructDefinition structDefinition) {
            return getFieldTypeFromDefinition(structDefinition, field);
        }
        if (typeDefinition instanceof UnionDefinition unionDefinition) {
            return getFieldTypeFromDefinition(unionDefinition, field);
        }
        return null;
    }
    public Type getFieldType(Type type, String field) throws TypeNotDefinedException {
        TypeCategory category = type.getCategory();
        if (!(category.equals(TypeCategory.STRUCT) || category.equals(TypeCategory.NAMED_TYPE))) {
            return null;
        }
        if (category.equals(TypeCategory.NAMED_TYPE)) {
            String typeName = ((NamedType) type).getName();
            TypeDefinition typeDefinition = getTypeDefinition(typeName);
            return getFieldTypeFromDefinition(typeDefinition, field);
        }
        return ((StructType) type).getFields().get(field);
    }
}
