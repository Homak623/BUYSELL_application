package buysell.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
//@Scope("prototype")
public class CustomCache<K, V> {

    private final Map<K, V> cache;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final long maxAgeInMillis;
    private final int maxSize;

    public CustomCache(long maxAgeInMillis, int maxSize) {
        this.maxAgeInMillis = maxAgeInMillis;
        this.maxSize = maxSize;

        this.cache = new LinkedHashMap<K, V>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                boolean shouldRemove = size() > maxSize;
                if (shouldRemove) {
                    String logMessage = String.format("Removing eldest entry:"
                            + " %s=%s (Cache size: %d)",
                        eldest.getKey(), eldest.getValue(), size());
                    log.info(logMessage);
                }
                return shouldRemove;
            }
        };
    }

    public CustomCache() {
        this.maxAgeInMillis = 60000;
        this.maxSize = 1000;
        this.cache = new LinkedHashMap<K, V>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                boolean shouldRemove = size() > maxSize;
                if (shouldRemove) {
                    log.info("Removing eldest entry: {}={} (Cache size: {})",
                        eldest.getKey(), eldest.getValue(), size());
                }
                return shouldRemove;
            }
        };
    }


    public synchronized void put(K key, V value) {
        cache.put(key, value);
        log.info("Added entry: {}={} (Cache size: {})", key, value, cache.size());

        executor.schedule(() -> {
            remove(key);
            log.info("Automatically removed entry: {} (Cache size: {})", key, cache.size());
        }, maxAgeInMillis, TimeUnit.MILLISECONDS);
    }

    public synchronized V get(K key) {
        V value = cache.get(key);
        log.info("Retrieved entry: {}={} (Cache size: {})", key, value, cache.size());
        return value;
    }

    public synchronized void remove(K key) {
        V value = cache.remove(key);
        if (value != null) {
            log.info("Removed entry: {}={} (Cache size: {})", key, value, cache.size());
        }
    }

    public synchronized int size() {
        int size = cache.size();
        log.info("Current cache size: {}", size);
        return size;
    }
}
