package com.addhen.spotify.view;

import com.addhen.spotify.model.TrackModel;

import java.util.List;

public interface TrackView extends UiView {

    void showTracks(List<TrackModel> trackList);
}
