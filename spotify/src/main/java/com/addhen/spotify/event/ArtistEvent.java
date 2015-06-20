package com.addhen.spotify.event;

public class ArtistEvent {

    public String artistId;

    public String artistName;

    public ArtistEvent(String artistId, String artistName) {
        this.artistId = artistId;
        this.artistName = artistName;
    }
}
