package me.jkowalc.zephyr.analizer;

import me.jkowalc.zephyr.domain.TypeCheckerResult;
import me.jkowalc.zephyr.domain.type.BareStaticType;
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
}
