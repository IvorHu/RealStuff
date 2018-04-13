package com.example.ivor_hu.meizhi.ui.callback;

import android.view.View;

import com.example.ivor_hu.meizhi.db.entity.Stuff;

/**
 * Created by ivor on 2018/1/14.
 */

public interface StuffItemCallback {
    void onItemClick(View view, Stuff stuff);

    boolean onItemLongClick(View view, Stuff stuff);
}
