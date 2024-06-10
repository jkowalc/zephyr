package me.jkowalc.zephyr.analizer;

import me.jkowalc.zephyr.domain.TypeCheckerResult;
import me.jkowalc.zephyr.domain.node.program.StructDefinition;
import me.jkowalc.zephyr.domain.node.program.StructDefinitionMember;
import me.jkowalc.zephyr.domain.node.program.TypeDefinition;
import me.jkowalc.zephyr.domain.node.program.UnionDefinition;
import me.jkowalc.zephyr.domain.type.NamedStaticType;
import me.jkowalc.zephyr.domain.type.StructStaticType;
import me.jkowalc.zephyr.domain.type.StaticType;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.exception.TypeNotDefinedException;
import me.jkowalc.zephyr.exception.ZephyrInternalException;

import java.util.List;
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

    private StaticType getFieldTypeFromDefinition(StructDefinition structDefinition, String field) {
        StructDefinitionMember fieldMember = structDefinition.getMembers()
                .stream()
                .filter(member -> member.getName().equals(field))
                .findFirst().orElse(null);
        return fieldMember == null ? null : new NamedStaticType(fieldMember.getTypeName(), false, false);
    }
    private StaticType getFieldTypeFromDefinition(UnionDefinition unionDefinition, String field) throws TypeNotDefinedException {
        StaticType foundType = null;
        for (String unionMemberName : unionDefinition.getTypeNames()) {
            TypeCategory unionMemberTypeCategory = TypeCategory.fromString(unionMemberName);
            if (!unionMemberTypeCategory.equals(TypeCategory.NAMED_TYPE))
                return null;
            TypeDefinition typeDefinition = getTypeDefinition(unionMemberName);
            StaticType fieldType;
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
    private StaticType getFieldTypeFromDefinition(TypeDefinition typeDefinition, String field) throws TypeNotDefinedException {
        if (typeDefinition instanceof StructDefinition structDefinition) {
            return getFieldTypeFromDefinition(structDefinition, field);
        }
        if (typeDefinition instanceof UnionDefinition unionDefinition) {
            return getFieldTypeFromDefinition(unionDefinition, field);
        }
        return null;
    }
    public StaticType getFieldType(StaticType type, String field) throws TypeNotDefinedException {
        TypeCategory category = type.getCategory();
        if (!(category.equals(TypeCategory.STRUCT) || category.equals(TypeCategory.NAMED_TYPE))) {
            return null;
        }
        if (category.equals(TypeCategory.NAMED_TYPE)) {
            String typeName = ((NamedStaticType) type).getName();
            TypeDefinition typeDefinition = getTypeDefinition(typeName);
            return getFieldTypeFromDefinition(typeDefinition, field);
        }
        return ((StructStaticType) type).getFields().get(field);
    }
    private static final List<TypeCategory> BUILTIN_TYPES = List.of(
            TypeCategory.INT,
            TypeCategory.FLOAT,
            TypeCategory.STRING,
            TypeCategory.BOOL
    );
    private TypeCheckerResult checkTypeConversionNamed(StaticType got, String typeName) throws TypeNotDefinedException {
        TypeDefinition typeDefinition = getTypeDefinition(typeName);
        // TODO: implement
        return TypeCheckerResult.SUCCESS;
    }
    public TypeCheckerResult checkType(StaticType got, StaticType expected) throws TypeNotDefinedException {
        TypeCategory gotCategory = got.getCategory();
        TypeCategory expectedCategory = expected.getCategory();
        if(gotCategory.equals(expectedCategory)) {
            return TypeCheckerResult.SUCCESS;
        }
        return switch (expectedCategory) {
            case STRUCT -> throw new ZephyrInternalException();
            case INT, FLOAT, STRING, BOOL ->
                    BUILTIN_TYPES.contains(gotCategory) ? TypeCheckerResult.CONVERTIBLE : TypeCheckerResult.ERROR;
            case ANY_BUILTIN ->
                    BUILTIN_TYPES.contains(gotCategory) ? TypeCheckerResult.SUCCESS : TypeCheckerResult.ERROR;
            case NAMED_TYPE -> checkTypeConversionNamed(got, ((NamedStaticType) expected).getName());
            default -> TypeCheckerResult.ERROR;
        };
    }
}
