package ru.demo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class KeyHandler {

    private final Consumer<Key> externalSystem;

    private final ConcurrentMap<Key, Consumer<Key>> handlingKeys = new ConcurrentHashMap<>();

    public KeyHandler(Consumer<Key> externalSystem) {
        this.externalSystem = externalSystem;
    }

    /**
     * Handle only unique keys
     * @param key
     */
    public void handleKey(final Key key) {

        Consumer<Key> handle = (k) -> externalSystem.accept(key);

        while (handlingKeys.putIfAbsent(key, handle) != handle);

        handle.accept(key);

        handlingKeys.remove(key);


    }
}
