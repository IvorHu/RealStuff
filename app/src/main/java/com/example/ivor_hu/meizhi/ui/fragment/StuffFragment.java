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
 * Created by Ivor on 2016/3/3.
 */
public class StuffFragment extends BaseStuffFragment {
    public static final String SERVICE_TYPE = "service_type";
    private static final String TAG = "StuffFragment";
    private static final String TYPE = "type";

    public static StuffFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);

        StuffFragment fragment = new StuffFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        mType = getArguments().getString(TYPE);
        mStuffViewModel.getStuffList().observe(this, new Observer<PagedList<Stuff>>() {
            @Override
            public void onChanged(@Nullable PagedList<Stuff> stuffs) {
                setFetchingFlag(false);
                mAdapter.submitList(stuffs);
            }
        });
    }

    @Override
    protected void refresh() {
        if (isFetching()) {
            return;
        }

        mStuffViewModel.refresh(mType);
        setFetchingFlag(true);
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

                getActivity().startActionMode(new ShareListener(getActivity(), stuff, view, false));
                return true;
            }
        });
        return adapter;
    }

}
