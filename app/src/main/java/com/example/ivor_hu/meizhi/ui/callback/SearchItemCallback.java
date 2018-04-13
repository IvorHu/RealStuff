package com.example.ivor_hu.meizhi.ui.callback;

import android.view.View;

import com.example.ivor_hu.meizhi.db.entity.SearchEntity;

/**
 * Created by ivor on 2018/1/14.
 */

public interface SearchItemCallback {
    void onItemClick(View view, SearchEntity searchEntity);

    boolean onItemLongClick(View view, SearchEntity searchEntity);
}
