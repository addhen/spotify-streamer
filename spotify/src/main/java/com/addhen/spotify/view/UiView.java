package com.addhen.spotify.view;

import android.content.Context;

public interface UiView {

    void showError(String message);

    Context getAppContext();
}
