package com.addhen.spotify.common;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;


public class SpotifyServices {

    // TODO: Provide the accessToke at build time
    final String accessToken = "";

    public SpotifyService getSpotify() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + accessToken);
                    }
                })
                .build();
        return restAdapter.create(SpotifyService.class);
    }
}
