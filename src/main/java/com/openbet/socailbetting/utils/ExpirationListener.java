package com.openbet.socailbetting.utils;

/**
 * A listener for expired object events.
 */
 public interface ExpirationListener<E> {
    void expired(E expiredObject);
}
