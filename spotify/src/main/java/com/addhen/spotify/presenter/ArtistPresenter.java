package com.addhen.spotify.presenter;

import com.addhen.spotify.model.mapper.ArtistModelMapper;
import com.addhen.spotify.view.ArtistView;

import android.support.annotation.NonNull;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ArtistPresenter implements Presenter {

    private ArtistView mArtistView;

    private ArtistModelMapper mArtistModelMapper;

    public ArtistPresenter() {
        mArtistModelMapper = new ArtistModelMapper();
    }

    public void setView(@NonNull ArtistView view) {
        mArtistView = view;
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
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        spotify.searchArtists(name, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                final List<Artist> artists = artistsPager.artists.items;
                mArtistView.showArtists(mArtistModelMapper.map(artists));
            }

            @Override
            public void failure(RetrofitError error) {
                mArtistView.showError(error.getMessage());
            }
        });
    }
}
