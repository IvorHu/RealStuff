package com.example.ivor_hu.meizhi.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.example.ivor_hu.meizhi.db.data.StuffDatasource;
import com.example.ivor_hu.meizhi.db.data.StuffRepository;
import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.utils.AppExecutors;

/**
 * Created by ivor on 2017/11/24.
 */

public class StuffViewModel extends AndroidViewModel {

    private final StuffRepository mRepository;

    private final LiveData<PagedList<Stuff>> mCollections;
    private final LiveData<PagedList<Stuff>> mStuffList;
    private MutableLiveData<String> mRefresh = new MutableLiveData<>();

    public StuffViewModel(@NonNull Application application) {
        super(application);
        mRepository = StuffRepository.getInstance(application);

        mCollections = new LivePagedListBuilder<>(mRepository.getCollections(), GankApi.DEFAULT_BATCH_NUM).build();

        mStuffList = Transformations.switchMap(mRefresh, new Function<String, LiveData<PagedList<Stuff>>>() {
            @Override
            public LiveData<PagedList<Stuff>> apply(final String type) {
                return new LivePagedListBuilder<>(
                        new DataSource.Factory<Integer, Stuff>() {
                            @Override
                            public DataSource<Integer, Stuff> create() {
                                return new StuffDatasource(type);
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

    public LiveData<PagedList<Stuff>> getCollections() {
        return mCollections;
    }

    public void insertCollection(@NonNull Stuff stuff) {
        mRepository.insertCollection(stuff);
    }

    public void deleteCollection(@NonNull Stuff stuff) {
        mRepository.deleteCollection(stuff);
    }

    public LiveData<PagedList<Stuff>> getStuffList() {
        return mStuffList;
    }

}
