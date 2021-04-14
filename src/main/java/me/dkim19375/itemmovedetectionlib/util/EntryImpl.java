package me.dkim19375.itemmovedetectionlib.util;

import java.util.Map;
import java.util.Objects;

public class EntryImpl<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public EntryImpl(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        final V before = this.value;
        this.value = value;
        return before;
    }

    @Override
    public String toString() {
        return "EntryImpl{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntryImpl<?, ?> entry = (EntryImpl<?, ?>) o;
        return key.equals(entry.key) && value.equals(entry.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
