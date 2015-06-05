package com.addhen.spotify.adapter;

import com.addhen.spotify.R;
import com.addhen.spotify.activity.TopTracksActivity;
import com.addhen.spotify.model.Artist;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ArtistRecyclerViewAdapter
        extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();

    private int mBackground;

    private List<Artist> mArtistList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public long artistId;

        public final View mView;

        public final ImageView mImageView;

        public final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.coverPhoto);
            mTextView = (TextView) view.findViewById(R.id.artistName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText();
        }
    }

    public Artist getValueAt(int position) {
        return mArtistList.get(position);
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

    public ArtistRecyclerViewAdapter(Context context, List<Artist> items) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mArtistList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_list_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.artistId = mArtistList.get(position)._id;
        holder.mTextView.setText(mArtistList.get(position).name);
        // TODO: Replace launcher icon with one from the api
        Picasso.with(holder.mImageView.getContext())
                .load(R.mipmap.ic_launcher)
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, TopTracksActivity.class);
                intent.putExtra(TopTracksActivity.EXTRA_ARTIST_ID, holder.artistId);
                context.startActivity(intent);
            }
        });
    }
}

