package com.addhen.spotify.ui.fragment;

import com.addhen.spotify.BusProvider;
import com.addhen.spotify.R;
import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.presenter.PlaybackPresenter;
import com.addhen.spotify.service.AudioStreamService;
import com.addhen.spotify.state.PlaybackState;
import com.addhen.spotify.util.Utils;
import com.addhen.spotify.view.PlaybackView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import butterknife.OnClick;

import static android.widget.SeekBar.OnSeekBarChangeListener;

public class PlaybackFragment extends BaseFragment implements PlaybackView {

    private static final String ARGUMENT_KEY_TRACK_MODEL_LIST
            = "com.addhen.spotify.ARGUMENT_TRACK_MODEL_LIST";

    private static final String ARGUMENT_KEY_TRACK_MODEL_LIST_INDEX
            = "com.addhen.spotify.ARGUMENT_TRACK_MODEL_LIST_INDEX";

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;

    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private final Handler mHandler = new Handler();

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    @InjectView(R.id.playbackPause)
    ImageView mPlaybackPause;

    @InjectView(R.id.playbackStartTime)
    TextView mPlaybackStartTime;

    @InjectView(R.id.playbackEndTime)
    TextView mPlaybackEndTime;

    @InjectView(R.id.playbackSeekBar)
    SeekBar mPlaybackSeekbar;

    @InjectView(R.id.playbackAlbumName)
    TextView mPlaybackAlbumName;

    @InjectView(R.id.playbackTrackArtistName)
    TextView mPlaybackTrackArtistName;

    @InjectView(R.id.playbackTrackName)
    TextView mPlaybackTrackName;

    @InjectView(R.id.playbackStatusInfo)
    TextView mPlaybackStatusInfo;

    @InjectView(R.id.playbackTrackLoadingProgress)
    ProgressBar mPlaybackTrackLoadingProgress;

    @InjectView(R.id.controllers)
    View mControllers;

    @InjectView(R.id.backgroundCoverArt)
    ImageView mBackgroundImage;

    private List<TrackModel> mTrackModelList;

    private int mTrackModelListIndex;

    private Drawable mPauseDrawable;

    private Drawable mPlayDrawable;

    private PlaybackPresenter mPlaybackPresenter;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private ScheduledFuture<?> mScheduleFuture;

    private AudioStreamService mAudioStreamService;

    private Intent mMusicServiceIntent;

    public PlaybackFragment() {
        super(R.layout.fragmet_track_playback, 0);
    }

