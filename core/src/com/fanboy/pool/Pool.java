package com.fanboy.pool;

import java.util.ArrayList;
import java.util.List;

public class Pool<T> {
    private int max, current = 0;
    private PoolInterface<T> poolInterface;
    private List<T> objects = new ArrayList<>();

    Pool(PoolInterface<T> poolInterface) {
        this(poolInterface, 32);
    }

    Pool(PoolInterface<T> poolInterface, int max) {
        this.poolInterface = poolInterface;
        this.max = max;
        init();
    }

    private void init() {
        for (int i = 0; i < max; i++) {
            objects.add(getNewObject());
        }
    }

    private T getNewObject() {
        return poolInterface.getNewObject();
    }

    public T obtain() {
        current = (current + 1) % max;
        T object = objects.get(current % max);
        if (object instanceof Poolable) {
            ((Poolable) object).reset();
        }
        return object;
    }
}
