package com.addhen.spotify.presenter;

import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.view.PlaybackView;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import java.io.IOException;

import static android.media.MediaPlayer.OnPreparedListener;

public class PlaybackPresenter implements Presenter {

    private PlaybackView mPlaybackView;

    private MediaPlayer mMediaPlayer;

    private TrackModel mTrackModel;

    private final OnPreparedListener mOnPreparedListener
            = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mPlaybackView.hideLoading();
            mPlaybackView.musicPlayerPrepared();
            mp.start();
            mPlaybackView.playing();
            mPlaybackView.updateSeekbar();
        }
    };

    private final MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mp.reset();
            mPlaybackView.showError("Error occurred attempting to playback");
            return false;
        }
    };

    private final MediaPlayer.OnCompletionListener mOnCompletionListener
            = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mPlaybackView.stopSeekbarUpdate();
            mPlaybackView.stopped();
        }
    };

    public PlaybackPresenter(MediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
    }

    public void setView(@NonNull PlaybackView view) {
        mPlaybackView = view;
    }

    @Override
    public void resume() {
        // Do nothing
    }

    public void stop() {
        releaseMediaPlayer();
    }

    @Override
    public void pause() {
        releaseMediaPlayer();
    }

    public void setTrackModel(TrackModel trackModel) {
        mTrackModel = trackModel;
        mPlaybackView.loadCoverArt(mTrackModel.coverPhoto);
        mPlaybackView.loading();
        try {
            mMediaPlayer.setDataSource(mTrackModel.previewUrl);
        } catch (IOException e) {
            e.printStackTrace();
            mPlaybackView.showError(e.getMessage());
        } catch (IllegalStateException e) {
            mPlaybackView.showError(e.getMessage());
        }
        mMediaPlayer.prepareAsync();
    }

    public void destroy() {
        releaseMediaPlayer();
    }

    public void playTrack() {
        if (mMediaPlayer.isPlaying()) {
            pauseTrack();
            return;
        }
        mMediaPlayer.start();
        mPlaybackView.playing();
        mPlaybackView.updateSeekbar();
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
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(to);
            mPlaybackView.seeked();
            mPlaybackView.updateSeekbar();
        }
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