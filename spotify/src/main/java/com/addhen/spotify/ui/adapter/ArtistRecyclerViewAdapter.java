package com.addhen.spotify.ui.adapter;

import com.addhen.spotify.R;
import com.addhen.spotify.databinding.ArtistListItemBinding;
import com.addhen.spotify.model.ArtistModel;
import com.squareup.picasso.Picasso;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Add databinding support. This is a way to learn the new data-binding support coming to Android.
 * See for implementation details.
 * https://developer.android.com/tools/data-binding/guide.html
 *
 * Also used this sample codes to figure stuff out; https://github.com/saleeh93/data-binding-samples
 * and https://github.com/ashdavies/data-binding
 */
public class ArtistRecyclerViewAdapter
        extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ViewHolder> {

    private List<ArtistModel> mArtistList;

    private View mEmptyView;

    public ArtistRecyclerViewAdapter(final View emptyView) {
        mArtistList = new ArrayList<>();
        mEmptyView = emptyView;
        //Calling this to set the emptyView
        onDataSetChanged();
    }

    @BindingAdapter({"bind:imageUrl", "bind:placeholder", "bind:error"})
    public static void loadImage(ImageView view, String url, Drawable placholder, Drawable error) {
        Picasso.with(view.getContext()).load(url).placeholder(placholder).error(error).into(view);
    }

    @UiThread
    public void setAdapterItems(List<ArtistModel> artistList) {
        mArtistList.clear();
        mArtistList.addAll(artistList);
        onDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ArtistListItemBinding listItemBinding = DataBindingUtil
                .inflate(inflater, R.layout.artist_list_item, parent, false);
        final View view = listItemBinding.getRoot();
        return new ViewHolder(view, listItemBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ArtistModel artistModel = mArtistList.get(position);
        holder.bindData(artistModel);
    }

    /**
     * Sets an empty view when the adapter's data item gets to zero
     */
    private void onDataSetChanged() {
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ArtistListItemBinding mArtistListItemBinding;

        public ViewHolder(final View view, final ArtistListItemBinding artistListItemBinding) {
            super(view);
            mArtistListItemBinding = artistListItemBinding;
        }

        @UiThread
        public void bindData(ArtistModel artistModel) {
            mArtistListItemBinding.setArtist(artistModel);
        }
    }
}

