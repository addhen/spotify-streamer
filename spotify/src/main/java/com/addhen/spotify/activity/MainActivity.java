package com.addhen.spotify.activity;

import com.addhen.spotify.BusProvider;
import com.addhen.spotify.R;
import com.addhen.spotify.fragment.ArtistFragment;
import com.addhen.spotify.fragment.TrackFragment;
import com.addhen.spotify.state.ArtistEvent;
import com.addhen.spotify.state.SearchClearedEvent;
import com.squareup.otto.Subscribe;

import android.content.Intent;
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
        if (isTwoPane()) {
            mArtistFragment = (ArtistFragment) getFragmentManager().findFragmentById(
                    R.id.fragment_artist_list);

        } else {
            mArtistFragment = (ArtistFragment) getFragmentManager().findFragmentByTag(FRAG_TAG);
            if (mArtistFragment == null) {
                mArtistFragment = ArtistFragment.newInstance();
                replaceFragment(R.id.add_artist_fragment_container, mArtistFragment, FRAG_TAG);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
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

    private boolean isTwoPane() {
        return findViewById(R.id.add_track_fragment_container) != null;
    }

    private void replaceTrackFrag(String artistId) {
        replaceFragment(R.id.add_track_fragment_container,
                TrackFragment.newInstance(artistId), "tracks");
    }

    @Subscribe
    public void artistClick(ArtistEvent event) {
        if (isTwoPane()) {
            replaceTrackFrag(event.artistId);
        } else {
            Intent intent = new Intent(this, TrackActivity.class);
            intent.putExtra(TrackActivity.INTENT_EXTRA_ARTIST_ID, event.artistId);
            intent.putExtra(TrackActivity.INTENT_EXTRA_ARTIST_NAME, event.artistName);
            startActivity(intent);
        }
    }

    @Subscribe
    public void searchCleared(SearchClearedEvent event) {
        if (isTwoPane()) {
            replaceTrackFrag("");
        }
    }
}
