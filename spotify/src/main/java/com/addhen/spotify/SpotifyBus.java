package com.addhen.spotify;

import com.squareup.otto.Bus;

import android.os.Handler;
import android.os.Looper;

/**
 * Adds support for events to be posted on other thread apart from the main.
 */
public class SpotifyBus extends Bus {

    private final Bus mBus;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public SpotifyBus(final Bus bus) {
        if (bus == null) {
            throw new NullPointerException("Bus cannot be null");
        }
        mBus = bus;
    }

    @Override
    public void register(Object obj) {
        mBus.register(obj);
    }

    @Override
    public void unregister(Object obj) {
        mBus.unregister(obj);
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mBus.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBus.post(event);
                }
            });
        }
    }

}
