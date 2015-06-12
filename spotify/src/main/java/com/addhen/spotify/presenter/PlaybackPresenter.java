package com.addhen.spotify.presenter;

import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.view.PlaybackView;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;

public class PlaybackPresenter implements Presenter {

    private PlaybackView mPlaybackView;

    private MediaPlayer mMediaPlayer;

    private TrackModel mTrackModel;

    public PlaybackPresenter(MediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
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
        releaseMediaPlayer();
    }

    public void setTrackModel(TrackModel trackModel) {
        mTrackModel = trackModel;
        mPlaybackView.loadCoverArt(mTrackModel.coverPhoto);
    }

    public void destroy() {
        releaseMediaPlayer();
    }

    public void playTrack() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mPlaybackView.playing();
            mPlaybackView.updateSeekbar();
        }
    }

    public void pauseTrack() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mPlaybackView.paused();
            mPlaybackView.stopSeekbarUpdate();
        }
    }

    public void stopTrack() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mPlaybackView.stopped();
            mPlaybackView.stopSeekbarUpdate();
        }
    }

    public void seekTo(int to) {
        mMediaPlayer.seekTo(to);
        mPlaybackView.seeked();
        mPlaybackView.updateSeekbar();
    }

    public void nextTrack() {
        // TODO: Implement next track
    }

    public void previousTrack() {
        // TODO: Implement previous track
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
