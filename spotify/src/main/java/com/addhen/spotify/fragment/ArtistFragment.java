package com.addhen.spotify.fragment;

import com.addhen.spotify.R;
import com.addhen.spotify.adapter.ArtistRecyclerViewAdapter;
import com.addhen.spotify.model.ArtistModel;
import com.addhen.spotify.presenter.ArtistPresenter;
import com.addhen.spotify.util.Util;
import com.addhen.spotify.view.ArtistView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class ArtistFragment extends BaseFragment implements ArtistView {

    private ArtistPresenter mArtistPresenter;

    private ArtistRecyclerViewAdapter mArtistRecyclerViewAdapter;

    @InjectView(R.id.empty_list_view)
    TextView mEmptyView;

    private List<ArtistModel> mArtistList = new ArrayList<>();

    @InjectView(R.id.artistRecyclerView)
    RecyclerView mRecyclerView;

    @InjectView(R.id.artistSearchProgress)
    ProgressBar mProgressBar;

    @InjectView(R.id.searchBar)
    EditText mSearchField;

    public ArtistFragment() {
        super(R.layout.fragment_artist_list, 0);
    }

    public static ArtistFragment newInstance() {
        ArtistFragment fragment = new ArtistFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mArtistPresenter.setView(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mArtistPresenter = new ArtistPresenter();
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    mSearchField.clearFocus();
                    //Hide the soft keyboard so the user can view the full list of searched artist
                    hideSoftKeyboard(mSearchField);
                    final String artistName = mSearchField.getText().toString();
                    mArtistPresenter.searchArtist(artistName);
                    return true;
                }
                return false;
            }
        });
        setRecyclerView(mRecyclerView);
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        mArtistRecyclerViewAdapter = new ArtistRecyclerViewAdapter(getActivity().getApplication(),
                mEmptyView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(mArtistRecyclerViewAdapter);
        if (!Util.isEmpty(mArtistList)) {
            mArtistRecyclerViewAdapter.setAdapterItems(mArtistList);
        }
    }

    @OnClick(R.id.clearSearchIcon)
    void clearSearch() {
        mSearchField.setText(null);
        mSearchField.requestFocus();
        if (!Util.isEmpty(mArtistList)) {
            mArtistList.clear();
            mArtistRecyclerViewAdapter.setAdapterItems(mArtistList);
        }
    }

    @Override
    @UiThread
    public void showArtists(final List<ArtistModel> artistList) {
        mArtistList = artistList;
        setAdapterItems();
    }

    public void setArtistList(final List<ArtistModel> artistList) {
        mArtistList = artistList;
    }

    public List<ArtistModel> getArtistList() {
        return mArtistList;
    }

    @Override
    @UiThread
    public void showError(String message) {
        // Reinitialize items recyclerview
        setAdapterItems();
        showSnabackar(mRecyclerView, message);
    }

    @Override
    @UiThread
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

    private boolean hideSoftKeyboard(EditText editText) {
        InputMethodManager imm =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void setAdapterItems() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mArtistRecyclerViewAdapter.setAdapterItems(mArtistList);
            }
        });
    }
}
