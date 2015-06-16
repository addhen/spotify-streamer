package com.addhen.spotify.activity;

import com.addhen.spotify.R;
import com.addhen.spotify.fragment.TrackFragment;

import android.os.Bundle;

public class TrackActivity extends BaseActivity {

    public static final String INTENT_EXTRA_ARTIST_ID = "com.addhen.spotify.INTENT_PARAM_ARTIST_ID";

    public static final String INTENT_EXTRA_ARTIST_NAME = "com.addhen.spotify.INTENT_ARTIST_NAME";

    private String mArtistId;

    private static final String FRAG_TAG = "track";

    private TrackFragment mTrackFragment;

    public TrackActivity() {
        super(R.layout.activity_main, R.menu.menu_main);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtistId = getIntent().getStringExtra(INTENT_EXTRA_ARTIST_ID);
        final String artistName = getIntent().getStringExtra(INTENT_EXTRA_ARTIST_NAME);
        setSubTitle(artistName);
        mTrackFragment = (TrackFragment) getFragmentManager().findFragmentByTag(FRAG_TAG);
        if (mTrackFragment == null) {
            mTrackFragment = TrackFragment.newInstance(mArtistId);
            replaceFragment(R.id.add_track_fragment_container, mTrackFragment, FRAG_TAG);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTrackFragment.setTrackList(mTrackFragment.getTrackList());
    }
}
