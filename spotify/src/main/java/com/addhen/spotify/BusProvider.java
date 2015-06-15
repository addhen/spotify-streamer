package com.addhen.spotify;


import com.squareup.otto.Bus;

public final class BusProvider {

    private static final SpotifyBus BUS = new SpotifyBus(new Bus());

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
