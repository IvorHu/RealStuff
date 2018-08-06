package com.example.ivor_hu.meizhi.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.ivor_hu.meizhi.db.entity.SearchEntity;
import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.ui.adapter.SearchAdapter;
import com.example.ivor_hu.meizhi.ui.callback.SearchItemCallback;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.viewmodel.SearchViewModel;

import java.text.ParseException;

/**
 * Created by ivor on 16-6-17.
 */
public class SearchFragment extends BaseStuffFragment<SearchEntity, SearchAdapter.Viewholder> {
    public static final String KEYWORD = "keyword";
    public static final String CATEGORY = "category";
    private static final String TAG = "SearchFragment";
    private String mKeyword;
    private String mCategory;
    private SearchViewModel mSearchViewModel;

    public static SearchFragment newInstance(String keyword, String category) {
        Bundle args = new Bundle();
        args.putString(KEYWORD, keyword);
        args.putString(CATEGORY, category);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        mKeyword = getArguments().getString(KEYWORD);
        mCategory = getArguments().getString(CATEGORY);
        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        mSearchViewModel.getSearchResult().observe(this, new Observer<PagedList<SearchEntity>>() {
            @Override
            public void onChanged(@Nullable PagedList<SearchEntity> searchEntities) {
                setFetchingFlag(false);
                mAdapter.submitList(searchEntities);
            }
        });
    }

    @Override
    protected void refresh() {
        if (isFetching()) {
            return;
        }

        mSearchViewModel.search(mKeyword, mCategory);
        setFetchingFlag(true);
    }

    @Override
    protected PagedListAdapter<SearchEntity, SearchAdapter.Viewholder> initAdapter() {
        final SearchAdapter adapter = new SearchAdapter(getActivity());
        adapter.setCallback(new SearchItemCallback() {
            @Override
            public void onItemClick(View view, SearchEntity searchEntity) {
                if (isFetching() || searchEntity == null) {
                    return;
                }

                CommonUtil.openUrl(getActivity(), searchEntity.getUrl());
            }

            @Override
            public boolean onItemLongClick(View view, SearchEntity searchEntity) {
                if (isFetching() || searchEntity == null) {
                    return true;
                }

                try {
                    Stuff stuff = Stuff.fromSearch(searchEntity);
                    getActivity().startActionMode(new ShareListener(getActivity(), stuff, view, false));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        return adapter;
    }

    public void search(String keyword, String category) {
        this.mKeyword = keyword;
        this.mCategory = category;
        refresh();
    }
}
