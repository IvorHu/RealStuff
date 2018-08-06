package com.example.ivor_hu.meizhi.db.data;

import android.arch.paging.DataSource;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.ivor_hu.meizhi.db.AppDatabase;
import com.example.ivor_hu.meizhi.db.DatabaseHelper;
import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.utils.AppExecutors;

/**
 * Created by ivor on 2017/11/24.
 */

public class StuffRepository {
    private static StuffRepository sInstance;
    private final AppDatabase mDatabase;
    private final AppExecutors mExecutors;

    private StuffRepository(Context context) {
        mDatabase = DatabaseHelper.getInstance(context.getApplicationContext()).getDatabase();
        mExecutors = AppExecutors.getInstance();
    }

    public static StuffRepository getInstance(Context context) {
        if (sInstance != null) {
            return sInstance;
        }

        synchronized (StuffRepository.class) {
            if (sInstance == null) {
                sInstance = new StuffRepository(context);
            }
        }
        return sInstance;
    }

    public DataSource.Factory<Integer, Stuff> getCollections() {
        return mDatabase.collectionDao().getAll();
    }

    public void insertCollection(@NonNull final Stuff stuff) {
        mExecutors.normal().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.collectionDao().insert(stuff);
            }
        });
    }

    public void deleteCollection(@NonNull final Stuff stuff) {
        mExecutors.normal().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.collectionDao().delete(stuff);
            }
        });
    }
}
