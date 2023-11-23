package com.zhaizq.aio.common.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 异步任务执行器
 *
 * execute: 执行的任务
 * callback: [false]状态变化后的回调, [true]结束后的回调
 */
public class AsyncTaskUtil {
    private final ExecutorService executor = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(100));

    public Task000 submit(Consumer<Task000> execute) throws RejectedExecutionException {
        return this.submit(execute, (v, b) -> {});
    }

    public Task000 submit(Consumer<Task000> execute, BiConsumer<Task000, Boolean> callback) throws RejectedExecutionException {
        Task task = new Task(execute, callback);
        executor.execute(task);
        return task;
    }

    @Getter
    @RequiredArgsConstructor
    private static class Task implements Runnable, Task000 {
        private final Consumer<Task000> execute;
        private final BiConsumer<Task000, Boolean> callback;
        private int rate = 0;
        private final long createTime = System.currentTimeMillis();
        private long startTime = -1;
        private long endTime = -1;
        private Exception exception;

        @Override
        public void run() {
            startTime = System.currentTimeMillis();

            try {
                execute.accept(this);
                this.syncRate(100);
            } catch (Exception e) {
                exception = e;
                this.syncRate(-1);
            }

            endTime = System.currentTimeMillis();
            callback.accept(this, true);
        }

        public void syncRate(int rate) {
            if (this.rate != rate) {
                this.rate = rate;
                callback.accept(this, false);
            }
        }
    }

    public interface Task000 {
        int getRate();

        void syncRate(int rate);

        long getCreateTime();

        long getStartTime();

        long getEndTime();

        Exception getException();
    }
}