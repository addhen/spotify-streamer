package com.addhen.spotify.view;

import com.addhen.spotify.model.Artist;

import java.util.List;

public interface ArtistView extends UiView {

    void showArtists(List<Artist> artistList);
}
