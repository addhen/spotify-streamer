package com.addhen.spotify.model.mapper;

import com.addhen.spotify.model.ArtistModel;
import com.addhen.spotify.util.Util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistModelMapper {

    public ArtistModel map(@NonNull Artist artist) {
        ArtistModel artistModel = new ArtistModel();
        artistModel._id = artist.id;
        artistModel.name = artist.name;
        if (!Util.isEmpty(artist.images)) {
            artistModel.coverPhoto = artist.images.get(0).url;
        }
        return artistModel;
    }

    public List<ArtistModel> map(@NonNull List<Artist> artists) {
        List<ArtistModel> artistModels = new ArrayList<>();
        for (Artist artist : artists) {
            artistModels.add(map(artist));
        }
        return artistModels;
    }
}
