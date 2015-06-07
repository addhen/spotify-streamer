package com.addhen.spotify.util;

import java.util.Collection;

public class Util {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
