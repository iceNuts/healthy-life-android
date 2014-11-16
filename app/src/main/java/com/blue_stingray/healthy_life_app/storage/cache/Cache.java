package com.blue_stingray.healthy_life_app.storage.cache;

import android.util.LruCache;

public class Cache<K,V> extends LruCache<K,V> {

    public Cache() {
        super((int) (Runtime.getRuntime().maxMemory() / 1024));
    }

    @Override
    protected int sizeOf(K key, V value) {
        return 1;
    }

}
