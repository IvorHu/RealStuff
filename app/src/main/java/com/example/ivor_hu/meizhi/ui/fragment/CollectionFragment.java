package com.example.ivor_hu.meizhi.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.ui.adapter.StuffAdapter;
import com.example.ivor_hu.meizhi.ui.callback.StuffItemCallback;
import com.example.ivor_hu.meizhi.utils.CommonUtil;

/**
 * Created by ivor on 16-6-21.
 */
public class CollectionFragment extends BaseStuffFragment<Stuff, StuffAdapter.Viewholder> {
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
        mType = getArguments().getString(TYPE);
        super.initData();
        mStuffViewModel.getCollections().observe(this, new Observer<PagedList<Stuff>>() {
            @Override
            public void onChanged(@Nullable PagedList<Stuff> stuffs) {
                setFetchingFlag(false);
                mAdapter.submitList(stuffs);
            }
        });

    }

    @Override
    protected void refresh() {
        setFetchingFlag(false);
    }

    @Override
    protected PagedListAdapter<Stuff, StuffAdapter.Viewholder> initAdapter() {
        final StuffAdapter adapter = new StuffAdapter(getActivity());
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