    public static PlaybackFragment newInstance(@NonNull ArrayList<TrackModel> trackModelList,
            int trackModelListIndex) {
        PlaybackFragment fragment = new PlaybackFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARGUMENT_KEY_TRACK_MODEL_LIST, trackModelList);
        bundle.putInt(ARGUMENT_KEY_TRACK_MODEL_LIST_INDEX, trackModelListIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPlaybackPresenter.setView(this);
        mPauseDrawable = getActivity().getResources()
                .getDrawable(R.drawable.ic_pause_circle_outline_white_48dp);
        mPlayDrawable = getActivity().getResources()
                .getDrawable(R.drawable.ic_play_circle_outline_white_48dp);

        mTrackModelList = getArguments()
                .getParcelableArrayList(ARGUMENT_KEY_TRACK_MODEL_LIST);
        mTrackModelListIndex = getArguments().getInt(ARGUMENT_KEY_TRACK_MODEL_LIST_INDEX,
                0);
        mPlaybackSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()

                                                    {
                                                        @Override
                                                        public void onProgressChanged(
                                                                SeekBar seekBar, int progress,
                                                                boolean fromUser) {
                                                            mPlaybackStartTime.setText(
                                                                    Utils.formatMillis(progress));
                                                        }

                                                        @Override
                                                        public void onStartTrackingTouch(
                                                                SeekBar seekBar) {
                                                            stopSeekbarUpdate();
                                                        }

                                                        @Override
                                                        public void onStopTrackingTouch(
                                                                SeekBar seekBar) {
                                                            mAudioStreamService
                                                                    .seekTo(seekBar.getProgress());
                                                            mPlaybackPresenter.seekTo();
                                                        }
                                                    }

        );
    }

    @Override
    public void updateSeekbar() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void loadCoverArt(String url) {
        Picasso.with(getAppContext()).load(url).into(mBackgroundImage);
    }

    @Override
    public void buffering() {
        mPlaybackTrackLoadingProgress.setVisibility(View.VISIBLE);
        mPlaybackStatusInfo.setText(R.string.playback_buffering);
        mPlaybackStatusInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        mPlaybackPresenter.resume();
        startAudioService((ArrayList) mTrackModelList, mTrackModelListIndex);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlaybackPresenter.pause();
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        mPlaybackPresenter.pause();
    }

    @Override
    public void onDestroy() {
        getActivity().getApplication().unbindService(mConnection);
        mAudioStreamService = null;
        mExecutorService.shutdown();
        super.onDestroy();
        mPlaybackPresenter.destroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlaybackPresenter = new PlaybackPresenter();
    }

    public void setTrackModel(final List<TrackModel> trackModelList, int trackModelListIndex) {
        mTrackModelList = trackModelList;
        mTrackModelListIndex = trackModelListIndex;
    }

    @OnClick({R.id.playbackPrevious, R.id.playbackNext, R.id.playbackPause})
    public void controlPressed(View view) {
        final int id = view.getId();
        if (id == R.id.playbackPrevious) {
            playPreviousTrack();
        } else if (id == R.id.playbackNext) {
            playNextTrack();
        } else if (id == R.id.playbackPause) {
            mAudioStreamService.playTrack();
        }
    }

    @Override
    public void playing() {
        mPlaybackTrackLoadingProgress.setVisibility(View.INVISIBLE);
        mPlaybackStatusInfo.setVisibility(View.INVISIBLE);
        mPlaybackPause.setImageDrawable(mPauseDrawable);
        mControllers.setVisibility(View.VISIBLE);
        updateDuration();
    }

    @Override
    public void paused() {
        mControllers.setVisibility(View.VISIBLE);
        mPlaybackTrackLoadingProgress.setVisibility(View.INVISIBLE);
        mPlaybackPause.setImageDrawable(mPlayDrawable);
    }

    @Override
    public void stopped() {
        mPlaybackTrackLoadingProgress.setVisibility(View.INVISIBLE);
        mPlaybackStatusInfo.setVisibility(View.INVISIBLE);
        mPlaybackPause.setImageDrawable(mPlayDrawable);
    }

    @Override
    public void seeked() {
        stopSeekbarUpdate();
    }

    @Override
    public void showError(String message) {
        showSnabackar(getView(), message);
    }

    @Override
    public void loading() {
        mPlaybackTrackLoadingProgress.setVisibility(View.VISIBLE);
        mPlaybackStatusInfo.setText(R.string.playback_loading);
        mPlaybackStatusInfo.setVisibility(View.VISIBLE);
        mControllers.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideLoading() {
        mPlaybackTrackLoadingProgress.setVisibility(View.GONE);
        mPlaybackStatusInfo.setVisibility(View.INVISIBLE);
        mControllers.setVisibility(View.VISIBLE);
    }

    @Override
    public Context getAppContext() {
        return getActivity().getApplication();
    }

    private void updateMediaDescription(TrackModel trackModel) {
        if (trackModel == null) {
            return;
        }
        mPlaybackTrackArtistName.setText(trackModel.artistName);
        mPlaybackAlbumName.setText(trackModel.album);
        mPlaybackTrackName.setText(trackModel.name);
    }

    private void updateDuration() {
        int duration = mAudioStreamService.getMediaPlayer().getDuration();
        mPlaybackSeekbar.setMax(duration);
        mPlaybackEndTime.setText(Utils.formatMillis(duration));
    }

    private void updateProgress() {
        if (mAudioStreamService != null) {
            mPlaybackSeekbar.setProgress(mAudioStreamService.getPlayingPosition());
        }
    }

    private void updateCurrentlyPlayInfo() {
        final TrackModel trackModel = mTrackModelList
                .get(mAudioStreamService.getCurrentPlayingTrack());
        mPlaybackPresenter.setTrackModel(trackModel.coverPhoto);
        updateMediaDescription(trackModel);
    }

    private void playNextTrack() {
        if (mAudioStreamService != null) {
            mAudioStreamService.playNextTrack();
        }
    }

    private void playPreviousTrack() {
        if (mAudioStreamService != null) {
            mAudioStreamService.playPreviousTrack();
        }
    }

    public int getCurrentlyPlayingTrack() {
        if (mAudioStreamService != null) {
            return mAudioStreamService.getCurrentPlayingTrack();
        }
        return mTrackModelListIndex;
    }

    @Subscribe
    public void updatePlaybackState(final PlaybackState playbackState) {
        if (playbackState == null) {
            return;
        }
        switch (playbackState.state) {
            case PLAYING:
                updateCurrentlyPlayInfo();
                mPlaybackPresenter.playTrack();
                break;
            case PAUSED:
                mPlaybackPresenter.pauseTrack();
                break;
            case LOADING:
                loading();
                break;
            case ERROR:
                showError(playbackState.getError());
                break;
            case SKIPPED_NEXT:
            case SKIPPED_PREVIOUS:
                updateCurrentlyPlayInfo();
                break;
            case STOPPED:
                mTrackModelListIndex = -1;
        }
    }


    public void startAudioService(ArrayList<TrackModel> trackModels, int index) {
        mMusicServiceIntent = new Intent(getActivity(), AudioStreamService.class);
        mMusicServiceIntent
                .putParcelableArrayListExtra(AudioStreamService.INTENT_EXTRA_PARAM_TRACK_MODEL_LIST,
                        trackModels);
        mMusicServiceIntent
                .putExtra(AudioStreamService.INTENT_EXTRA_PARAM_TRACK_MODEL_LIST_INDEX, index);
        getActivity().getApplication().bindService(mMusicServiceIntent, mConnection,
                Context.BIND_AUTO_CREATE);
        AudioStreamService.sendWakefulTask(getActivity(), mMusicServiceIntent);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder binder) {
            AudioStreamService.AudioStreamServiceBinder b
                    = (AudioStreamService.AudioStreamServiceBinder) binder;
            mAudioStreamService = b.getAudoStreamService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mAudioStreamService = null;
        }
    };
}
