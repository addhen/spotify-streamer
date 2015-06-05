package com.addhen.spotify.adapter;

import com.addhen.spotify.R;
import com.addhen.spotify.activity.TrackActivity;
import com.addhen.spotify.model.ArtistModel;
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

import java.util.ArrayList;
import java.util.List;

public class ArtistRecyclerViewAdapter
        extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();

    private int mBackground;

    private List<ArtistModel> mArtistList;

    public ArtistRecyclerViewAdapter(Context context) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mArtistList = new ArrayList<>();
    }

    public ArtistModel getValueAt(int position) {
        return mArtistList.get(position);
    }

    public void setAdapterItems(List<ArtistModel> artistList) {
        mArtistList.clear();
        mArtistList.addAll(artistList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
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
        Picasso.with(holder.mImageView.getContext())
                .load(mArtistList.get(position).coverPhoto)
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, TrackActivity.class);
                intent.putExtra(TrackActivity.EXTRA_ARTIST_ID, holder.artistId);
                context.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public String artistId;

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
}
