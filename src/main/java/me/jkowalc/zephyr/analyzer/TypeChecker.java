package me.jkowalc.zephyr.analyzer;

import me.jkowalc.zephyr.domain.TypeCheckerResult;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.runtime.value.StructValue;
import me.jkowalc.zephyr.domain.type.BareStaticType;
import me.jkowalc.zephyr.domain.type.StructStaticType;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.domain.type.UnionStaticType;

import java.util.List;

public class TypeChecker {
    public static final List<BareStaticType> BUILTIN_TYPES = List.of(
            new BareStaticType(TypeCategory.INT),
            new BareStaticType(TypeCategory.FLOAT),
            new BareStaticType(TypeCategory.STRING),
            new BareStaticType(TypeCategory.BOOL)
    );
    public static boolean checkUnion(BareStaticType got, UnionStaticType expected) {
        return expected.getAlternatives().stream().anyMatch(t -> checkType(got, t) == TypeCheckerResult.SUCCESS);
    }
    public static TypeCheckerResult checkType(BareStaticType got, BareStaticType expected) {
        if(got.equals(expected)) return TypeCheckerResult.SUCCESS;
        if(got.getCategory().equals(TypeCategory.VOID)) return TypeCheckerResult.ERROR;
        if(expected.getCategory().equals(TypeCategory.VOID)) return TypeCheckerResult.ERROR;
        if(got.getCategory().equals(TypeCategory.UNION)) return TypeCheckerResult.ERROR;
        // at this point the two types must be different and neither of them is void plus got is not a union
        return switch (expected.getCategory()) {
            case STRING ->
                    (BUILTIN_TYPES.contains(got) || got.getCategory().equals(TypeCategory.STRUCT)) ? TypeCheckerResult.CONVERTIBLE : TypeCheckerResult.ERROR;
            case INT, FLOAT, BOOL ->
                    BUILTIN_TYPES.contains(got) ? TypeCheckerResult.CONVERTIBLE : TypeCheckerResult.ERROR;
            case UNION ->
                    checkUnion(got, (UnionStaticType) expected) ? TypeCheckerResult.SUCCESS : TypeCheckerResult.ERROR;
            default -> TypeCheckerResult.ERROR;
        };
    }
    private static boolean checkUnionValue(Value value, UnionStaticType expected) {
        return expected.getAlternatives().stream().anyMatch(t -> checkValue(value, t));
    }
    private static boolean checkStructValue(StructValue value, StructStaticType expected) {
        return expected.getFields().size() == value.fields().size() && expected.getFields().entrySet().stream().allMatch(e -> {
            Value fieldValue = value.getField(e.getKey());
            return fieldValue != null && checkValue(fieldValue, e.getValue());
        });
    }
    public static boolean checkValue(Value value, BareStaticType expected) {
        return switch (expected.getCategory()) {
            case UNION -> checkUnionValue(value, (UnionStaticType) expected);
            case STRUCT -> {
                if (value instanceof StructValue structValue) {
                    yield checkStructValue(structValue, (StructStaticType) expected);
                }
                yield false;
            }
            case VOID -> value.getCategory().equals(TypeCategory.VOID);
            case INT, FLOAT, STRING, BOOL -> value.getCategory().equals(expected.getCategory());
        };
    }
}
