package buysell.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CustomCache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final long maxAgeInMillis;

    public CustomCache(long maxAgeInMillis) {
        this.maxAgeInMillis = maxAgeInMillis;
    }

    public void put(K key, V value) {
        cache.put(key, value);

        executor.schedule(() -> remove(key), maxAgeInMillis, TimeUnit.MILLISECONDS);
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public int size() {
        return cache.size();
    }
}
