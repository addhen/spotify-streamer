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

    public static final String INSTANCE_STATE_PARAM_ARTIST_ID
            = "com.addhen.spotify.STATE_PARAM_ARTIST_ID";

    private String mArtistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        if (savedInstanceState == null) {
            mArtistId = getIntent().getStringExtra(INTENT_EXTRA_ARTIST_ID);
            addFragment(R.id.add_fragment_container, TrackFragment.newInstance(mArtistId));
        } else {
            mArtistId = savedInstanceState.getString(INSTANCE_STATE_PARAM_ARTIST_ID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putString(INSTANCE_STATE_PARAM_ARTIST_ID, mArtistId);
        }
        super.onSaveInstanceState(outState);
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

    private void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager()
                .beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }
}
