package org.healthapps.birthdefects.utils;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

public class TestUtils {

    public static Set<Long> defectsWithOneItem(long id) {
        final HashSet<Long> defects = Sets.newHashSet(id);
        return defects;
    }

    private TestUtils() {
    }
}
