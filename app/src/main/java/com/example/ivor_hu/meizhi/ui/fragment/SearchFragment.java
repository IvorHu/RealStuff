package com.example.ivor_hu.meizhi.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedListAdapter;
import android.os.Bundle;
import android.view.View;

import com.example.ivor_hu.meizhi.db.entity.SearchEntity;
import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.ui.adapter.SearchAdapter;
import com.example.ivor_hu.meizhi.ui.adapter.StuffAdapter;
import com.example.ivor_hu.meizhi.ui.callback.SearchItemCallback;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.viewmodel.SearchViewModel;

import java.text.ParseException;

/**
 * Created by ivor on 16-6-17.
 */
public class SearchFragment extends BaseStuffFragment {
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
//        mSearchViewModel.getSearchResult().observe(this, new Observer<GankApi.Result<List<SearchEntity>>>() {
//            @Override
//            public void onChanged(@Nullable GankApi.Result<List<SearchEntity>> result) {
//                setFetchingFlag(false);
//                if (result == null) {
//                    return;
//                }
//
//                SearchAdapter adapter = (SearchAdapter) mAdapter;
//                if (mPage == 1) {
//                    adapter.clearData();
//                }
//                adapter.addSearch(result.results);
//                adapter.notifyItemRangeInserted(adapter.getItemCount(), result.results.size());
//                mPage++;
//            }
//        });
    }

    @Override
    protected void refresh() {
        if (isFetching()) {
            return;
        }

        mPage = 1;
        mSearchViewModel.search(mKeyword, mCategory, mPage);
        setFetchingFlag(true);
    }

    @Override
    protected PagedListAdapter<Stuff, StuffAdapter.Viewholder> initAdapter() {
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
        return null;
    }

    public void search(String keyword, String category) {
        this.mKeyword = keyword;
        this.mCategory = category;
//        ((SearchAdapter) mAdapter).clearData();
        refresh();
    }
}
