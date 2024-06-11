package me.jkowalc.zephyr;

import me.jkowalc.zephyr.domain.runtime.BuiltinFunctionSignature;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.runtime.value.StringValue;
import me.jkowalc.zephyr.domain.type.StaticType;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.exception.ConversionException;
import me.jkowalc.zephyr.exception.ZephyrInternalException;
import me.jkowalc.zephyr.interpreter.TypeConverter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class BuiltinFunctionManager {
    public static final Map<String, BuiltinFunctionSignature> BUILTIN_FUNCTIONS = Map.ofEntries(
            entry("print", new BuiltinFunctionSignature(
                    "print",
                    List.of(new StaticType(TypeCategory.STRING)),
                    new StaticType(TypeCategory.VOID))),
            entry("printLn", new BuiltinFunctionSignature(
                    "printLn",
                    List.of(new StaticType(TypeCategory.STRING)),
                    new StaticType(TypeCategory.VOID))),
            entry("to_string", new BuiltinFunctionSignature(
                    "to_string",
                    List.of(new StaticType(TypeCategory.ANY_BUILTIN)),
                    new StaticType(TypeCategory.STRING))),
            entry("to_int", new BuiltinFunctionSignature(
                    "to_int",
                    List.of(new StaticType(TypeCategory.ANY_BUILTIN)),
                    new StaticType(TypeCategory.INT))),
            entry("to_float", new BuiltinFunctionSignature(
                    "to_float",
                    List.of(new StaticType(TypeCategory.ANY_BUILTIN)),
                    new StaticType(TypeCategory.FLOAT))),
            entry("to_bool", new BuiltinFunctionSignature(
                    "to_bool",
                    List.of(new StaticType(TypeCategory.ANY_BUILTIN)),
                    new StaticType(TypeCategory.BOOL)))
    );
    private final OutputStreamWriter outputStream;
    public BuiltinFunctionManager(OutputStreamWriter outputStream) {
        this.outputStream = outputStream;
    }
    public Value execute(String name, List<Value> arguments) throws ConversionException {
        return switch (name) {
            case "print" -> {
                StringValue value = (StringValue) arguments.getFirst();
                try {
                    outputStream.write(value.value());
                    outputStream.flush();
                } catch(IOException e) {
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
                } catch(IOException e) {
                    e.printStackTrace();
                }
                yield null;
            }
            case "to_string" -> TypeConverter.convert(arguments.getFirst(), TypeCategory.STRING);
            case "to_int" -> TypeConverter.convert(arguments.getFirst(), TypeCategory.INT);
            case "to_float" -> TypeConverter.convert(arguments.getFirst(), TypeCategory.FLOAT);
            case "to_bool" -> TypeConverter.convert(arguments.getFirst(), TypeCategory.BOOL);
            default -> throw new ZephyrInternalException();
        };
    }
}
