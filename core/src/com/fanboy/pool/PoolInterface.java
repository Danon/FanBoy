package com.fanboy.pool;

@FunctionalInterface
interface PoolInterface<T> {
    T getNewObject();
}
