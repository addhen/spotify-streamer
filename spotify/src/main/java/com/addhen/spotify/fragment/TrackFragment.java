package com.addhen.spotify.fragment;

import com.addhen.spotify.R;
import com.addhen.spotify.adapter.TrackRecyclerViewAdapter;
import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.presenter.TrackPresenter;
import com.addhen.spotify.util.Util;
import com.addhen.spotify.view.TrackView;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;

public class TrackFragment extends BaseFragment implements TrackView {

    private static final String ARGUMENT_KEY_ARTIST_ID = "com.addhen.spotify.ARGUMENT_ARTIST_ID";

    private TrackPresenter mTrackPresenter;

    private TrackRecyclerViewAdapter mTrackRecyclerViewAdapter;

    private List<TrackModel> mTrackList;

    @InjectView(R.id.empty_list_view)
    TextView mEmptyView;

    @InjectView(R.id.trackRecyclerView)
    RecyclerView mRecyclerView;

    @InjectView(R.id.trackProgress)
    ProgressBar mProgressBar;

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
        if (Util.isEmpty(mTrackList)) {
            mTrackPresenter.setTrack(artistId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTrackPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mTrackPresenter.pause();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTrackPresenter = new TrackPresenter();
        setRecyclerView(mRecyclerView);
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        mTrackRecyclerViewAdapter = new TrackRecyclerViewAdapter(getActivity().getApplication());
        mTrackRecyclerViewAdapter.registerAdapterDataObserver(
                new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        setEmptyText();
                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {
                        super.onItemRangeChanged(positionStart, itemCount);
                        setEmptyText();
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        super.onItemRangeRemoved(positionStart, itemCount);
                        setEmptyText();
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        setEmptyText();
                    }

                    @Override
                    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                        super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                        setEmptyText();
                    }
                });
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(mTrackRecyclerViewAdapter);
        if (!Util.isEmpty(mTrackList)) {
            mTrackRecyclerViewAdapter.setAdapterItems(mTrackList);
        }
        setEmptyText();
    }

    private void setEmptyText() {
        if ((mTrackRecyclerViewAdapter == null
                || mTrackRecyclerViewAdapter.getItemCount() == 0)) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
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
}
