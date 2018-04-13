package com.example.ivor_hu.meizhi.ui.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.ui.adapter.StuffAdapter;
import com.example.ivor_hu.meizhi.ui.callback.StuffItemCallback;
import com.example.ivor_hu.meizhi.utils.CommonUtil;

import java.util.List;

/**
 * Created by ivor on 16-6-21.
 */
public class CollectionFragment extends BaseStuffFragment {
    private static final String TAG = "CollectionFragment";
    private static final String TYPE = "col_type";

    public static CollectionFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);

        CollectionFragment fragment = new CollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        mType = getArguments().getString(TYPE);
        mStuffViewModel.getCollections().observe(this, new Observer<List<Stuff>>() {
            @Override
            public void onChanged(@Nullable List<Stuff> stuffs) {
                setFetchingFlag(false);
                if (stuffs == null) {
                    return;
                }

                StuffAdapter adapter = (StuffAdapter) mAdapter;
                adapter.updateStuffs(stuffs);
            }
        });

    }

    @Override
    protected void loadingMore() {
        return;
    }

    @Override
    protected void refresh() {
        if (isFetching()) {
            return;
        }

        mStuffViewModel.loadCollections();
        setFetchingFlag(true);
    }

    @Override
    protected RecyclerView.Adapter initAdapter() {
        final StuffAdapter adapter = new StuffAdapter(getActivity(), mType);
        adapter.setCallback(new StuffItemCallback() {
            @Override
            public void onItemClick(View view, Stuff stuff) {
                if (isFetching() || stuff == null) {
                    return;
                }

                CommonUtil.openUrl(getActivity(), stuff.getUrl());
            }

            @Override
            public boolean onItemLongClick(View view, Stuff stuff) {
                if (isFetching() || stuff == null) {
                    return true;
                }

                getActivity().startActionMode(new StuffFragment.ShareListener(getActivity(), stuff, view, true));
                return true;
            }
        });
        return adapter;
    }
}
