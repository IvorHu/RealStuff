package com.example.ivor_hu.meizhi.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.example.ivor_hu.meizhi.db.data.SearchDatasource;
import com.example.ivor_hu.meizhi.db.entity.SearchEntity;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.utils.AppExecutors;

/**
 * Created by ivor on 2017/11/25.
 */

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<SearchWrapper> mSearchWrapper;
    private final LiveData<PagedList<SearchEntity>> mResult;

    public SearchViewModel() {
        mSearchWrapper = new MutableLiveData<>();
        mResult = Transformations.switchMap(mSearchWrapper, new Function<SearchWrapper, LiveData<PagedList<SearchEntity>>>() {
            @Override
            public LiveData<PagedList<SearchEntity>> apply(final SearchWrapper wrapper) {
                return new LivePagedListBuilder<>(
                        new DataSource.Factory<Integer, SearchEntity>() {
                            @Override
                            public DataSource<Integer, SearchEntity> create() {
                                return new SearchDatasource(wrapper);
                            }
                        },
                        GankApi.DEFAULT_BATCH_NUM)
                        .setFetchExecutor(AppExecutors.getInstance().network())
                        .build();
            }
        });
    }

    public void search(String query, String category) {
        mSearchWrapper.setValue(new SearchWrapper(query, category, GankApi.DEFAULT_BATCH_NUM));
    }

    public LiveData<PagedList<SearchEntity>> getSearchResult() {
        return mResult;
    }

    public static class SearchWrapper {
        public final String query;
        public final String category;
        public final int count;

        public SearchWrapper(String query, String category, int count) {
            this.query = query;
            this.category = category;
            this.count = count;
        }

        @Override
        public String toString() {
            return "SearchWrapper{" +
                    "query='" + query + '\'' +
                    ", category='" + category + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

}
