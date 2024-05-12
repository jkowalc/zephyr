package me.jkowalc.zephyr.util;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleMap<K, T> implements Map<K, T> {
    private record SimpleMapEntry<K, T> (K key, T value) implements Entry<K, T> {
        @Override
        public K getKey() {
            return key;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public T setValue(T value) {
            throw new UnsupportedOperationException();
        }
    }

    private final List<SimpleMapEntry<K, T>> entries = new ArrayList<>();

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return entries.stream().anyMatch(entry -> entry.key.equals(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return entries.stream().anyMatch(entry -> entry.value.equals(value));
    }

    @Override
    public T get(Object key) {
        return entries.stream()
                .filter(entry -> entry.key.equals(key))
                .map(SimpleMapEntry::value)
                .findFirst()
                .orElse(null);
    }

    @Override
    public T put(K key, T value) {
        if(containsKey(key)){
            T oldValue = get(key);
            entries.removeIf(entry -> entry.key.equals(key));
            entries.add(new SimpleMapEntry<>(key, value));
            return oldValue;
        }
        entries.add(new SimpleMapEntry<>(key, value));
        return null;
    }

    @Override
    public T remove(Object key) {
        T value = get(key);
        entries.removeIf(entry -> entry.key.equals(key));
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends T> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public Set<K> keySet() {
        return entries.stream()
                .map(SimpleMapEntry::key)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<T> values() {
        return entries.stream()
                .map(SimpleMapEntry::value)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Entry<K, T>> entrySet() {
        return entries.stream()
                .map(entry -> (Entry<K, T>) entry)
                .collect(Collectors.toSet());
    }
}
