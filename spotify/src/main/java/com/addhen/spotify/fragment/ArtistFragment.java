package com.addhen.spotify.fragment;

import com.addhen.spotify.R;
import com.addhen.spotify.adapter.ArtistRecyclerViewAdapter;
import com.addhen.spotify.model.ArtistModel;
import com.addhen.spotify.presenter.ArtistPresenter;
import com.addhen.spotify.util.Util;
import com.addhen.spotify.view.ArtistView;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class ArtistFragment extends Fragment implements ArtistView {

    private ArtistPresenter mArtistPresenter;

    private ArtistRecyclerViewAdapter mArtistRecyclerViewAdapter;

    private TextView mEmptyView;

    private List<ArtistModel> mArtistList;

    public static ArtistFragment newInstance() {
        ArtistFragment fragment = new ArtistFragment();
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
        mArtistPresenter.setView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);
        mArtistPresenter = new ArtistPresenter();
        final EditText searchField = (EditText) view.findViewById(R.id.searchBar);
        ImageButton searchBtn = (ImageButton) view.findViewById(R.id.searchIcon);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String artistName = searchField.getText().toString();
                mArtistPresenter.searchArtist(artistName);
            }
        });
        mEmptyView = (TextView) view.findViewById(R.id.empty_list_view);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.artistRecyclerView);
        setRecyclerView(recyclerView);
        return view;
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        mArtistRecyclerViewAdapter = new ArtistRecyclerViewAdapter(getActivity().getApplication());
        mArtistRecyclerViewAdapter.registerAdapterDataObserver(
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
        recyclerView.setAdapter(mArtistRecyclerViewAdapter);
        if (!Util.isEmpty(mArtistList)) {
            mArtistRecyclerViewAdapter.setAdapterItems(mArtistList);
        }
        setEmptyText();
    }

    private void setEmptyText() {
        if ((mArtistRecyclerViewAdapter == null
                || mArtistRecyclerViewAdapter.getItemCount() == 0)) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showArtists(final List<ArtistModel> artistList) {
        mArtistList = artistList;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mArtistRecyclerViewAdapter.setAdapterItems(artistList);
            }
        });

    }

    public void setArtistList(final List<ArtistModel> artistList) {
        mArtistList = artistList;
    }

    public List<ArtistModel> getArtistList() {
        return mArtistList;
    }

    @Override
    public void showError(String message) {
        Snackbar.make(null, message, Snackbar.LENGTH_LONG);
    }

    @Override
    public Context getAppContext() {
        return getActivity().getApplication();
    }
}
