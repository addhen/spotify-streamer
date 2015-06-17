package com.addhen.spotify.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistModel extends Model implements Parcelable {

    public String name;

    public String coverPhoto;

    public ArtistModel() {

    }

    public ArtistModel(Parcel in) {
        name = in.readString();
        coverPhoto = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(coverPhoto);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ArtistModel> CREATOR
            = new Parcelable.Creator<ArtistModel>() {
        @Override
        public ArtistModel createFromParcel(Parcel in) {
            return new ArtistModel(in);
        }

        @Override
        public ArtistModel[] newArray(int size) {
            return new ArtistModel[size];
        }
    };
}
