package com.example.toolkit;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A lightweight, thread-unsafe, TTL + LRU-bounded in-memory cache.
 * Designed for single-threaded use or external synchronisation.
 *
 * @param <K> key type
 * @param <V> value type
 */
public final class SimpleCache<K, V> {

    private final int maxSize;
    private final Duration ttl;
    private final Clock clock;

    private final LinkedHashMap<K, CacheEntry<V>> store;

    /**
     * @param maxSize maximum number of entries (LRU eviction when exceeded)
     * @param ttl     how long each entry lives; {@link Duration#ZERO} means no TTL
     */
    public SimpleCache(int maxSize, Duration ttl) {
        this(maxSize, ttl, Clock.systemUTC());
    }

    /** Package-visible constructor for testing with a controllable clock. */
    SimpleCache(int maxSize, Duration ttl, Clock clock) {
        if (maxSize <= 0) throw new IllegalArgumentException("maxSize must be > 0");
        Objects.requireNonNull(ttl, "ttl");
        this.maxSize = maxSize;
        this.ttl = ttl;
        this.clock = Objects.requireNonNull(clock, "clock");
        this.store = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > maxSize;
            }
        };
    }

    /**
     * Associates {@code value} with {@code key}.
     */
    public void put(K key, V value) {
        Objects.requireNonNull(key, "key");
        store.put(key, new CacheEntry<>(value, clock.instant()));
    }

    /**
     * Returns the cached value, or empty if absent / expired.
     */
    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "key");
        CacheEntry<V> entry = store.get(key);
        if (entry == null) return Optional.empty();
        if (isExpired(entry)) {
            store.remove(key);
            return Optional.empty();
        }
        return Optional.ofNullable(entry.value);
    }

    /**
     * Returns the cached value, or computes and caches it via {@code loader}.
     */
    public V getOrLoad(K key, Supplier<V> loader) {
        return get(key).orElseGet(() -> {
            V value = loader.get();
            put(key, value);
            return value;
        });
    }

    /**
     * Removes a single entry. No-op if not present.
     */
    public void invalidate(K key) {
        Objects.requireNonNull(key, "key");
        store.remove(key);
    }

    /** Removes all entries. */
    public void invalidateAll() {
        store.clear();
    }

    /** Returns the number of non-expired entries currently held. */
    public int size() {
        if (!ttl.isZero()) {
            store.entrySet().removeIf(e -> isExpired(e.getValue()));
        }
        return store.size();
    }

    /** Returns {@code true} if there are no live entries. */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns whether a key is present and not expired (without evicting it).
     */
    public boolean containsKey(K key) {
        Objects.requireNonNull(key, "key");
        CacheEntry<V> entry = store.get(key);
        return entry != null && !isExpired(entry);
    }

    /**
     * Refreshes the TTL of an existing entry. No-op if not present or expired.
     */
    public void refresh(K key) {
        Objects.requireNonNull(key, "key");
        CacheEntry<V> entry = store.get(key);
        if (entry != null && !isExpired(entry)) {
            store.put(key, new CacheEntry<>(entry.value, clock.instant()));
        }
    }

    /** Returns a read-only snapshot of current (non-expired) entries. */
    public Map<K, V> snapshot() {
        Map<K, V> snap = new java.util.LinkedHashMap<>();
        Instant now = clock.instant();
        store.forEach((k, e) -> {
            if (!isExpiredAt(e, now)) snap.put(k, e.value);
        });
        return Map.copyOf(snap);
    }

    // ── internals ────────────────────────────────────────────────────────────

    private boolean isExpired(CacheEntry<V> e) {
        return isExpiredAt(e, clock.instant());
    }

    private boolean isExpiredAt(CacheEntry<V> e, Instant now) {
        if (ttl.isZero()) return false;
        return now.isAfter(e.insertedAt.plus(ttl));
    }

    private record CacheEntry<V>(V value, Instant insertedAt) {}
}
