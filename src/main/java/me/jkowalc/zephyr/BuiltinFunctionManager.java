package me.jkowalc.zephyr;

import me.jkowalc.zephyr.domain.FunctionRepresentation;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.runtime.value.StringValue;
import me.jkowalc.zephyr.domain.type.StaticType;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.exception.type.ConversionTypeException;
import me.jkowalc.zephyr.exception.ZephyrInternalException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class BuiltinFunctionManager {
    public static final Map<String, FunctionRepresentation> BUILTIN_FUNCTIONS = Map.ofEntries(
            entry("print", new FunctionRepresentation(
                    true,
                    "print",
                    List.of(StaticType.fromCategory(TypeCategory.STRING)),
                    StaticType.fromCategory(TypeCategory.VOID))),
            entry("printLn", new FunctionRepresentation(
                    true,
                    "printLn",
                    List.of(StaticType.fromCategory(TypeCategory.STRING)),
                    StaticType.fromCategory(TypeCategory.VOID))),
            entry("to_string", new FunctionRepresentation(
                    true,
                    "to_string",
                    List.of(StaticType.fromCategory(TypeCategory.STRING)),
                    StaticType.fromCategory(TypeCategory.STRING))),
            entry("to_int", new FunctionRepresentation(
                    true,
                    "to_int",
                    List.of(StaticType.fromCategory(TypeCategory.INT)),
                    StaticType.fromCategory(TypeCategory.INT))),
            entry("to_float", new FunctionRepresentation(
                    true,
                    "to_float",
                    List.of(StaticType.fromCategory(TypeCategory.FLOAT)),
                    StaticType.fromCategory(TypeCategory.FLOAT))),
            entry("to_bool", new FunctionRepresentation(
                    true,
                    "to_bool",
                    List.of(StaticType.fromCategory(TypeCategory.BOOL)),
                    StaticType.fromCategory(TypeCategory.BOOL)))
    );
    private final OutputStreamWriter outputStream;

    public BuiltinFunctionManager(OutputStreamWriter outputStream) {
        this.outputStream = outputStream;
    }

    public Value execute(String name, List<Value> arguments) {
        return switch (name) {
            case "print" -> {
                StringValue value = (StringValue) arguments.getFirst();
                try {
                    outputStream.write(value.value());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                yield null;
            }
            case "printLn" -> {
                StringValue value = (StringValue) arguments.getFirst();
                try {
                    outputStream.write(value.value());
                    outputStream.write("\n");
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                yield null;
            }
            case "to_string", "to_int", "to_float", "to_bool" -> arguments.getFirst();
            default -> throw new ZephyrInternalException();
        };
    }
}
