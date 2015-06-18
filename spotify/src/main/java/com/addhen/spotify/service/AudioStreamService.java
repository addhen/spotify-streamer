package com.addhen.spotify.service;


import com.addhen.spotify.BusProvider;
import com.addhen.spotify.R;
import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.state.PlaybackState;
import com.addhen.spotify.state.State;
import com.squareup.otto.Produce;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;

public class AudioStreamService extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {

    public static String TAG = AudioStreamService.class.getSimpleName();

    public static final String INTENT_EXTRA_PARAM_TRACK_MODEL_LIST
            = "com.addhen.spotify.service.INTENT_PARAM_TRACK_MODEL_LIST";

    public static final String INTENT_EXTRA_PARAM_TRACK_MODEL_LIST_INDEX
            = "com.addhen.spotify.service.INTENT_PARAM_TRACK_MODEL_LIST_INDEX";

    private static PowerManager.WakeLock mStartingService = null;

    private static WifiManager.WifiLock wifilock = null;

    private MediaPlayer mMediaPlayer;

    private PlaybackState mPlaybackState;

    private final IBinder mAudioStreamBinder = new AudioStreamServiceBinder();

    private int mCurrentPlayingSong;

    private List<TrackModel> mTrackModelList;

    private int mTrackModelListIndex;

    synchronized private static PowerManager.WakeLock getPhoneWakeLock(
            Context context) {
        if (mStartingService == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mStartingService = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    TAG);
        }
        return mStartingService;
    }

    synchronized private static WifiManager.WifiLock getPhoneWifiLock(
            Context context) {
        if (wifilock == null) {
            WifiManager manager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            wifilock = manager.createWifiLock(WifiManager.WIFI_MODE_FULL,
                    TAG);
        }
        return wifilock;
    }

    public static void bindWakefulTask(Context context, Intent intent) {

        if (!getPhoneWakeLock(context.getApplicationContext()).isHeld()) {
            getPhoneWakeLock(context.getApplicationContext()).acquire();
        }
        if (!getPhoneWifiLock(context.getApplicationContext()).isHeld()) {
            getPhoneWifiLock(context.getApplicationContext()).acquire();
        }
        context.startService(intent);
    }

    /*
     * Subclasses must implement this method so it executes any tasks
     * implemented in it.
     */
    protected void executeTask(Intent intent) {
        if (intent != null) {
            mTrackModelList = intent
                    .getParcelableArrayListExtra(INTENT_EXTRA_PARAM_TRACK_MODEL_LIST);
            mTrackModelListIndex = intent.getIntExtra(INTENT_EXTRA_PARAM_TRACK_MODEL_LIST_INDEX, 0);
            final TrackModel trackModel = mTrackModelList.get(mCurrentPlayingSong);
            mPlaybackState = new PlaybackState();
            setTrack(trackModel);
        }
    }

    private void setTrack(TrackModel trackModel) {
        mMediaPlayer.reset();
        updateState(mPlaybackState.sendState(State.LOADING));
        try {
            mMediaPlayer.setDataSource(trackModel.previewUrl);
        } catch (IOException e) {
            updateState(mPlaybackState.sendState(State.ERROR, e));
        } catch (IllegalStateException e) {
            updateState(mPlaybackState.sendState(State.ERROR, e));
        }
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BusProvider.getInstance().register(this);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setWakeMode(getApplication(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    private void handleIntent(Intent intent) {
        try {
            executeTask(intent);
        } finally {
            if (getPhoneWakeLock(this.getApplicationContext()).isHeld()
                    && getPhoneWakeLock(this.getApplicationContext()) != null) {
                getPhoneWakeLock(this.getApplicationContext()).release();
            }
            if (getPhoneWifiLock(this.getApplicationContext()).isHeld()
                    && getPhoneWifiLock(this.getApplicationContext()) != null) {
                getPhoneWifiLock(this.getApplicationContext()).release();
            }
        }
    }

    @Override
    public void onDestroy() {
        // release resources
        if (getPhoneWifiLock(this.getApplicationContext()).isHeld()
                && getPhoneWifiLock(this.getApplicationContext()) != null) {
            getPhoneWifiLock(this.getApplicationContext()).release();
        }
        if (getPhoneWakeLock(this.getApplicationContext()).isHeld()
                && getPhoneWakeLock(this.getApplicationContext()) != null) {
            getPhoneWakeLock(this.getApplicationContext()).release();
        }
        BusProvider.getInstance().unregister(this);
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAudioStreamBinder;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            updateState(mPlaybackState.sendState(State.BUFFERING));
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            updateState(mPlaybackState.sendState(State.PLAYING));
        }
        return false;
    }

    public class AudioStreamServiceBinder extends Binder {

        public AudioStreamService getAudoStreamService() {
            return AudioStreamService.this;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        updateState(mPlaybackState.sendState(State.STOPPED));
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        final Exception error = new Exception(getApplicationContext().getString(
                R.string.playback_error));
        BusProvider.getInstance().post(mPlaybackState.sendState(State.ERROR, error));
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        updateState(mPlaybackState.sendState(State.PLAYING));
    }

    public void playTrack() {
        if (mMediaPlayer.isPlaying()) {
            pauseTrack();
            return;
        }
        mMediaPlayer.start();
        updateState(mPlaybackState.sendState(State.PLAYING));
    }

    public void pauseTrack() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            updateState(mPlaybackState.sendState(State.PAUSED));
        }
    }

    public void seekTo(int to) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(to);
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public int getPlayingPosition() {
        try {
            return mMediaPlayer.getCurrentPosition();
        } catch (IllegalStateException e) {
            // For some reason this exception get thrown
            // when I destroy the fragment by navigating away using the back button.
            // No matter what I do, I can't escape it. I'm calling getCurrentPosition upon destorying
            // the activity
            // TODO: Improve code so this is not called when navigating away from the now playing screen
            e.printStackTrace();
            return 0;
        }
    }

    private void playSong(int trackModelListIndex) {
        mCurrentPlayingSong = trackModelListIndex;
        setTrack(mTrackModelList.get(mCurrentPlayingSong));
    }

    public void playNextTrack() {
        if (mCurrentPlayingSong < (mTrackModelList.size() - 1)) {
            playSong(mCurrentPlayingSong + 1);
            mCurrentPlayingSong = mCurrentPlayingSong + 1;
        } else {
            playSong(mTrackModelListIndex);
        }
    }

    public void playPreviousTrack() {
        if (mCurrentPlayingSong > 0) {
            playSong(mCurrentPlayingSong - 1);
            mCurrentPlayingSong = mCurrentPlayingSong - 1;
        } else {
            playSong(mTrackModelListIndex);
        }
    }

    @Produce
    public PlaybackState produceLastState() {
        return mPlaybackState;
    }

    private void updateState(PlaybackState playbackState) {
        BusProvider.getInstance().post(playbackState);
    }
}
