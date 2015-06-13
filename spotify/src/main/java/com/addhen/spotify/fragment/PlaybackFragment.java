package com.addhen.spotify.fragment;

import com.addhen.spotify.R;
import com.addhen.spotify.model.TrackModel;
import com.addhen.spotify.presenter.PlaybackPresenter;
import com.addhen.spotify.util.Utils;
import com.addhen.spotify.view.PlaybackView;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import butterknife.OnClick;

import static android.widget.SeekBar.OnSeekBarChangeListener;

public class PlaybackFragment extends BaseFragment implements PlaybackView {

    private static final String ARGUMENT_KEY_TRACK_MODEL
            = "com.addhen.spotify.ARGUMENT_TRACK_MODEL";

    private TrackModel mTrackModel;

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;

    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    @InjectView(R.id.playbackPause)
    ImageView mPlaybackPause;

    @InjectView(R.id.playbackNext)
    ImageView mPlaybackNext;

    @InjectView(R.id.playbackPrevious)
    ImageView mPlaybackPrevious;

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

    @InjectView(R.id.playbackTrackLoadingProgress)
    ProgressBar mPlaybackTrackLoadingProgress;

    @InjectView(R.id.controllers)
    View mControllers;

    @InjectView(R.id.backgroundCoverArt)
    ImageView mBackgroundImage;

    private Drawable mPauseDrawable;

    private Drawable mPlayDrawable;

    private final Handler mHandler = new Handler();

    private PlaybackPresenter mPlaybackPresenter;

    private MediaPlayer mMediaPlayer;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;

    public PlaybackFragment() {
        super(R.layout.fragmet_track_playback, 0);
    }

    public static PlaybackFragment newInstance(@NonNull TrackModel trackModel) {
        PlaybackFragment fragment = new PlaybackFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGUMENT_KEY_TRACK_MODEL, trackModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPlaybackPresenter.setView(this);
        mPauseDrawable = getActivity().getResources()
                .getDrawable(R.drawable.ic_pause_circle_outline_white_48dp);
        mPlayDrawable = getActivity().getResources()
                .getDrawable(R.drawable.ic_play_circle_outline_white_48dp);
        final TrackModel trackModel = getArguments().getParcelable(ARGUMENT_KEY_TRACK_MODEL);
        mPlaybackPresenter.setTrackModel(trackModel);
        updateMediaDescription(trackModel);
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
                                                            mPlaybackPresenter
                                                                    .seekTo(seekBar.getProgress());
                                                            updateSeekbar();
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
    public void musicPlayerPrepared() {
        mControllers.setVisibility(View.VISIBLE);
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
        mPlaybackPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlaybackPresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlaybackPresenter.destroy();
        mExecutorService.shutdown();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlaybackPresenter = new PlaybackPresenter(mMediaPlayer);
    }

    public void setTrackModel(final TrackModel trackModel) {
        mTrackModel = trackModel;
    }

    public TrackModel getTrackModel() {
        return mTrackModel;
    }

    @OnClick({R.id.playbackPrevious, R.id.playbackNext, R.id.playbackPause})
    public void controlPressed(View view) {
        final int id = view.getId();
        if (id == R.id.playbackPrevious) {
            mPlaybackPresenter.previousTrack();
        } else if (id == R.id.playbackNext) {
            mPlaybackPresenter.nextTrack();
        } else if (id == R.id.playbackPause) {
            mPlaybackPresenter.playTrack();
        }
    }

    @Override
    public void playing() {
        mPlaybackTrackLoadingProgress.setVisibility(View.INVISIBLE);
        mPlaybackPause.setVisibility(View.VISIBLE);
        mPlaybackPause.setImageDrawable(mPauseDrawable);
        mControllers.setVisibility(View.VISIBLE);
        updateDuration();
    }

    @Override
    public void paused() {
        mControllers.setVisibility(View.VISIBLE);
        mPlaybackTrackLoadingProgress.setVisibility(View.INVISIBLE);
        mPlaybackPause.setVisibility(View.VISIBLE);
        mPlaybackPause.setImageDrawable(mPlayDrawable);
    }

    @Override
    public void stopped() {
        mPlaybackTrackLoadingProgress.setVisibility(View.INVISIBLE);
        mPlaybackPause.setVisibility(View.VISIBLE);
        mPlaybackPause.setImageDrawable(mPlayDrawable);
    }

    @Override
    public void seeked() {
        mPlaybackPause.setVisibility(View.INVISIBLE);
        mPlaybackTrackLoadingProgress.setVisibility(View.VISIBLE);
        mPlaybackTrackName.setText(R.string.loading);
        stopSeekbarUpdate();
    }

    @Override
    public void showError(String message) {
        showSnabackar(getView(), message);
    }

    @Override
    public void loading() {
        mPlaybackTrackLoadingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mPlaybackTrackLoadingProgress.setVisibility(View.GONE);
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
        int duration = mMediaPlayer.getDuration();
        mPlaybackSeekbar.setMax(duration);
        mPlaybackEndTime.setText(Utils.formatMillis(duration));
    }


    private void updateProgress() {
        mPlaybackSeekbar.setProgress(mMediaPlayer.getCurrentPosition());
    }
}
