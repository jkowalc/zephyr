package me.jkowalc.zephyr.interpreter;

import me.jkowalc.zephyr.VoidTextPrinter;
import me.jkowalc.zephyr.domain.node.expression.literal.IntegerLiteral;
import me.jkowalc.zephyr.domain.node.program.FunctionDefinition;
import me.jkowalc.zephyr.domain.node.program.Program;
import me.jkowalc.zephyr.domain.node.program.TypeDefinition;
import me.jkowalc.zephyr.domain.node.statement.ReturnStatement;
import me.jkowalc.zephyr.domain.node.statement.StatementBlock;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.runtime.value.IntegerValue;
import me.jkowalc.zephyr.exception.ZephyrException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterFunctionTest {
    private Value returnValue;
    private Map<String, TypeDefinition> types = Map.of();
    private static final FunctionDefinition DEFAULT_MAIN = new FunctionDefinition("main", List.of(), new StatementBlock(List.of()), null);
    private void executeFunction(FunctionDefinition function, List<Value> arguments) throws ZephyrException {
        Program program = new Program(Map.of(function.getName(), function, "main", DEFAULT_MAIN), types);
        Interpreter interpreter = new Interpreter(program, new VoidTextPrinter());
        returnValue = interpreter.executeFunction(function.getName(), arguments);
    }
    @Test
    public void testBasicFunction() throws ZephyrException {
        FunctionDefinition function = new FunctionDefinition("test", List.of(), new StatementBlock(List.of(new ReturnStatement(new IntegerLiteral(1)))), "int");
        executeFunction(function, List.of());
        assertEquals(new IntegerValue(1), returnValue);
    }
}
