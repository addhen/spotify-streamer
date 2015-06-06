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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class ArtistFragment extends Fragment implements ArtistView {

    private ArtistPresenter mArtistPresenter;

    private ArtistRecyclerViewAdapter mArtistRecyclerViewAdapter;

    private TextView mEmptyView;

    private List<ArtistModel> mArtistList;

    private RecyclerView mRecyclerView;

    private ProgressBar mProgressBar;

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
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    final String artistName = searchField.getText().toString();
                    mArtistPresenter.searchArtist(artistName);
                    return true;
                }
                return false;
            }
        });
        mEmptyView = (TextView) view.findViewById(R.id.empty_list_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.artistRecyclerView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.artistSearchProgress);
        setRecyclerView(mRecyclerView);

        ImageButton clearBtn = (ImageButton) view.findViewById(R.id.searchIcon);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchField.setText(null);
                searchField.requestFocus();
                if (!Util.isEmpty(mArtistList)) {
                    mArtistList.clear();
                    mArtistRecyclerViewAdapter.setAdapterItems(mArtistList);
                }
            }
        });
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
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void loading() {
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
