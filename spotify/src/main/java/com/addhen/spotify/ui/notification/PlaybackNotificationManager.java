package com.addhen.spotify.ui.notification;

import com.addhen.spotify.R;
import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.service.AudioStreamService;
import com.addhen.spotify.state.PlaybackState;
import com.addhen.spotify.ui.activity.PlaybackActivity;
import com.addhen.spotify.ui.fragment.SettingsFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;

import java.util.ArrayList;

/**
 * A one stop shop for all notifications for playback.
 * Using media session compact for notification media controls.
 */
public class PlaybackNotificationManager extends BroadcastReceiver {

    public static final String INTENT_ACTION_PAUSE = "com.addhen.spotify.ui.INTENT_ACTION.PAUSE";

    public static final String INTENT_ACTION_PLAY = "com.addhen.spotify.ui.INTENT_ACTION.PLAY";

    public static final String INTENT_ACTION_NEXT = "com.addhen.spotify.ui.INTENT_ACTION.NEXT";

    public static final String INTENT_ACTION_PREV = "com.addhen.spotify.ui.INTENT_ACTION.PREV";


    private static final int NOTIFICATION_ID = 412;

    private static final int REQUEST_CODE = 100;

    private final AudioStreamService mAudioStreamService;

    private final NotificationManager mNotificationManager;

    private final PendingIntent mPauseIntent;

    private final PendingIntent mPlayIntent;

    private final PendingIntent mPreviousIntent;

    private final PendingIntent mNextIntent;

    private final int mNotificationColor;

    private boolean mStarted = false;

    private NotificationCompat.Builder mNotificationBuilder;

    private PlaybackState mPlaybackState;

    private MediaControllerCompat mController;

    private MediaControllerCompat.TransportControls mTransportControls;

    private MediaSessionCompat.Token mSessionToken;

