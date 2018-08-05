package com.example.ivor_hu.meizhi.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.example.ivor_hu.meizhi.db.data.ImageDatasource;
import com.example.ivor_hu.meizhi.db.entity.Image;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.utils.AppExecutors;

/**
 * Created by ivor on 2017/11/24.
 */

public class GirlViewModel extends ViewModel {
    private final MutableLiveData<String> mRefresh;
    private final LiveData<PagedList<Image>> mImages;

    public GirlViewModel() {
        mRefresh = new MutableLiveData<>();
        mImages = Transformations.switchMap(mRefresh, new Function<String, LiveData<PagedList<Image>>>() {
            @Override
            public LiveData<PagedList<Image>> apply(final String type) {
                return new LivePagedListBuilder<>(
                        new DataSource.Factory<Integer, Image>() {
                            @Override
                            public DataSource<Integer, Image> create() {
                                return new ImageDatasource(type);
                            }
                        },
                        GankApi.DEFAULT_BATCH_NUM)
                        .setFetchExecutor(AppExecutors.getInstance().network())
                        .build();
            }
        });
    }

    public void refresh(String type) {
        mRefresh.setValue(type);
    }

    public LiveData<PagedList<Image>> getImages() {
        return mImages;
    }
}
