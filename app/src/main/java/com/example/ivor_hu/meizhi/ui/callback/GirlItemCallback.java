package com.example.ivor_hu.meizhi.ui.callback;

import android.view.View;

/**
 * Created by ivor on 2018/1/14.
 */

public interface GirlItemCallback {
    void onItemClick(View view, int position);

    boolean onItemLongClick(View view, int position);
}
