package com.addhen.spotify.fragment;

import com.addhen.spotify.R;
import com.addhen.spotify.presenter.ArtistPresenter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TopTracksFragment extends Fragment {

    private ArtistPresenter mArtistPresenter;

    public static TopTracksFragment newInstance() {
        TopTracksFragment fragment = new TopTracksFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TopTracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        mArtistPresenter = new ArtistPresenter();
        return view;
    }

    public void setEmptyText(CharSequence emptyText) {
    }

    public void searchForArtist(String name) {
        mArtistPresenter.searchArtist(name);
    }
}
