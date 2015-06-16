package com.addhen.spotify.state;

public class ArtistEvent {

    public String artistId;

    public String artistName;

    public ArtistEvent(String artistId, String artistName) {
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public static class SearchClearedEvent {

    }
}
