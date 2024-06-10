package me.jkowalc.zephyr;

import me.jkowalc.zephyr.domain.runtime.BuiltinFunctionSignature;
import me.jkowalc.zephyr.domain.type.StaticType;
import me.jkowalc.zephyr.domain.type.TypeCategory;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class BuiltinFunctionManager {
    public static final Map<String, BuiltinFunctionSignature> BUILTIN_FUNCTIONS = Map.ofEntries(
            entry("print", new BuiltinFunctionSignature(
                    List.of(new StaticType(TypeCategory.STRING)),
                    new StaticType(TypeCategory.VOID))),
            entry("to_string", new BuiltinFunctionSignature(
                    List.of(new StaticType(TypeCategory.ANY_BUILTIN)),
                    new StaticType(TypeCategory.STRING))),
            entry("to_int", new BuiltinFunctionSignature(
                    List.of(new StaticType(TypeCategory.ANY_BUILTIN)),
                    new StaticType(TypeCategory.INT))),
            entry("to_float", new BuiltinFunctionSignature(
                    List.of(new StaticType(TypeCategory.ANY_BUILTIN)),
                    new StaticType(TypeCategory.FLOAT))),
            entry("to_bool", new BuiltinFunctionSignature(
                    List.of(new StaticType(TypeCategory.ANY_BUILTIN)),
                    new StaticType(TypeCategory.BOOL)))
    );
}
