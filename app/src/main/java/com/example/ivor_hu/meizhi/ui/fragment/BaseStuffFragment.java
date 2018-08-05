package com.example.ivor_hu.meizhi.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.databinding.StuffFragmentBinding;
import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.viewmodel.StuffViewModel;

import java.util.Date;

/**
 * Created by ivor on 16-6-21.
 */
public abstract class BaseStuffFragment<T, VH> extends BaseFragment {
    protected StuffViewModel mStuffViewModel;
    protected StuffFragmentBinding mBinding;

    @Override
    protected void initData() {
        super.initData();
        mStuffViewModel = ViewModelProviders.of(this).get(StuffViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.stuff_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mBinding.stuffRecyclerview;
    }

    @Override
    protected SwipeRefreshLayout getRefreshLayout() {
        return mBinding.stuffRefreshLayout;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    public class ShareListener implements AbsListView.MultiChoiceModeListener {
        private final Context context;
        private final Stuff stuff;
        private final View view;
        private final boolean isCollection;

        public ShareListener(Context context, Stuff stuff, View view, boolean isCollectionPage) {
            this.context = context;
            this.stuff = stuff;
            this.view = view;
            this.isCollection = isCollectionPage;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context_menu, menu);
            if (isCollection) {
                menu.findItem(R.id.context_menu_favor).setVisible(false);
            } else {
                menu.findItem(R.id.context_menu_unfavor).setVisible(false);
            }
            view.setActivated(true);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.context_menu_share:
                    String textShared = stuff.getDesc() + "    " + stuff.getUrl() + " -- " + context.getString(R.string.share_msg);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_msg));
                    intent.putExtra(Intent.EXTRA_TEXT, textShared);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    mode.finish();
                    return true;
                case R.id.context_menu_favor:
                    stuff.setLastChanged(new Date());
                    if (mStuffViewModel != null) {
                        mStuffViewModel.insertCollection(stuff);
                        CommonUtil.makeSnackBar(
                                getView(),
                                getContext().getString(R.string.favored),
                                Snackbar.LENGTH_SHORT);
                    }
                    mode.finish();
                    return true;
                case R.id.context_menu_unfavor:
                    if (mStuffViewModel != null) {
                        mStuffViewModel.deleteCollection(stuff);
                        CommonUtil.makeSnackBar(
                                getView(),
                                getContext().getString(R.string.unfavored),
                                Snackbar.LENGTH_SHORT);
                    }
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            view.setActivated(false);
        }
    }
}
