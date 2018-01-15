package com.example.ivor_hu.meizhi.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.ViewerActivity;
import com.example.ivor_hu.meizhi.databinding.GirlsFragmentBinding;
import com.example.ivor_hu.meizhi.db.entity.Image;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.ui.adapter.GirlsAdapter;
import com.example.ivor_hu.meizhi.ui.callback.GirlItemCallback;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.viewmodel.GirlViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ivor on 2016/2/6.
 */
public class GirlsFragment extends BaseFragment {
    public static final String TAG = "GirlsFragment";
    public static final String POSTION = "viewer_position";
    public static final String IMAGES = "viewer_images";
    private static final String TYPE = "girls_type";
    private static final int GIRLS_SPAN_COUNT = 2;
    private GirlViewModel mGirlViewModel;
    private GirlsFragmentBinding mBinding;

    public static GirlsFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);

        GirlsFragment fragment = new GirlsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        mType = getArguments().getString(TYPE);
        mGirlViewModel = ViewModelProviders.of(this).get(GirlViewModel.class);
        mGirlViewModel.getGirls().observe(this, new Observer<GankApi.Result<List<Image>>>() {
            @Override
            public void onChanged(@Nullable GankApi.Result<List<Image>> result) {
                setFetchingFlag(false);
                if (result == null) {
                    return;
                }

                GirlsAdapter adapter = (GirlsAdapter) mAdapter;
                if (mPage == 1) {
                    adapter.clear();
                }
                adapter.addGirls(result.results);
                adapter.notifyItemRangeInserted(adapter.getItemCount(), result.results.size());
                mPage++;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.girls_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected void loadingMore() {
        if (isFetching()) {
            return;
        }

        mGirlViewModel.fetchGirls(mPage);
        setFetchingFlag(true);
    }


    @Override
    protected void refresh() {
        if (isFetching()) {
            return;
        }

        mPage = 1;
        mGirlViewModel.fetchGirls(mPage);
        setFetchingFlag(true);
    }

    @Override
    protected int getLastVisiblePos() {
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mLayoutManager;
        int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
        return getMaxPosition(lastPositions);
    }

    @Override
    protected SwipeRefreshLayout getRefreshLayout() {
        return mBinding.swipeRefreshLayout;
    }

    @Override
    protected RecyclerView.Adapter initAdapter() {
        final GirlsAdapter adapter = new GirlsAdapter(getActivity());
        adapter.setCallback(new GirlItemCallback() {
            @Override
            public void onItemClick(View view, int position) {
                if (isFetching()) {
                    CommonUtil.makeSnackBar(mRefreshLayout, getString(R.string.fetching_pic), Snackbar.LENGTH_LONG);
                    return;
                }

                Intent intent = new Intent(getActivity(), ViewerActivity.class);
                intent.putExtra(POSTION, position);
                intent.putParcelableArrayListExtra(IMAGES, (ArrayList<? extends Parcelable>) adapter.getImages());
                getActivity().startActivity(intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(),
                                view.findViewById(R.id.network_imageview),
                                adapter.getUrlAt(position)).toBundle());
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                CommonUtil.makeSnackBar(mRefreshLayout, position + getString(R.string.fragment_long_clicked), Snackbar.LENGTH_SHORT);
                return true;
            }
        });
        return adapter;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(GIRLS_SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mBinding.girlsRecyclerviewId;
    }

    private int getMaxPosition(int[] positions) {
        int maxPosition = 0;
        for (int position : positions) {
            maxPosition = Math.max(maxPosition, position);
        }
        return maxPosition;
    }

    public void smoothScrollTo(int index) {
        mRecyclerView.smoothScrollToPosition(index);
    }

    public void onActivityReenter(final int index) {
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                getActivity().supportStartPostponedEnterTransition();
                return true;
            }
        });

    }

    public String getImageUrlAt(int i) {
        return ((GirlsAdapter) mAdapter).getUrlAt(i);
    }

    public View getImageViewAt(int i) {
        return mLayoutManager.findViewByPosition(i);
    }
}
