package com.example.ivor_hu.meizhi.db.data;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.net.GankApi.Result;
import com.example.ivor_hu.meizhi.net.GankApiService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ivor on 2018/7/25.
 */

public class StuffDatasource extends PageKeyedDataSource<Integer, Stuff> {
    private static final String TAG = "StuffDatasource";
    private String mType;

    public StuffDatasource(String type) {
        mType = type;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Stuff> callback) {
        Log.d(TAG, "loadInitial: " + mType);
        Call<Result<List<Stuff>>> call = GankApiService.getInstance().fetchStuffs(mType, GankApi.DEFAULT_BATCH_NUM, 1);
        try {
            Response<Result<List<Stuff>>> response = call.execute();
            if (response != null && response.isSuccessful()) {
                callback.onResult(response.body().results, null, 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Stuff> callback) {
        // Empty
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Stuff> callback) {
        Log.d(TAG, "loadAfter: " + mType + ", " + params.key);
        Call<Result<List<Stuff>>> call = GankApiService.getInstance().fetchStuffs(mType, GankApi.DEFAULT_BATCH_NUM, params.key);
        try {
            Response<Result<List<Stuff>>> response = call.execute();
            if (response != null && response.isSuccessful()) {
                callback.onResult(response.body().results, params.key + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
