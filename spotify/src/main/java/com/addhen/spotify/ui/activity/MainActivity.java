package com.addhen.spotify.ui.activity;

import com.addhen.spotify.BusProvider;
import com.addhen.spotify.R;
import com.addhen.spotify.ui.fragment.ArtistFragment;
import com.addhen.spotify.ui.fragment.TrackFragment;
import com.addhen.spotify.model.ArtistModel;
import com.addhen.spotify.state.ArtistEvent;
import com.addhen.spotify.state.SearchClearedEvent;
import com.addhen.spotify.util.Utils;
import com.squareup.otto.Subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String INTENT_PARAM_ARTIST_MODEL_LIST
            = "com.addhen.spotify.ui.activity.INTENT_PARAM_ARTIST_MODEL_LIST";

    private ArtistFragment mArtistFragment;

    private static final String FRAG_TAG = "artist";

    private List<ArtistModel> mArtistModelList;

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
        if (savedInstanceState != null) {
            mArtistModelList = savedInstanceState
                    .getParcelableArrayList(INTENT_PARAM_ARTIST_MODEL_LIST);
        } else {
            mArtistModelList = mArtistFragment.getArtistList();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(INTENT_PARAM_ARTIST_MODEL_LIST,
                (ArrayList) mArtistFragment.getArtistList());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        if (!Utils.isEmpty(mArtistModelList)) {
            mArtistFragment.showArtists(mArtistModelList);
        }
    }

    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mArtistFragment.setArtistList(mArtistModelList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(SettingsActivity.getIntent(getApplicationContext()));
            return true;
        } else if (id == android.R.id.home) {
            finish();
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
            // TODO: Refresh the list instead of making a new request to spotify api when
            // an artist is empty
            replaceTrackFrag("");
        }
    }
}
