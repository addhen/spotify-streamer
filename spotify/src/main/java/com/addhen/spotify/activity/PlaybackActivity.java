package com.addhen.spotify.activity;

import com.addhen.spotify.R;
import com.addhen.spotify.fragment.PlaybackFragment;
import com.addhen.spotify.model.TrackModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.InjectView;

public class PlaybackActivity extends BaseActivity {

    private static final String INTENT_EXTRA_PARAM_TRACK_MODEL
            = "com.ushahidi.android.INTENT_PARAM_TRACK_MODEL";

    private static final String INTENT_STATE_PARAM_TRACK
            = "com.ushahidi.android.STATE_PARAM_TRACK_MODEL";

    private TrackModel mTrackModel;

    private static final String FRAG_TAG = "track";

    private PlaybackFragment mPlaybackFragment;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    public PlaybackActivity() {
        super(R.layout.activity_playback, R.menu.menu_main);
    }

    public static Intent getIntent(final Context context, TrackModel trackModel) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(INTENT_EXTRA_PARAM_TRACK_MODEL, trackModel);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
        }
        if (savedInstanceState == null) {
            mTrackModel = getIntent().getParcelableExtra(INTENT_EXTRA_PARAM_TRACK_MODEL);
        } else {
            mTrackModel = savedInstanceState.getParcelable(INTENT_STATE_PARAM_TRACK);
        }
        mPlaybackFragment = (PlaybackFragment) getFragmentManager()
                .findFragmentByTag(FRAG_TAG);
        if (mPlaybackFragment == null) {
            mPlaybackFragment = PlaybackFragment.newInstance(mTrackModel);
            replaceFragment(R.id.add_fragment_container, mPlaybackFragment, FRAG_TAG);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlaybackFragment.setTrackModel(mTrackModel);
    }
}
