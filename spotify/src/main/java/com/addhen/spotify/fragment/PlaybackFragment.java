package com.addhen.spotify.fragment;

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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

    private int mCurrentPlayingSong;

    private Drawable mPauseDrawable;

    private Drawable mPlayDrawable;

    private PlaybackPresenter mPlaybackPresenter;

    private AudioStreamService mAudioStreamService;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private ScheduledFuture<?> mScheduleFuture;

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
        playSong(mTrackModelListIndex);
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
                                                            if (mAudioStreamService != null) {
                                                                mAudioStreamService
                                                                        .seekTo(seekBar
                                                                                .getProgress());
                                                            }
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
        super.onDestroy();
        mPlaybackPresenter.destroy();
        mExecutorService.shutdown();
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

    public void setAudioStreamService(AudioStreamService audioStreamService) {
        mAudioStreamService = audioStreamService;
    }

    @OnClick({R.id.playbackPrevious, R.id.playbackNext, R.id.playbackPause})
    public void controlPressed(View view) {
        final int id = view.getId();
        if (id == R.id.playbackPrevious) {
            playPreviousTrack();
        } else if (id == R.id.playbackNext) {
            playNextTrack();
        } else if (id == R.id.playbackPause) {
            if (mAudioStreamService != null) {
                mAudioStreamService.playTrack();
            }
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
        if (mAudioStreamService != null) {
            final int duration = mAudioStreamService.getMediaPlayer().getDuration();
            mPlaybackSeekbar.setMax(duration);
            mPlaybackEndTime.setText(Utils.formatMillis(duration));
        }
    }

    private void updateProgress() {
        if (mAudioStreamService != null) {
            mPlaybackSeekbar.setProgress(mAudioStreamService.getPlayingPosition());
        }
    }

    private void playSong(int trackModelListIndex) {
        mCurrentPlayingSong = trackModelListIndex;
        final TrackModel trackModel = mTrackModelList.get(mCurrentPlayingSong);
        mPlaybackPresenter.setTrackModel(trackModel.coverPhoto);
        updateMediaDescription(trackModel);
    }

    private void playNextTrack() {
        //setViewGone(mPlaybackNext, false);
        if (mCurrentPlayingSong < (mTrackModelList.size() - 1)) {
            playSong(mCurrentPlayingSong + 1);
            mCurrentPlayingSong = mCurrentPlayingSong + 1;
        } else {
            // Play the original strong passed from the intent
            playSong(mTrackModelListIndex);
            //setViewGone(mPlaybackNext, true);
        }
        if (mAudioStreamService != null) {
            mAudioStreamService.playNextTrack();
        }
    }

    private void playPreviousTrack() {
        if (mCurrentPlayingSong > 0) {
            playSong(mCurrentPlayingSong - 1);
            mCurrentPlayingSong = mCurrentPlayingSong - 1;
        } else {
            // Play the original index of the song passed from the intent.
            playSong(mTrackModelListIndex);
        }
        if (mAudioStreamService != null) {
            mAudioStreamService.playPreviousTrack();
        }
    }

    public TrackModel getCurrentlyPlayingSong() {
        return mTrackModelList.get(mCurrentPlayingSong);
    }

    @Subscribe
    public void updatePlaybackState(final PlaybackState playbackState) {
        if (playbackState == null) {
            return;
        }
        if (playbackState.isPlaying()) {
            mPlaybackPresenter.playTrack();
        } else if (playbackState.isPaused()) {
            mPlaybackPresenter.pauseTrack();
        } else if (playbackState.isStopped()) {
            stopped();
        } else if (playbackState.isLoading()) {
            loading();
        } else if (playbackState.isError()) {
            showError(playbackState.getError());
        }
    }
}
