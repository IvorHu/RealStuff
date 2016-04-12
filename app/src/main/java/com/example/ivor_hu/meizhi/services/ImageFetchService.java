package com.example.ivor_hu.meizhi.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.ivor_hu.meizhi.db.Image;
import com.example.ivor_hu.meizhi.net.GankAPI;
import com.example.ivor_hu.meizhi.net.GankAPIService;
import com.example.ivor_hu.meizhi.net.ImageFetcher;
import com.example.ivor_hu.meizhi.utils.DateUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Ivor on 2016/2/12.
 */
public class ImageFetchService extends IntentService implements ImageFetcher {
    private static final String TAG = "ImageFetchService";
    public static final String ACTION_UPDATE_RESULT = "com.ivor.meizhi.girls_update_result";
    public static final String EXTRA_FETCHED = "girls_fetched";
    public static final String EXTRA_TRIGGER = "girls_trigger";
    public static final String ACTION_FETCH_REFRESH = "com.ivor.meizhi.girls_fetch_refresh";
    public static final String ACTION_FETCH_MORE = "com.ivor.meizhi.girls_fetch_more";

    private LocalBroadcastManager localBroadcastManager;

//    private final Gson gson = new GsonBuilder()
//            .setDateFormat(DateUtil.DATE_FORMAT_WHOLE)
//            .setExclusionStrategies(new ExclusionStrategy() {
//                @Override
//                public boolean shouldSkipField(FieldAttributes f) {
//                    return f.getDeclaringClass().equals(RealmObject.class);
//                }
//
//                @Override
//                public boolean shouldSkipClass(Class<?> clazz) {
//                    return false;
//                }
//            })
//            .create();
//
//    private final Retrofit girlsRetrofit = new Retrofit.Builder()
//            .baseUrl(GankAPI.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build();
//
//    private final GankAPI girlsApi = girlsRetrofit.create(GankAPI.class);

    public ImageFetchService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Realm realm = Realm.getDefaultInstance();

        RealmResults<Image> latest = Image.all(realm);

        int fetched = 0;
        try {
            if (latest.isEmpty()) {
                fetched = fetchLatest(realm);
                Log.d(TAG, "no latest, fresh fetch");
            } else if (ACTION_FETCH_REFRESH.equals(intent.getAction())) {
                Log.d(TAG, "latest fetch: " + latest.first().getPublishedAt());
                fetched = fetchRefresh(realm, latest.first().getPublishedAt());
            } else if (ACTION_FETCH_MORE.equals(intent.getAction())) {
                Log.d(TAG, "earliest fetch: " + latest.last().getPublishedAt());
                fetched = fetchMore(realm, latest.last().getPublishedAt());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendResult(intent, realm, fetched);
    }

    private void sendResult(Intent intent, Realm realm, int fetched) {
        realm.close();

        Log.d(TAG, "finished fetching, actual fetched " + fetched);

        Intent broadcast = new Intent(ACTION_UPDATE_RESULT);
        broadcast.putExtra(EXTRA_FETCHED, fetched);
        broadcast.putExtra(EXTRA_TRIGGER, intent.getAction());

        localBroadcastManager.sendBroadcast(broadcast);
    }


    private int fetchLatest(final Realm realm) throws IOException {
        GankAPI.Result<List<Image>> result = GankAPIService.getInstance().latestGirls(10).execute().body();

        if (result.error)
            return 0;

        int resultSize = result.results.size();
        for (int i = 0; i < resultSize; i++) {
            if (!saveToDb(realm, result.results.get(i)))
                return i;
        }

        return resultSize;
    }

    private int fetchRefresh(Realm realm, Date publishedAt) throws IOException {
        String after = DateUtil.format(publishedAt);
        List<String> dates = DateUtil.generateSequenceDateTillToday(publishedAt);
        return fetch(realm, after, dates);
    }

    private int fetchMore(Realm realm, Date publishedAt) throws IOException {
        String before = DateUtil.format(publishedAt);
        List<String> dates = DateUtil.generateSequenceDateBefore(publishedAt, 20);
        return fetch(realm, before, dates);
    }

    private int fetch(Realm realm, String baseline, List<String> dates) throws IOException {
        int fetched = 0;

        for (String date : dates) {
            if (date.equals(baseline))
                continue;

            GankAPI.Result<GankAPI.Girls> girlsResult = GankAPIService.getInstance().dayGirls(date).execute().body();

            if (girlsResult.error || null == girlsResult.results || null == girlsResult.results.images)
                continue;

            for (Image image : girlsResult.results.images) {
                if (!saveToDb(realm, image))
                    return fetched;

                fetched++;
            }
        }
        return fetched;
    }

    private boolean saveToDb(Realm realm, Image image) {
        realm.beginTransaction();

        try {
            realm.copyToRealm(Image.persist(image, this));
            Log.d(TAG, "saveToDb: " + image.getPublishedAt());
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch image", e);
            realm.cancelTransaction();
            return false;
        }

        realm.commitTransaction();
        return true;
    }

    @Override
    public void prefetchImage(String url, Point measured) throws IOException, InterruptedException, ExecutionException {
        Bitmap bitmap = Glide.with(this)
                .load(url).asBitmap()
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get();

        measured.x = bitmap.getWidth();
        measured.y = bitmap.getHeight();

    }
}
