package com.addhen.spotify.ui.activity;

import com.addhen.spotify.BusProvider;
import com.addhen.spotify.R;
import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.service.AudioStreamService;
import com.addhen.spotify.ui.fragment.PlaybackFragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.Optional;

public class PlaybackActivity extends BaseActivity {

    private static final String INTENT_EXTRA_PARAM_TRACK_MODEL_LIST
            = "com.addhen.spotify.ui.activity.INTENT_PARAM_TRACK_MODEL_LIST";

    private static final String INTENT_EXTRA_PARAM_TRACK_MODEL_LIST_INDEX
            = "com.addhen.spotify.ui.activity.INTENT_PARAM_TRACK_MODEL_LIST_INDEX";

    private static final String BUNDLE_STATE_PARAM_TRACK_LIST
            = "com.addhen.spotify.ui.activity.STATE_PARAM_TRACK_MODEL_LIST";

    private static final String BUNDLE_STATE_PARAM_TRACK_MODEL_LIST_INDEX
            = "com.addhen.spotify.ui.activity.BUNDLE_STATE_PARAM_TRACK_MODEL_LIST_INDEX";

    private List<TrackModel> mTrackModelList;

    private int mTrackModelListIndex;

    private static final String FRAG_TAG = "track";

    private PlaybackFragment mPlaybackFragment;

    private Intent mMusicServiceIntent;

    private AudioStreamService mAudioStreamService;

    private ShareActionProvider mShareActionProvider;


    @Optional
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    public PlaybackActivity() {
        super(R.layout.activity_playback, R.menu.menu_playback);
    }

    public static Intent getIntent(final Context context, ArrayList<TrackModel> trackModelList,
            int trackListIndex) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putParcelableArrayListExtra(INTENT_EXTRA_PARAM_TRACK_MODEL_LIST, trackModelList);
        intent.putExtra(INTENT_EXTRA_PARAM_TRACK_MODEL_LIST_INDEX, trackListIndex);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isTwoPane = getResources().getBoolean(R.bool.large_layout);
        if (isTwoPane) {
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setTitle(R.string.now_playing);
                }
            }
        }
        setupIntent(savedInstanceState);
    }

    private void setupIntent(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mTrackModelList = getIntent().getParcelableArrayListExtra(
                    INTENT_EXTRA_PARAM_TRACK_MODEL_LIST);
            mTrackModelListIndex = getIntent().getIntExtra(
                    INTENT_EXTRA_PARAM_TRACK_MODEL_LIST_INDEX, 0);
        } else {
            mTrackModelList = savedInstanceState.getParcelableArrayList(
                    BUNDLE_STATE_PARAM_TRACK_LIST);
            mTrackModelListIndex = savedInstanceState
                    .getInt(BUNDLE_STATE_PARAM_TRACK_MODEL_LIST_INDEX, 0);
        }
        mPlaybackFragment = (PlaybackFragment) getFragmentManager()
                .findFragmentByTag(FRAG_TAG);
        if (mPlaybackFragment == null) {
            mPlaybackFragment = PlaybackFragment
                    .newInstance((ArrayList) mTrackModelList, mTrackModelListIndex);
            replaceFragment(R.id.add_playback_fragment_container, mPlaybackFragment, FRAG_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState
                .putParcelableArrayList(BUNDLE_STATE_PARAM_TRACK_LIST, (ArrayList) mTrackModelList);
        savedInstanceState.putInt(BUNDLE_STATE_PARAM_TRACK_MODEL_LIST_INDEX, mTrackModelListIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        startAudioService((ArrayList) mTrackModelList, mTrackModelListIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlaybackFragment.setTrackModel(mTrackModelList, mTrackModelListIndex);
        mPlaybackFragment.setAudioStreamService(mAudioStreamService);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
        stopService(mMusicServiceIntent);
    }

    private void startAudioService(ArrayList<TrackModel> trackModels, int index) {
        mMusicServiceIntent = new Intent(PlaybackActivity.this, AudioStreamService.class);
        mMusicServiceIntent
                .putParcelableArrayListExtra(AudioStreamService.INTENT_EXTRA_PARAM_TRACK_MODEL_LIST,
                        trackModels);
        mMusicServiceIntent
                .putExtra(AudioStreamService.INTENT_EXTRA_PARAM_TRACK_MODEL_LIST_INDEX, index);
        bindService(mMusicServiceIntent, mConnection, BIND_AUTO_CREATE);
        AudioStreamService.sendWakefulTask(PlaybackActivity.this, mMusicServiceIntent);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder binder) {
            AudioStreamService.AudioStreamServiceBinder b
                    = (AudioStreamService.AudioStreamServiceBinder) binder;
            mAudioStreamService = b.getAudoStreamService();
            mPlaybackFragment.setAudioStreamService(mAudioStreamService);
        }

        public void onServiceDisconnected(ComponentName className) {
            mAudioStreamService = null;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.menu_playback_share) {
            final TrackModel trackModel = mPlaybackFragment.getCurrentlyPlayingSong();
            setShareIntent(trackModel);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playback, menu);
        MenuItem item = menu.findItem(R.id.menu_playback_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        final TrackModel trackModel = mPlaybackFragment.getCurrentlyPlayingSong();
        setShareIntent(trackModel);
        return true;
    }

    private void setShareIntent(@NonNull TrackModel trackModel) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, trackModel.externalUrl);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(share);
        }
    }
}
