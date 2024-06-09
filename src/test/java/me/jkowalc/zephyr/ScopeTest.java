package me.jkowalc.zephyr;

import me.jkowalc.zephyr.analizer.ScopedContext;
import me.jkowalc.zephyr.exception.VariableAlreadyDefinedException;
import me.jkowalc.zephyr.exception.VariableNotDefinedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScopeTest {
    @Test
    public void testTrivial() throws VariableAlreadyDefinedException, VariableNotDefinedException {
        ScopedContext<Integer> context = new ScopedContext<>();
        context.add("a", 1);
        assertEquals(1, context.get("a"));
    }
    @Test
    public void testDelete() throws VariableAlreadyDefinedException {
        ScopedContext<Integer> context = new ScopedContext<>();
        context.createScope();
        context.add("a", 1);
        context.rollback();
        assertThrows(VariableNotDefinedException.class, () -> context.get("a"));
    }
    @Test
    public void testScope() throws VariableAlreadyDefinedException, VariableNotDefinedException {
        ScopedContext<Integer> context = new ScopedContext<>();
        context.add("a", 1);
        context.createScope();
        context.add("b", 2);
        assertEquals(2, context.get("b"));
        assertThrows(VariableNotDefinedException.class, () -> context.get("a"));
        context.rollback();
        assertEquals(1, context.get("a"));
    }
    @Test
    public void testLocalScope() throws VariableAlreadyDefinedException, VariableNotDefinedException {
        ScopedContext<Integer> context = new ScopedContext<>();
        context.add("a", 1);
        context.createLocalScope();
        context.add("b", 2);
        assertEquals(2, context.get("b"));
        assertEquals(1, context.get("a"));
        context.rollback();
        assertThrows(VariableNotDefinedException.class, () -> context.get("b"));
        assertEquals(1, context.get("a"));
    }
    @Test
    public void testShadowing() throws VariableAlreadyDefinedException, VariableNotDefinedException {
        ScopedContext<Integer> context = new ScopedContext<>();
        context.add("a", 1);
        context.createLocalScope();
        context.add("a", 2);
        assertEquals(2, context.get("a"));
        context.rollback();
        assertEquals(1, context.get("a"));
    }
    @Test
    public void testTooManyRollbacks() {
        ScopedContext<Integer> context = new ScopedContext<>();
        context.createScope();
        context.rollback();
        assertThrows(IllegalStateException.class, context::rollback);
    }
}
