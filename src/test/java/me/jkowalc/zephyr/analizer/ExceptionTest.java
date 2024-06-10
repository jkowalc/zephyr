package me.jkowalc.zephyr.analizer;

import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.literal.IntegerLiteral;
import me.jkowalc.zephyr.domain.node.program.FunctionDefinition;
import me.jkowalc.zephyr.domain.node.program.Program;
import me.jkowalc.zephyr.domain.node.statement.ReturnStatement;
import me.jkowalc.zephyr.domain.node.statement.StatementBlock;
import me.jkowalc.zephyr.domain.node.statement.VariableDefinition;
import me.jkowalc.zephyr.exception.analizer.TypeNotDefinedAnalizerException;
import me.jkowalc.zephyr.exception.analizer.VariableAlreadyDefinedAnalizerException;
import me.jkowalc.zephyr.exception.analizer.VariableNotDefinedAnalizerException;
import me.jkowalc.zephyr.exception.analizer.VariableNotInitializedException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionTest {
    private final StaticAnalizer analizer = new StaticAnalizer();

    private Program programFromMainBlock(StatementBlock block) {
        return new Program(
                Map.ofEntries(
                        entry("main", new FunctionDefinition(
                                "main",
                                List.of(),
                                block,
                                null
                        ))
                ),
                Map.of()
        );
    }

    @Test
    public void testVariableNotDefined() {
        Program program = programFromMainBlock(new StatementBlock(
                List.of(
                        new ReturnStatement(new VariableReference("a"))
                )
        ));
        assertThrows(VariableNotDefinedAnalizerException.class, () -> program.accept(analizer));
    }
    @Test
    public void testVariableNotInitialized() {
        Program program = programFromMainBlock(new StatementBlock(
                List.of(
                        new VariableDefinition("a", "int", false, false, null)
                )
        ));
        assertThrows(VariableNotInitializedException.class, () -> program.accept(analizer));
    }
    @Test
    public void testVariableAlreadyDefined() {
        Program program = programFromMainBlock(new StatementBlock(
                List.of(
                        new VariableDefinition("a", "int", false, false, new IntegerLiteral(1)),
                        new VariableDefinition("a", "int", false, false, new IntegerLiteral(1))
                )
        ));
        assertThrows(VariableAlreadyDefinedAnalizerException.class, () -> program.accept(analizer));
    }
    @Test
    public void testTypeNotDefined() {
        Program program = programFromMainBlock(new StatementBlock(
                List.of(
                        new VariableDefinition("a", "not_defined", false, false, new IntegerLiteral(1))
                )
        ));
        assertThrows(TypeNotDefinedAnalizerException.class, () -> program.accept(analizer));
    }
}
