package com.addhen.spotify.view;

public interface PlaybackView extends UiView {

    void playing();

    void paused();

    void stopped();

    void seeked();

    void stopSeekbarUpdate();

    void updateSeekbar();

    void loadCoverArt(String url);

    void musicPlayerPrepared();
}
