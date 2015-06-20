package com.addhen.spotify.ui.fragment;

import com.addhen.spotify.BusProvider;
import com.addhen.spotify.R;
import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.presenter.TrackPresenter;
import com.addhen.spotify.state.PlaybackState;
import com.addhen.spotify.ui.activity.PlaybackActivity;
import com.addhen.spotify.ui.adapter.TrackRecyclerViewAdapter;
import com.addhen.spotify.ui.listener.RecyclerItemClickListener;
import com.addhen.spotify.util.Utils;
import com.addhen.spotify.view.TrackView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class TrackFragment extends BaseFragment implements TrackView {

    private static final String ARGUMENT_KEY_ARTIST_ID = "com.addhen.spotify.ARGUMENT_ARTIST_ID";

    private static final String BUNDLE_STATE_TRACK_SELECTED
            = "com.addhen.spotify.BUNDLE_STATE_TRACK_SELECTED";

    private TrackPresenter mTrackPresenter;

    private TrackRecyclerViewAdapter mTrackRecyclerViewAdapter;

    private List<TrackModel> mTrackList;

    private int mPosition = -1;

    @InjectView(R.id.empty_list_view)
    TextView mEmptyView;

    @InjectView(R.id.trackRecyclerView)
    RecyclerView mRecyclerView;

    @InjectView(R.id.trackProgress)
    ProgressBar mProgressBar;

    @InjectView(R.id.selectedTrackAlbumArt)
    ImageView mTrackAlbumArt;

    @InjectView(R.id.selectedTrackName)
    TextView mTrackName;

    @InjectView(R.id.miniPlaybackController)
    View mControllerView;

    public TrackFragment() {
        super(R.layout.fragment_track_list, 0);
    }

    public static TrackFragment newInstance(String artistId) {
        TrackFragment fragment = new TrackFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_KEY_ARTIST_ID, artistId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTrackPresenter.setView(this);
        final String artistId = getArguments().getString(ARGUMENT_KEY_ARTIST_ID);

        if (Utils.isEmpty(mTrackList)) {
            final String countryCode = getSharedPreferences()
                    .getString(SettingsFragment.PLAYBACK_COUNTRY_CODES, "US");
            mTrackPresenter.setTrack(artistId, countryCode);
        }
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(BUNDLE_STATE_TRACK_SELECTED, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        mTrackPresenter.resume();
        updateMediaDescription(mPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        mTrackPresenter.pause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(BUNDLE_STATE_TRACK_SELECTED, mPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTrackPresenter = new TrackPresenter();
        setRecyclerView(mRecyclerView);
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        mTrackRecyclerViewAdapter = new TrackRecyclerViewAdapter(getActivity().getApplication(),
                mEmptyView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(mTrackRecyclerViewAdapter);
        if (!Utils.isEmpty(mTrackList)) {
            mTrackRecyclerViewAdapter.setAdapterItems(mTrackList);
        }
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getAppContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mPosition = position;
                        Intent intent = PlaybackActivity
                                .getIntent(getAppContext(), (ArrayList) mTrackList, position);
                        getActivity().startActivity(intent);
                    }
                }));
    }

    public void clearItems() {
        if (mTrackRecyclerViewAdapter != null) {
            mTrackRecyclerViewAdapter.clearAllItems();
        }
    }

    @Override
    @UiThread
    public void showTracks(final List<TrackModel> trackList) {
        mTrackList = trackList;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTrackRecyclerViewAdapter.setAdapterItems(trackList);
            }
        });

    }

    public void setTrackList(final List<TrackModel> trackList) {
        mTrackList = trackList;
    }

    public List<TrackModel> getTrackList() {
        return mTrackList;
    }

    @Override
    public void showError(String message) {
        showSnabackar(mRecyclerView, message);
    }

    @Override
    public void loading() {
        mEmptyView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    @UiThread
    public void hideLoading() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public Context getAppContext() {
        return getActivity().getApplication();
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getAppContext());
    }

    private void updateMediaDescription(final int currentlyPlaying) {
        if (currentlyPlaying == -1) {
            return;
        }
        final TrackModel trackModel = mTrackList.get(currentlyPlaying);
        mControllerView.setVisibility(View.VISIBLE);
        mTrackName.setText(trackModel.artistName);
        Picasso.with(getAppContext()).load(trackModel.coverPhoto)
                .placeholder(R.drawable.ic_default_music_playing).into(mTrackAlbumArt);
        mControllerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = PlaybackActivity
                        .getIntent(context, (ArrayList) mTrackList, currentlyPlaying);
                context.startActivity(intent);
            }
        });
    }

    @Subscribe
    public void updatePlaybackState(final PlaybackState playbackState) {
        if (playbackState == null) {
            return;
        }
        switch (playbackState.state) {
            case STOPPED:
                mControllerView.setVisibility(View.GONE);
                break;
        }
    }
}