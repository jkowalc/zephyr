package me.jkowalc.zephyr.analyzer;

import me.jkowalc.zephyr.exception.scope.VariableNotDefinedScopeException;

import java.util.ArrayList;
import java.util.List;

public class ScopedContext<T> {
    private final List<Scope<T>> scopes = new ArrayList<>();
    public ScopedContext() {
        scopes.add(new Scope<>(null, null));
    }

    public void rollback() {
        if(scopes.size() < 2) throw new IllegalStateException("Cannot rollback root scope");
        scopes.removeLast();
    }
    public T get(String name) throws VariableNotDefinedScopeException {
        return scopes.getLast().get(name);
    }
    public void set(String name, T value) {
        scopes.getLast().set(name, value);
    }
    public void createScope() {
        scopes.add(new Scope<>(null, null));
    }
    public void createLocalScope() {
        scopes.add(new Scope<>(scopes.getLast(), null));
    }
}
