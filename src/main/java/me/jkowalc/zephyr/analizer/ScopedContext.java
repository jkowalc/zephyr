package me.jkowalc.zephyr.analizer;

import me.jkowalc.zephyr.exception.VariableAlreadyDefinedException;
import me.jkowalc.zephyr.exception.VariableNotDefinedException;

import java.util.ArrayList;
import java.util.List;

public class ScopedContext<T> {
    private final List<Scope<T>> scopes = new ArrayList<>();
    public ScopedContext() {
        scopes.add(new Scope<>());
    }

    public void rollback() {
        if(scopes.size() < 2) throw new IllegalStateException("Cannot rollback root scope");
        scopes.removeLast();
    }
    public void add(String name, T value) throws VariableAlreadyDefinedException {
        scopes.getLast().add(name, value);
    }
    public T get(String name) throws VariableNotDefinedException {
        return scopes.getLast().get(name);
    }
    public void createScope() {
        scopes.add(new Scope<>());
    }
    public void createLocalScope() {
        scopes.add(new Scope<>(scopes.isEmpty() ? null : scopes.getFirst()));
    }
}
