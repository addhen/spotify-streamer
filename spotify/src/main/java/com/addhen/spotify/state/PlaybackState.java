package com.addhen.spotify.state;

public class PlaybackState {

    public final State state;

    private Exception mException;

    public PlaybackState(State state, Exception exception) {
        this.state = state;
        mException = exception;
    }

    public PlaybackState() {
        this(State.LOADING, null);
    }

    public PlaybackState sendState(State state, Exception exception) {
        return new PlaybackState(state, exception);
    }

    public PlaybackState sendState(State state) {
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

    public boolean isError() {
        return state == State.ERROR;
    }

    public String getError() {
        if (mException == null) {
            return null;
        }
        return mException.getLocalizedMessage();
    }
}
