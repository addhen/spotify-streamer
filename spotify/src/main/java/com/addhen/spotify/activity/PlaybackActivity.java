package com.addhen.spotify.activity;

import com.addhen.spotify.BusProvider;
import com.addhen.spotify.R;
import com.addhen.spotify.fragment.PlaybackFragment;
import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.service.AudioStreamService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class PlaybackActivity extends BaseActivity {

    private static final String INTENT_EXTRA_PARAM_TRACK_MODEL_LIST
            = "com.addhen.spotify.activity.INTENT_PARAM_TRACK_MODEL_LIST";

    private static final String INTENT_EXTRA_PARAM_TRACK_MODEL_LIST_INDEX
            = "com.addhen.spotify.activity.INTENT_PARAM_TRACK_MODEL_LIST_INDEX";

    private static final String INTENT_STATE_PARAM_TRACK_LIST
            = "com.addhen.spotify.activity.STATE_PARAM_TRACK_MODEL_LIST";

    private static final String INTENT_STATE_PARAM_TRACK_MODEL_LIST_INDEX
            = "com.addhen.spotify.activity.STATE_PARAM_TRACK_MODEL_LIST_INDEX";

    private List<TrackModel> mTrackModelList;

    private int mTrackModelListIndex;

    private static final String FRAG_TAG = "track";

    private PlaybackFragment mPlaybackFragment;

    private Intent mMusicSerivceIntent;

    private AudioStreamService mAudioStreamService;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    public PlaybackActivity() {
        super(R.layout.activity_playback, R.menu.menu_main);
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
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.now_playing);
        }
        if (savedInstanceState == null) {
            mTrackModelList = getIntent().getParcelableArrayListExtra(
                    INTENT_EXTRA_PARAM_TRACK_MODEL_LIST);
            mTrackModelListIndex = getIntent().getIntExtra(
                    INTENT_EXTRA_PARAM_TRACK_MODEL_LIST_INDEX, 0);
        } else {
            mTrackModelList = savedInstanceState.getParcelableArrayList(
                    INTENT_STATE_PARAM_TRACK_LIST);
            mTrackModelListIndex = savedInstanceState
                    .getInt(INTENT_STATE_PARAM_TRACK_MODEL_LIST_INDEX, 0);
        }
        mPlaybackFragment = (PlaybackFragment) getFragmentManager()
                .findFragmentByTag(FRAG_TAG);
        if (mPlaybackFragment == null) {
            mPlaybackFragment = PlaybackFragment
                    .newInstance((ArrayList) mTrackModelList, mTrackModelListIndex);
            replaceFragment(R.id.add_fragment_container, mPlaybackFragment, FRAG_TAG);
        }
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
        stopService(mMusicSerivceIntent);
    }

    private void startAudioService(ArrayList<TrackModel> trackModels, int index) {
        mMusicSerivceIntent = new Intent(PlaybackActivity.this, AudioStreamService.class);
        mMusicSerivceIntent
                .putParcelableArrayListExtra(AudioStreamService.INTENT_EXTRA_PARAM_TRACK_MODEL_LIST,
                        trackModels);
        mMusicSerivceIntent
                .putExtra(AudioStreamService.INTENT_EXTRA_PARAM_TRACK_MODEL_LIST_INDEX, index);
        bindService(mMusicSerivceIntent, mConnection, BIND_AUTO_CREATE);
        AudioStreamService.bindWakefulTask(PlaybackActivity.this, mMusicSerivceIntent);
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
}
