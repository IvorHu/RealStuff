package com.example.ivor_hu.meizhi.ui;

import android.view.View;

/**
 * Created by ivor on 2018/1/14.
 */

public class BindingAdapter {
    @android.databinding.BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
