package com.example.ivor_hu.meizhi.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.ivor_hu.meizhi.db.data.StuffRepository;
import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.net.GankApi.Result;

import java.util.List;

/**
 * Created by ivor on 2017/11/24.
 */

public class StuffViewModel extends AndroidViewModel {

    private final StuffRepository mRepository;

    private final MutableLiveData<FetchWrapper> mFetchWrapper;
    private final LiveData<Result<List<Stuff>>> mStuffs;

    private final MutableLiveData<Void> mCollectionTrigger;
    private final LiveData<List<Stuff>> mCollections;

    public StuffViewModel(@NonNull Application application) {
        super(application);
        mRepository = StuffRepository.getInstance(application);

        mFetchWrapper = new MutableLiveData<>();
        mStuffs = Transformations.switchMap(mFetchWrapper, new Function<FetchWrapper, LiveData<Result<List<Stuff>>>>() {
            @Override
            public LiveData<Result<List<Stuff>>> apply(FetchWrapper wrapper) {
                return mRepository.fetchStuffs(wrapper.type, wrapper.page);
            }
        });

        mCollectionTrigger = new MutableLiveData<>();
        mCollections = Transformations.switchMap(mCollectionTrigger, new Function<Void, LiveData<List<Stuff>>>() {
            @Override
            public LiveData<List<Stuff>> apply(Void input) {
                return mRepository.getCollections();
            }
        });
    }

    public void fetchStuffs(String type, int page) {
        mFetchWrapper.setValue(new FetchWrapper(type, page));
    }

    public LiveData<Result<List<Stuff>>> getStuffs() {
        return mStuffs;
    }

    public void loadCollections() {
        mCollectionTrigger.setValue(null);
    }

    public LiveData<List<Stuff>> getCollections() {
        return mCollections;
    }

    public void insertCollection(@NonNull Stuff stuff) {
        mRepository.insertCollection(stuff);
    }

    public void deleteCollection(@NonNull Stuff stuff) {
        mRepository.deleteCollection(stuff);
    }

    private static class FetchWrapper {
        final String type;
        final int page;

        public FetchWrapper(String type, int page) {
            this.type = type;
            this.page = page;
        }
    }

}
