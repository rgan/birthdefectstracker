package org.healthapps.birthdefects.utils;

import javax.cache.Cache;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import java.util.HashMap;

public class BDCache {

    private Cache cache;

    public void put(String key, Object value) {
        initCache();
        if (cache != null) {
            cache.put(key, value);
        }
    }

    public Object get(String key) {
        initCache();
        return cache != null ? cache.get(key) : null;
    }

    public void remove(String key) {
        initCache();
        if (cache != null) {
            cache.remove(key);
        }
    }

    private void initCache() {
        if (cache == null) {
            try {
                CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
                cache = cacheFactory.createCache(new HashMap());
            } catch (Exception ex) {
            }
        }
    }
}
