package com.example.ivor_hu.meizhi.db.data;

import android.arch.paging.PageKeyedDataSource;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.ivor_hu.meizhi.App;
import com.example.ivor_hu.meizhi.db.entity.Image;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.net.GankApi.Result;
import com.example.ivor_hu.meizhi.net.GankApiService;
import com.example.ivor_hu.meizhi.net.ImageFetcher;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ivor on 2018/8/5.
 */

public class ImageDatasource extends PageKeyedDataSource<Integer, Image> implements ImageFetcher {
    private static final String TAG = "ImageDatasource";
    private String mType;

    public ImageDatasource(String type) {
        mType = type;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Image> callback) {
        Log.d(TAG, "loadInitial: " + mType);
        Call<Result<List<Image>>> call = GankApiService.getInstance().fetchGirls(GankApi.DEFAULT_BATCH_NUM, 1);
        try {
            Response<Result<List<Image>>> response = call.execute();
            if (response != null && response.isSuccessful()) {
                callback.onResult(response.body().results, null, 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Image> callback) {
        // Empty
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Image> callback) {
        Log.d(TAG, "loadAfter: " + params.key);
        Call<Result<List<Image>>> call = GankApiService.getInstance().fetchGirls(GankApi.DEFAULT_BATCH_NUM, params.key);
        try {
            Response<Result<List<Image>>> response = call.execute();
            if (response != null && response.isSuccessful()) {
                List<Image> images = response.body().results;
                for (Image image : images) {
                    try {
                        Image.persist(image, ImageDatasource.this);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                callback.onResult(images, params.key + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prefetchImage(String url, Point measured) throws IOException, InterruptedException, ExecutionException {
        Bitmap bitmap = Glide.with(App.getInstance())
                .load(url).asBitmap()
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get();

        measured.x = bitmap.getWidth();
        measured.y = bitmap.getHeight();
    }
}
