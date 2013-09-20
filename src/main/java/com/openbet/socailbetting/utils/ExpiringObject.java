package com.openbet.socailbetting.utils;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExpiringObject<K, V> implements Serializable{
	private static final long serialVersionUID = 9092907270045706904L;

	private K key;

    private V value;

    private long lastAccessTime;

    private final ReadWriteLock lastAccessTimeLock = new ReentrantReadWriteLock();

    ExpiringObject(K key, V value, long lastAccessTime) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "An expiring object cannot be null.");
        }

        this.key = key;
        this.value = value;
        this.lastAccessTime = lastAccessTime;
    }

    public long getLastAccessTime() {
        lastAccessTimeLock.readLock().lock();

        try {
            return lastAccessTime;
        } finally {
            lastAccessTimeLock.readLock().unlock();
        }
    }

    public void setLastAccessTime(long lastAccessTime) {
        lastAccessTimeLock.writeLock().lock();

        try {
            this.lastAccessTime = lastAccessTime;
        } finally {
            lastAccessTimeLock.writeLock().unlock();
        }
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return value.equals(obj);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}