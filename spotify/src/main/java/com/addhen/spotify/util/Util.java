package com.addhen.spotify.util;

import java.util.Collection;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class Util {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
