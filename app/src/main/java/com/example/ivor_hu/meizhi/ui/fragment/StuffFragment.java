package com.example.ivor_hu.meizhi.ui.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.ui.adapter.StuffAdapter;
import com.example.ivor_hu.meizhi.ui.callback.StuffItemCallback;
import com.example.ivor_hu.meizhi.utils.CommonUtil;

import java.util.List;

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
        mStuffViewModel.getStuffs().observe(this, new Observer<GankApi.Result<List<Stuff>>>() {
            @Override
            public void onChanged(@Nullable GankApi.Result<List<Stuff>> result) {
                setFetchingFlag(false);

                if (result == null) {
                    return;
                }

                StuffAdapter adapter = (StuffAdapter) mAdapter;
                if (mPage == 1) {
                    adapter.clearStuff();
                }
                adapter.addStuffs(result.results);
                mPage++;
            }
        });
    }

    @Override
    protected void loadingMore() {
        if (mIsFetching) {
            return;
        }

        mStuffViewModel.fetchStuffs(mType, mPage);
        setFetchingFlag(true);
    }

    @Override
    protected void refresh() {
        if (isFetching()) {
            return;
        }

        mPage = 1;
        mStuffViewModel.fetchStuffs(mType, mPage);

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

                getActivity().startActionMode(new ShareListener(getActivity(), stuff, view, false));
                return true;
            }
        });
        return adapter;
    }

}
