package com.addhen.spotify.ui.activity;

import com.addhen.spotify.R;
import com.addhen.spotify.ui.fragment.TrackFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class TrackActivity extends BaseActivity {

    public static final String INTENT_EXTRA_ARTIST_ID = "com.addhen.spotify.INTENT_PARAM_ARTIST_ID";

    public static final String INTENT_EXTRA_ARTIST_NAME = "com.addhen.spotify.INTENT_ARTIST_NAME";

    public static final String BUNDLE_STATE_PARAM_ARTIST_ID
            = "com.addhen.spotify.BUNDLE_STATE_PARAM_ARTIST_ID";

    public static final String BUNDLE_STATE_PARAM_ARTIST_NAME
            = "com.addhen.spotify.BUNDLE_STATE_PARAM_ARTIST_NAME";

    private String mArtistId;

    private String mArtistName;

    private static final String FRAG_TAG = "track";

    private TrackFragment mTrackFragment;

    public TrackActivity() {
        super(R.layout.activity_track, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFragment(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTrackFragment.setTrackList(mTrackFragment.getTrackList());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState
                .putString(BUNDLE_STATE_PARAM_ARTIST_ID, mArtistId);
        savedInstanceState.putString(BUNDLE_STATE_PARAM_ARTIST_NAME, mArtistName);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void setupFragment(@Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mArtistId = savedInstanceState.getString(BUNDLE_STATE_PARAM_ARTIST_ID);
            mArtistName = savedInstanceState.getString(BUNDLE_STATE_PARAM_ARTIST_NAME);
        } else {
            mArtistId = getIntent().getStringExtra(INTENT_EXTRA_ARTIST_ID);
            mArtistName = getIntent().getStringExtra(INTENT_EXTRA_ARTIST_NAME);
        }
        setSubTitle(mArtistName);
        mTrackFragment = (TrackFragment) getFragmentManager().findFragmentByTag(FRAG_TAG);
        if (mTrackFragment == null) {
            mTrackFragment = TrackFragment.newInstance(mArtistId);
            replaceFragment(R.id.add_track_fragment_container, mTrackFragment, FRAG_TAG);
        }
    }
}
