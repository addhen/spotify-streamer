package com.addhen.spotify.ui.activity;

import com.addhen.spotify.R;
import com.addhen.spotify.ui.fragment.SettingsFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends BaseActivity {

    public static Intent getIntent(final Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    public SettingsActivity() {
        super(R.layout.activity_settings, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.preference_container, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
