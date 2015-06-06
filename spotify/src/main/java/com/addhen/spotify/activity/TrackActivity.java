package com.addhen.spotify.activity;

import com.addhen.spotify.R;
import com.addhen.spotify.fragment.TrackFragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class TrackActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_ARTIST_ID = "com.addhen.spotify.INTENT_PARAM_ARTIST_ID";

    public static final String INTENT_EXTRA_ARTIST_NAME = "com.addhen.spotify.INTENT_ARTIST_NAME";

    private String mArtistId;

    private static final String FRAG_TAG = "track";

    private TrackFragment mTrackFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        mArtistId = getIntent().getStringExtra(INTENT_EXTRA_ARTIST_ID);
        final String artistName = getIntent().getStringExtra(INTENT_EXTRA_ARTIST_NAME);
        setActionbarTitle(artistName);
        if (mTrackFragment == null) {
            mTrackFragment = TrackFragment.newInstance(mArtistId);
            addFragment(R.id.add_fragment_container, mTrackFragment, FRAG_TAG);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTrackFragment.setTrackList(mTrackFragment.getTrackList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setActionbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.ab_title_track);
            getSupportActionBar().setSubtitle(title);
        }
    }

    private void addFragment(int containerViewId, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager()
                .beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        fragmentTransaction.commit();
    }
}
