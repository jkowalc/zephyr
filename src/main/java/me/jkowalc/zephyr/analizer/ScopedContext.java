package me.jkowalc.zephyr.analizer;

import me.jkowalc.zephyr.exception.VariableAlreadyDefinedException;
import me.jkowalc.zephyr.exception.VariableNotDefinedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScopedContext<T> {
    private final List<Scope<T>> scopes = new ArrayList<>();
    public ScopedContext() {
        scopes.add(new Scope<>(null, null));
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
        scopes.add(new Scope<>(null, null));
    }
    public void createScope(Map<String, T> values) {
        scopes.add(new Scope<>(null, values));
    }
    public void createLocalScope() {
        scopes.add(new Scope<>(scopes.getFirst(), null));
    }
    public void createLocalScope(Map<String, T> values) {
        scopes.add(new Scope<>(scopes.getFirst(), values));
    }
}
