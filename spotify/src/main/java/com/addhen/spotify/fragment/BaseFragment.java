package com.addhen.spotify.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    /**
     * Layout resource mId
     */
    protected final int mLayout;

    /**
     * Menu resource mId
     */
    protected final int mMenu;

    /**
     * BaseFragment
     *
     * @param menu mMenu resource mId
     */
    public BaseFragment(int layout, int menu) {
        this.mLayout = layout;
        this.mMenu = menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = null;
        if (mLayout != 0) {
            root = inflater.inflate(mLayout, container, false);
        }
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.mMenu != 0) {
            inflater.inflate(this.mMenu, menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    /**
     * Shows a simple {@link Snackbar}
     *
     * @param view    The view to anchor the Snackbar to
     * @param message The message to be showed
     */
    protected void showSnabackar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Shows a simple {@link Snackbar}
     *
     * @param view  The view to anchor the Snackbar to
     * @param resId The message to be showed
     */
    protected void showSnabackar(View view, @StringRes int resId) {
        showSnabackar(view, getString(resId));
    }

    /**
     * Fades in a view.
     *
     * @param view The view to be faded in
     */
    protected android.view.View fadeIn(final android.view.View view) {
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                    android.R.anim.fade_in));
        }
        return view;
    }

    /**
     * Fades out a view
     *
     * @param view The view to be faded out
     */
    protected android.view.View fadeOut(final android.view.View view) {
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                    android.R.anim.fade_out));
        }
        return view;
    }

    /**
     * Animates the visibility of a view
     *
     * @param view The view to change its visibility
     * @param gone Specifiy whether it should be gone or visible
     * @param <V>  The view to change its visibility
     */
    protected <V extends android.view.View> V setViewGone(final V view, final boolean gone) {
        if (view != null) {
            if (gone) {
                if (View.GONE != view.getVisibility()) {

                    fadeOut(view);

                    view.setVisibility(View.GONE);
                }
            } else {
                if (View.VISIBLE != view.getVisibility()) {
                    view.setVisibility(View.VISIBLE);

                    fadeIn(view);

                }
            }
        }
        return view;
    }
}
