package com.zhaizq.aio.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CacheMap<K, V> {
    private final static List<WeakReference<CacheMap<?, ?>>> references = new LinkedList<>();
    public final static CacheMap<String, Object> DEFAULT = new CacheMap<>();

    static {
        new ScheduledThreadPoolExecutor(1, v -> {
            Thread thread = new Thread(v, "cache-map-cleaner");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setDaemon(true);
            return thread;
        }).scheduleAtFixedRate(CacheMap::cleanup0, 30, 30, TimeUnit.SECONDS);
    }

    private final Map<K, Cache<V>> cacheMap = new ConcurrentHashMap<>();

    public CacheMap() {
        references.add(new WeakReference<>(this));
    }

    public Cache<V> getCache(K k) {
        return cacheMap.get(k);
    }

    public V getOrDefault(K k, V v) {
        V value = get(k);
        return value != null ? value : v;
    }

    public V get(K k) {
        return get(k, 0);
    }

    public V get(K k, long expire) {
        Cache<V> cache = cacheMap.get(k);

        if (cache != null && cache.expire > System.currentTimeMillis()) {
            cache.expire = expire > 0 ? System.currentTimeMillis() + expire : cache.expire;
            return cache.value;
        }

        return null;
    }

    public V put(K k, V v, long expire) {
        Cache<V> cache = cacheMap.put(k, new Cache<>(v, System.currentTimeMillis() + expire));
        return cache != null ? cache.getValue() : null;
    }

    public V remove(K k) {
        Cache<V> cache = cacheMap.remove(k);
        return cache != null ? cache.value : null;
    }

    public Map<K, Cache<V>> getCacheMap() {
        return Collections.unmodifiableMap(cacheMap);
    }

    public void cleanup() {
        long limit = System.currentTimeMillis();
        cacheMap.entrySet().stream().filter(v -> v.getValue().expire < limit).forEach(v -> {
            cacheMap.computeIfPresent(v.getKey(), (key, cache) -> cache.expire < limit ? null : cache);
        });
    }

    public static void cleanup0() {
        Iterator<WeakReference<CacheMap<?, ?>>> iterator = references.iterator();
        while (iterator.hasNext()) {
            CacheMap<?, ?> cacheMap = iterator.next().get();
            if (cacheMap == null) {
                iterator.remove();
                continue;
            }

            cacheMap.cleanup();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Cache<V> {
        private final V value;
        private long expire;
    }
}