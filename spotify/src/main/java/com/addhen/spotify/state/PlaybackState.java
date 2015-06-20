package com.addhen.spotify.state;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PlaybackState {

    public final State state;

    private Exception mException;

    public long updateTime;

    public int position;

    public int duration;

    public PlaybackState(@NonNull State state, int position, long updateTime, int duration,
            @Nullable Exception exception) {
        this.state = state;
        this.position = position;
        this.updateTime = updateTime;
        this.duration = duration;
        mException = exception;
    }

    public PlaybackState() {
        this(State.LOADING, 0, 0, 0, null);
    }

    public PlaybackState sendState(@NonNull State state, int position, long updateTime,
            int duration) {
        return new PlaybackState(state, position, updateTime, duration, null);
    }

    public PlaybackState sendState(@NonNull State state, @Nullable Exception exception) {
        return new PlaybackState(state, 0, 0, 0, exception);
    }

    public PlaybackState sendState(@NonNull State state) {
        return sendState(state, null);
    }

    public boolean isPlaying() {
        return state == State.PLAYING;
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }

    public boolean isStopped() {
        return state == State.STOPPED;
    }

    public boolean isLoading() {
        return state == State.LOADING;
    }

    public boolean isBuffering() {
        return state == State.BUFFERING;
    }

    public boolean isError() {
        return state == State.ERROR;
    }

    public boolean isSkippedNext() {
        return state == State.SKIPPED_NEXT;
    }

    public boolean isSkippedPrevious() {
        return state == State.SKIPPED_NEXT;
    }

    public String getError() {
        if (mException == null) {
            return null;
        }
        return mException.getLocalizedMessage();
    }
}
