package com.addhen.spotify.adapter;

import com.addhen.spotify.R;
import com.addhen.spotify.databinding.TrackListItemBinding;
import com.addhen.spotify.model.TrackModel;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class TrackRecyclerViewAdapter
        extends RecyclerView.Adapter<TrackRecyclerViewAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();

    private int mBackground;

    private List<TrackModel> mTrackList;

    private View mEmptyView;

    public TrackRecyclerViewAdapter(final Context context, final View emptyView) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mTrackList = new ArrayList<>();
        mEmptyView = emptyView;
        //Calling this to set the emptyView
        onDataSetChanged();
    }

    @BindingAdapter({"bind:imageUrl", "bind:placeholder", "bind:error"})
    public static void loadImage(ImageView view, String url, Drawable placholder, Drawable error) {
        Picasso.with(view.getContext()).load(url).placeholder(placholder).error(error).into(view);
    }

    public void setAdapterItems(List<TrackModel> trackList) {
        mTrackList.clear();
        mTrackList.addAll(trackList);
        onDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final TrackListItemBinding trackListItemBinding = DataBindingUtil
                .inflate(inflater, R.layout.track_list_item, parent, false);

        final View view = trackListItemBinding.getRoot();
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view, trackListItemBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        TrackModel trackModel = mTrackList.get(position);
        holder.bindData(trackModel);
        // TODO: Launch activity for playing music
    }

    /**
     * Sets an empty view when the adapter's data item gets to zero
     */
    private void onDataSetChanged() {
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TrackListItemBinding mTrackListItemBinding;

        public final View mView;

        public ViewHolder(final View view, final TrackListItemBinding trackListItemBinding) {
            super(view);
            mView = view;
            mTrackListItemBinding = trackListItemBinding;
        }

        @UiThread
        public void bindData(TrackModel trackModel) {
            mTrackListItemBinding.setTrack(trackModel);
        }
    }
}

