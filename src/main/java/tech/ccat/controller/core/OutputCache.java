package tech.ccat.controller.core;

import java.util.LinkedList;
import java.util.List;

/**
 * 输出缓存系统
 */
public class OutputCache {
    private final int maxSize;
    private final LinkedList<String> cache;
    private final Object lock = new Object();

    public OutputCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedList<>();
    }

    /**
     * 线程安全地添加一行输出
     */
    public void addLine(String line) {
        synchronized (lock) {
            cache.addFirst(line);
            if (cache.size() > maxSize) {
                cache.removeLast();
            }
        }
    }

    /**
     * 获取最近的输出行
     */
    public List<String> getRecentLines(int count) {
        synchronized (lock) {
            int actualCount = Math.min(count, cache.size());
            return new LinkedList<>(cache.subList(0, actualCount));
        }
    }

    /**
     * 清空缓存
     */
    public void clear() {
        synchronized (lock) {
            cache.clear();
        }
    }

    /**
     * 获取缓存大小
     */
    public int size() {
        synchronized (lock) {
            return cache.size();
        }
    }
}