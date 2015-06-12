package com.addhen.spotify.presenter;

import com.addhen.spotify.view.PlaybackView;

import android.support.annotation.NonNull;

public class PlaybackPresenter implements Presenter {

    private PlaybackView mPlaybackView;

    public PlaybackPresenter() {
    }

    public void setView(@NonNull PlaybackView view) {
        mPlaybackView = view;
    }

    @Override
    public void resume() {
        // Do nothing
    }

    @Override
    public void pause() {
        // Do nothing
    }
}
