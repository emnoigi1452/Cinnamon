package me.stella.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadExecutor {

    private final ThreadPoolExecutor executor;
    private final List<Callable<?>> tasks;
    private final AtomicInteger counter;

    public MultiThreadExecutor(int numThreads) {
        // copied this directly from gpt-4o lmao
        this.counter = new AtomicInteger();
        this.executor = new ThreadPoolExecutor(numThreads, numThreads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        this.tasks = new ArrayList<>();
    }

    public void queueTask(Callable<?> task) {
        this.executor.submit(() -> {
            try {
                tasks.add(task);
                task.call();
            } catch(Exception err) { err.printStackTrace(); }
        });
    }

    public void awaitCompletion() throws InterruptedException {
        executor.awaitTermination((long)3600L * 20, TimeUnit.MILLISECONDS);
        executor.shutdown();
    }

    public final int getThreadCount() {
        return this.executor.getCorePoolSize();
    }

    public final AtomicInteger getCounter() {
        return this.counter;
    }

}
