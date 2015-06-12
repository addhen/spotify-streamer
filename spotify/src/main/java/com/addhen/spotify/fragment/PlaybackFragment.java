package com.addhen.spotify.fragment;

import com.addhen.spotify.R;
import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.view.PlaybackView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

public class PlaybackFragment extends BaseFragment implements PlaybackView {

    private static final String ARGUMENT_KEY_TRACK_MODEL
            = "com.addhen.spotify.ARGUMENT_TRACK_MODEL";

    private TrackModel mTrackModel;


    public PlaybackFragment() {
        super(R.layout.fragmet_track_playback, 0);
    }

    public static PlaybackFragment newInstance(@NonNull TrackModel trackModel) {
        PlaybackFragment fragment = new PlaybackFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGUMENT_KEY_TRACK_MODEL, trackModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final TrackModel trackModel = getArguments().getParcelable(ARGUMENT_KEY_TRACK_MODEL);
        // TODO: Fetch playback
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setTrackModel(final TrackModel trackModel) {
        mTrackModel = trackModel;
    }

    public TrackModel getTrackList() {
        return mTrackModel;
    }

    @Override
    public void playTrack(TrackModel trackModel) {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void loading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public Context getAppContext() {
        return null;
    }
}
