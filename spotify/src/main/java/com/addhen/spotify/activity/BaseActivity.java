package com.addhen.spotify.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Layout resource id
     */
    protected final int mLayout;

    /**
     * Menu resource id
     */
    protected final int mMenu;

    private ActionBar mActionBar;

    public BaseActivity(int layout, int menu) {
        mLayout = layout;
        mMenu = menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mLayout != 0) {
            setContentView(mLayout);
            ButterKnife.inject(this);
        }

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
        }
    }

    /**
     * Shows a simple {@link Snackbar}
     *
     * @param view    The view to anchor the Snackbar to
     * @param message The message to be showed
     */
    protected void showSnabackar(@NonNull View view, @NonNull String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Sets an actionbar main and sub titles
     *
     * @param title    The main title for the action bar
     * @param subTitle The sub title for the action bar
     */
    protected void setActionBarTitle(String title, String subTitle) {
        if (mActionBar != null) {
            if (!TextUtils.isEmpty(title)) {
                mActionBar.setTitle(title);
            }
            mActionBar.setSubtitle(subTitle);
        }
    }

    protected void setSubTitle(@NonNull String subTitle) {
        setActionBarTitle(null, subTitle);
    }

    /**
     * Shows a simple {@link Snackbar}
     *
     * @param view  The view to anchor the Snackbar to
     * @param resId The message to be showed
     */
    protected void showSnabackar(@NonNull View view, @StringRes int resId) {
        showSnabackar(view, getString(resId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMenu != 0) {
            getMenuInflater().inflate(mMenu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view where to add the fragment.
     * @param fragment        The fragment to be added.
     * @param tag             The tag for the fragment
     */
    protected void addFragment(int containerViewId, Fragment fragment, String tag) {
        android.app.FragmentTransaction fragmentTransaction = this.getFragmentManager()
                .beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        fragmentTransaction.commit();
    }
}