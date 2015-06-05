package com.addhen.spotify.view;

import com.addhen.spotify.model.ArtistModel;

import java.util.List;

public interface ArtistView extends UiView {

    void showArtists(List<ArtistModel> artistList);
}
