package com.addhen.spotify.presenter;

import com.addhen.spotify.model.mapper.TrackModelMapper;
import com.addhen.spotify.view.TrackView;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
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
        Map<String, Object> options = new HashMap<>();
        options.put("country", "US");
        spotify.getArtistTopTrack(artistId, options, new Callback<Tracks>() {
            @Override
            public void success(Tracks t, Response response) {
                final List<Track> trackList = t.tracks;
                mTrackView.showTracks(mTrackModelMapper.map(trackList));
            }

            @Override
            public void failure(RetrofitError error) {
                mTrackView.showError(error.getMessage());
            }
        });
    }
}
