package ru.demo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

import static java.util.stream.IntStream.range;

public class TestHandler {

    private final static int numberOfThreads = 5;

    private final static int sampleSize = 25_000;

    private static ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    @After
    public void stop() {
        executorService.shutdownNow();
    }

    @Test
    public void testHandler() {

        var externalSystem = new TestExternalSystem();
        KeyHandler handler = new KeyHandler(externalSystem);

        var futures = range(0, numberOfThreads).mapToObj(i -> (Runnable) () -> {
            for (int j = 0; j < sampleSize; j++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                Key key = new Key((long) (Math.random() * numberOfThreads));
                handler.handleKey(key);
            }
        }).map(task -> CompletableFuture.runAsync(task, executorService))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();

        var handledKeys = externalSystem.getHandledKeys();

        long totalCount = handledKeys.values().stream().mapToLong(LongAdder::longValue).sum();

        Assert.assertEquals(sampleSize * numberOfThreads, totalCount);


    }
}
