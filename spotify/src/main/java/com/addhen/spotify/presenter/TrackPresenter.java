package com.addhen.spotify.presenter;

import com.addhen.spotify.model.mapper.TrackModelMapper;
import com.addhen.spotify.view.TrackView;

import android.support.annotation.NonNull;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TrackPresenter implements Presenter {

    private TrackView mTrackView;

    private TrackModelMapper mTrackModelMapper;

    public TrackPresenter() {
        mTrackModelMapper = new TrackModelMapper();
    }

    public void setView(@NonNull TrackView view) {
        mTrackView = view;
    }

    @Override
    public void resume() {
        // Do nothing
    }

    @Override
    public void pause() {
        // Do nothing
    }

    public void setTrack(String artistId) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        spotify.searchTracks(artistId, new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {
                final List<Track> tracks = tracksPager.tracks.items;
                mTrackView.showTracks(mTrackModelMapper.map(tracks));
            }

            @Override
            public void failure(RetrofitError error) {
                mTrackView.showError(error.getMessage());
            }
        });
    }
}
