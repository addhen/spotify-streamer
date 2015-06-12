package com.addhen.spotify.activity;

import com.addhen.spotify.R;
import com.addhen.spotify.fragment.ArtistFragment;

import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends BaseActivity {

    private ArtistFragment mArtistFragment;

    private static final String FRAG_TAG = "artist";

    public MainActivity() {
        super(R.layout.activity_main, R.menu.menu_main);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mArtistFragment = (ArtistFragment) getFragmentManager().findFragmentByTag(FRAG_TAG);
        if (mArtistFragment == null) {
            mArtistFragment = ArtistFragment.newInstance();
            replaceFragment(R.id.add_fragment_container, mArtistFragment, FRAG_TAG);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mArtistFragment.setArtistList(mArtistFragment.getArtistList());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
