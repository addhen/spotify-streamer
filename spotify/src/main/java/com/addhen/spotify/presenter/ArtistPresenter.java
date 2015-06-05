package com.addhen.spotify.presenter;

import com.addhen.spotify.common.SpotifyServices;

import kaaes.spotify.webapi.android.SpotifyService;

public class ArtistPresenter implements Presenter {

    private SpotifyService mSpotify;

    private SpotifyServices mSpotifyServices;

    public ArtistPresenter() {
        mSpotifyServices = new SpotifyServices();
        mSpotify = mSpotifyServices.getSpotify();
    }

    @Override
    public void resume() {
        // Do nothing
    }

    @Override
    public void pause() {
        // Do nothing
    }

    public void searchArtist(String name) {
        // TODO: Use spotify's API to search for the artist.
    }
}