    private final Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mNotificationBuilder.setLargeIcon(bitmap);
            mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case INTENT_ACTION_PAUSE:
                mAudioStreamService.pauseTrack();
                break;
            case INTENT_ACTION_PLAY:
                mAudioStreamService.playTrack();
                break;
            case INTENT_ACTION_NEXT:
                mAudioStreamService.playNextTrack();
                break;
            case INTENT_ACTION_PREV:
                mAudioStreamService.playPreviousTrack();
                break;
        }
    }

    public PlaybackNotificationManager(AudioStreamService audioStreamService) {
        mAudioStreamService = audioStreamService;
        updateSessionToken();

        mNotificationColor = Color.DKGRAY;

        mNotificationManager = (NotificationManager) audioStreamService
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String pkg = mAudioStreamService.getPackageName();
        mPauseIntent = PendingIntent.getBroadcast(mAudioStreamService, REQUEST_CODE,
                new Intent(INTENT_ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPlayIntent = PendingIntent.getBroadcast(mAudioStreamService, REQUEST_CODE,
                new Intent(INTENT_ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPreviousIntent = PendingIntent.getBroadcast(mAudioStreamService, REQUEST_CODE,
                new Intent(INTENT_ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mNextIntent = PendingIntent.getBroadcast(mAudioStreamService, REQUEST_CODE,
                new Intent(INTENT_ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mNotificationManager.cancelAll();
    }

    public void startNotification() {
        if (!mStarted) {
            mPlaybackState = mAudioStreamService.produceLastState();
            Notification notification = createNotification();
            if (notification != null) {
                mController.registerCallback(mCb);
                IntentFilter filter = new IntentFilter();
                filter.addAction(INTENT_ACTION_NEXT);
                filter.addAction(INTENT_ACTION_PAUSE);
                filter.addAction(INTENT_ACTION_PLAY);
                filter.addAction(INTENT_ACTION_PREV);
                mAudioStreamService.registerReceiver(this, filter);
                mAudioStreamService.startForeground(NOTIFICATION_ID, notification);
                mStarted = true;
            }
        }
    }

    public void stopNotification() {
        if (mStarted) {
            mStarted = false;
            mController.unregisterCallback(mCb);
            try {
                mNotificationManager.cancel(NOTIFICATION_ID);
                mAudioStreamService.unregisterReceiver(this);
            } catch (IllegalArgumentException ex) {
                // Do nothing
            }
            mAudioStreamService.stopForeground(true);
        }
    }

    private void updateSessionToken() {
        MediaSessionCompat.Token freshToken = mAudioStreamService.getSessionToken();
        if (mSessionToken == null || !mSessionToken.equals(freshToken)) {
            if (mController != null) {
                mController.unregisterCallback(mCb);
            }
            mSessionToken = freshToken;
            try {
                mController = new MediaControllerCompat(mAudioStreamService, mSessionToken);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mTransportControls = mController.getTransportControls();
            if (mStarted) {
                mController.registerCallback(mCb);
            }
        }
    }

    private final MediaControllerCompat.Callback mCb = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            mPlaybackState = mAudioStreamService.produceLastState();
            if (mPlaybackState.isStopped()) {
                stopNotification();
            } else {
                Notification notification = createNotification();
                if (notification != null) {
                    mNotificationManager.notify(NOTIFICATION_ID, notification);
                }
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }
    };

    private Notification createNotification() {

        final TrackModel trackModel = mAudioStreamService.getTrackModelList()
                .get(mAudioStreamService.getCurrentPlayingTrack());
        mNotificationBuilder = new NotificationCompat.Builder(mAudioStreamService);
        int playPauseButtonPosition = 0;
        if (mPlaybackState.isPlaying()) {
            mNotificationBuilder.addAction(R.drawable.ic_skip_previous_white_48dp,
                    mAudioStreamService.getString(R.string.skip_previous), mPreviousIntent);
            playPauseButtonPosition = 1;
        }
        addPlayPauseAction(mNotificationBuilder);
        if (mPlaybackState.isPlaying()) {
            mNotificationBuilder.addAction(R.drawable.ic_skip_next_white_48dp,
                    mAudioStreamService.getString(R.string.skip_next), mNextIntent);
        }
        if (trackModel.coverPhoto != null) {
            Picasso.with(mAudioStreamService.getApplicationContext())
                    .load(R.drawable.ic_default_music_playing).into(mTarget);

        }
        mNotificationBuilder
                .setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(
                        new int[]{playPauseButtonPosition}).setMediaSession(mSessionToken))
                .setColor(mNotificationColor)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setUsesChronometer(true)
                .setContentIntent(createContentIntent())
                .setContentTitle(trackModel.name);
        final boolean showOnLockScreen = PreferenceManager
                .getDefaultSharedPreferences(mAudioStreamService.getApplicationContext())
                .getBoolean(
                        SettingsFragment.ENABLE_PLAYBACK_NOTIFICATION, false);
        if (showOnLockScreen) {
            mNotificationBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        setNotificationPlaybackState(mNotificationBuilder);
        return mNotificationBuilder.build();
    }

    private PendingIntent createContentIntent() {
        Intent intent = new Intent(mAudioStreamService, PlaybackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(mAudioStreamService, REQUEST_CODE, PlaybackActivity
                        .getIntent(mAudioStreamService,
                                (ArrayList) mAudioStreamService.getTrackModelList(),
                                mAudioStreamService.getCurrentPlayingTrack()),
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void addPlayPauseAction(NotificationCompat.Builder builder) {
        String label;
        int icon;
        PendingIntent intent;
        if (mPlaybackState.isPlaying()) {
            label = mAudioStreamService.getString(R.string.pause);
            icon = R.drawable.ic_pause_circle_outline_white_48dp;
            intent = mPauseIntent;
        } else {
            label = mAudioStreamService.getString(R.string.play);
            icon = R.drawable.ic_play_circle_outline_white_48dp;
            intent = mPlayIntent;
        }
        builder.addAction(new NotificationCompat.Action(icon, label, intent));
    }

    private void setNotificationPlaybackState(NotificationCompat.Builder builder) {
        if (mPlaybackState == null || !mStarted) {
            mAudioStreamService.stopForeground(true);
            return;
        }
        if (mPlaybackState.isPlaying()) {
            builder
                    .setWhen(System.currentTimeMillis() - mAudioStreamService.getPlayingPosition())
                    .setShowWhen(true)
                    .setUsesChronometer(true);
        } else {
            builder
                    .setWhen(0)
                    .setShowWhen(false)
                    .setUsesChronometer(false);
        }
        builder.setOngoing(mPlaybackState.isPlaying());
    }
}
