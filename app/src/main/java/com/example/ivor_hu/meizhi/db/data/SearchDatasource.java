package com.example.ivor_hu.meizhi.db.data;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ivor_hu.meizhi.db.entity.SearchEntity;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.net.GankApi.Result;
import com.example.ivor_hu.meizhi.net.GankApiService;
import com.example.ivor_hu.meizhi.viewmodel.SearchViewModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ivor on 2018/8/5.
 */

public class SearchDatasource extends PageKeyedDataSource<Integer, SearchEntity> {
    private static final String TAG = "SearchDatasource";
    private SearchViewModel.SearchWrapper mSearchWrapper;

    public SearchDatasource(SearchViewModel.SearchWrapper searchWrapper) {
        mSearchWrapper = searchWrapper;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, SearchEntity> callback) {
        Log.d(TAG, "loadInitial: " + mSearchWrapper);
        Call<Result<List<SearchEntity>>> call = GankApiService.getInstance().search(
                mSearchWrapper.query, mSearchWrapper.category, GankApi.DEFAULT_BATCH_NUM, 1);
        try {
            Response<Result<List<SearchEntity>>> response = call.execute();
            if (response != null && response.isSuccessful()) {
                callback.onResult(response.body().results, null, 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, SearchEntity> callback) {
        // Empty
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, SearchEntity> callback) {
        Log.d(TAG, "loadAfter: " + params.key);
        Call<Result<List<SearchEntity>>> call = GankApiService.getInstance().search(
                mSearchWrapper.query, mSearchWrapper.category, GankApi.DEFAULT_BATCH_NUM, params.key);
        try {
            Response<Result<List<SearchEntity>>> response = call.execute();
            if (response != null && response.isSuccessful()) {
                callback.onResult(response.body().results, params.key + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
