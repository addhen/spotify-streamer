package com.addhen.spotify.presenter;

import com.addhen.spotify.state.PlaybackState;
import com.addhen.spotify.view.PlaybackView;

import android.support.annotation.NonNull;

public class PlaybackPresenter implements Presenter {

    private PlaybackView mPlaybackView;

    private PlaybackState mPlaybackState;


    public void setView(@NonNull PlaybackView view) {
        mPlaybackView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void pause() {
    }

    public void setTrackModel(String coverPhoto) {
        mPlaybackView.loadCoverArt(coverPhoto);
        mPlaybackState = new PlaybackState();
    }

    public void playTrack() {
        mPlaybackView.playing();
        mPlaybackView.updateSeekbar();
    }

    public void pauseTrack() {
        mPlaybackView.paused();
        mPlaybackView.stopSeekbarUpdate();
    }


    public void seekTo() {
        mPlaybackView.seeked();
        mPlaybackView.updateSeekbar();
    }
}