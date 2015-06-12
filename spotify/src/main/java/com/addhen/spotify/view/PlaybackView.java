package com.addhen.spotify.view;

import com.addhen.spotify.model.TrackModel;

public interface PlaybackView extends UiView {

    void playTrack(TrackModel trackModel);
}
