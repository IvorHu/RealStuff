package com.example.ivor_hu.meizhi.ui.fragment;

import android.arch.paging.PagedListAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ivor on 16-6-3.
 */
public abstract class BaseFragment<T, VH extends RecyclerView.ViewHolder> extends Fragment {
    private static final String TAG = "BaseFragment";
    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mRefreshLayout;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected PagedListAdapter<T, VH> mAdapter;
    protected boolean mIsFetching;
    protected String mType;
    protected boolean mIsNoMore;
    protected int mPage = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    protected void initData() {
        // Empty
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRefreshLayout = getRefreshLayout();
        mRecyclerView = getRecyclerView();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = getLayoutManager();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = initAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        };
        mRefreshLayout.setOnRefreshListener(listener);
        if (savedInstanceState == null) {
            listener.onRefresh();
        }

        // another way to call onRefresh
//        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                mRefreshLayout.setRefreshing(true);
//            }
//        });
    }

    public void setFetchingFlag(final boolean state) {
        mIsFetching = state;
        if (mRefreshLayout == null) {
            return;
        }

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(state);
            }
        });
    }

    public void smoothScrollToTop() {
        if (mLayoutManager != null) {
            mLayoutManager.smoothScrollToPosition(mRecyclerView, null, 0);
        }
    }

    public void updateData() {
        if (null == mAdapter) {
            return;
        }

        mAdapter.notifyDataSetChanged();
    }

    public boolean isFetching() {
        return mIsFetching;
    }

    protected abstract void refresh();

    protected abstract SwipeRefreshLayout getRefreshLayout();

    protected abstract PagedListAdapter<T, VH> initAdapter();

    protected abstract RecyclerView.LayoutManager getLayoutManager();

    protected abstract RecyclerView getRecyclerView();
}
