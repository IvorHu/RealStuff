package com.example.ivor_hu.meizhi.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.ViewerActivity;
import com.example.ivor_hu.meizhi.db.Image;
import com.example.ivor_hu.meizhi.services.ImageFetchService;
import com.example.ivor_hu.meizhi.utils.CommonUtil;

import io.realm.Realm;


/**
 * Created by Ivor on 2016/2/6.
 */
public class GirlsFragment extends android.support.v4.app.Fragment {
    public static final String TAG = "GirlsFragment";
    public static final String POSTION = "viewer_position";
    private static final String TYPE = "girls_type";

    private RecyclerView mRecyclerView;
    private LocalBroadcastManager mLocalBroadcastManager;
    private final UpdateResultReceiver updateResultReceiver = new UpdateResultReceiver();
    private GirlsAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private SwipeRefreshLayout mRefreshLayout;
    private boolean mIsLoadingMore;
    private boolean mIsRefreshing;
    private Realm mRealm;
    private String mType;

    public static GirlsFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);

        GirlsFragment fragment = new GirlsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        mType = getArguments().getString(TYPE);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.girls_fragment, container, false);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.girls_recyclerview_id);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new GirlsAdapter(getActivity(), mRealm);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mIsLoadingMore && dy > 0) {
                    int lastVisiblePos = getLastVisiblePos(mLayoutManager);
                    if (lastVisiblePos + 1 == mAdapter.getItemCount()) {
                        loadingMore();
                        CommonUtil.makeSnackBar(mRefreshLayout, getResources().getString(R.string.fragment_load_more), Snackbar.LENGTH_SHORT);
                    }
                }
            }
        });

        mAdapter.setOnItemClickListener(new GirlsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (mIsLoadingMore || mIsRefreshing) {
                    CommonUtil.makeSnackBar(mRefreshLayout, getString(R.string.fragment_isfetching), Snackbar.LENGTH_LONG);
                    return;
                }

                Intent intent = new Intent(getActivity(), ViewerActivity.class);
                intent.putExtra(POSTION, pos);
                getActivity().startActivity(intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(),
                                view.findViewById(R.id.network_imageview),
                                mAdapter.getUrlAt(pos)).toBundle());
            }

            @Override
            public void onItemLongClick(View view, int pos) {
                CommonUtil.makeSnackBar(mRefreshLayout, pos + getString(R.string.fragment_long_clicked), Snackbar.LENGTH_SHORT);
            }
        });

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        return view;
    }

    private void refreshImages() {
        if (mIsRefreshing) {
            return;
        }

        Intent intent = new Intent(getActivity(), ImageFetchService.class);
        intent.setAction(ImageFetchService.ACTION_FETCH_REFRESH);
        getActivity().startService(intent);

        mIsRefreshing = true;
        setRefreshLayout(true);
    }

    private void loadingMore() {
        if (mIsLoadingMore)
            return;

        Intent intent = new Intent(getActivity(), ImageFetchService.class);
        intent.setAction(ImageFetchService.ACTION_FETCH_MORE);
        getActivity().startService(intent);

        mIsLoadingMore = true;
        setRefreshLayout(true);
    }

    private void setRefreshLayout(final boolean state) {
        if (null == mRefreshLayout)
            return;

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(state);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshImages();
            }
        };
        mRefreshLayout.setOnRefreshListener(listener);
        if (savedInstanceState == null)
            listener.onRefresh();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mLocalBroadcastManager.registerReceiver(updateResultReceiver, new IntentFilter(ImageFetchService.ACTION_UPDATE_RESULT));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        mLocalBroadcastManager.unregisterReceiver(updateResultReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.removeAllChangeListeners();
        mRealm.close();
    }

    private int getLastVisiblePos(StaggeredGridLayoutManager layoutManager) {
        int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
        return getMaxPosition(lastPositions);
    }

    private int getMaxPosition(int[] positions) {
        int maxPosition = 0;
        int size = positions.length;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    public void smoothScrollToTop() {
        if (mLayoutManager != null) {
            mLayoutManager.smoothScrollToPosition(mRecyclerView, null, 0);
        }
    }

    public void onActivityReenter(final int index) {
        mRecyclerView.smoothScrollToPosition(index);
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
        return Image.all(mRealm).get(i).getUrl();
    }

    public View getImageViewAt(int i) {
        return mLayoutManager.findViewByPosition(i);
    }

    private class UpdateResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int fetched = intent.getIntExtra(ImageFetchService.EXTRA_FETCHED, 0);
            String trigger = intent.getStringExtra(ImageFetchService.EXTRA_TRIGGER);
            Log.d(TAG, "fetched " + fetched + ", triggered by " + trigger);

            setRefreshLayout(false);

            if (mIsRefreshing) {
                mIsRefreshing = false;
                CommonUtil.makeSnackBar(mRefreshLayout, getString(R.string.fragment_refreshed), Snackbar.LENGTH_SHORT);
                if (fetched > 0) {
                    mAdapter.updateRefreshed(fetched);
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }

            if (mIsLoadingMore) {
                mIsLoadingMore = false;
            }
        }
    }
}
