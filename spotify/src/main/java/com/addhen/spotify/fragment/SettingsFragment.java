package com.addhen.spotify.fragment;


import com.addhen.spotify.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

    public static final String ENABLE_PLAYBACK_NOTIFICATION
            = "enable_playback_notification_preference";

    public static final String PLAYBACK_COUNTRY_CODES = "playback_country_code_preference";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
