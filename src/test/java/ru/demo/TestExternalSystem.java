package ru.demo;

import org.junit.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

public class TestExternalSystem implements Consumer<Key> {

    private ConcurrentHashMap<Key, AtomicBoolean> executingKeys = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Key, LongAdder> handledKeys = new ConcurrentHashMap<>();

    @Override
    public void accept(Key key) {

        var isExecuting = executingKeys.computeIfAbsent(key, key1 -> new AtomicBoolean());
        handledKeys.computeIfAbsent(key, k -> new LongAdder()).increment();

        if (isExecuting.get()) {
            Assert.fail("Concurrent exec");
        }

        isExecuting.set(true);

        try {
            Thread.currentThread().sleep((long) (Math.random() * 3));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isExecuting.set(false);
    }

    public Map<Key, LongAdder> getHandledKeys() {
        return handledKeys;
    }
}
