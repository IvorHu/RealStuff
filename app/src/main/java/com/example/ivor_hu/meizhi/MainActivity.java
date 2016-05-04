package com.example.ivor_hu.meizhi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.widget.GirlsFragment;
import com.example.ivor_hu.meizhi.widget.StuffFragment;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String CURR_TYPE = "curr_fragment_type";

    private FloatingActionButton mFab;
    private Toolbar mToolbar;
    private Fragment mCurrFragment;
    private String mCurrFragmentType;
    private GestureDetector mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            CommonUtil.makeSnackBar(mToolbar, getResources().getString(R.string.main_double_taps), Snackbar.LENGTH_LONG);
            return true;
        }
    });
    private Bundle reenterState;

    public enum TYPE {
        GIRLS("GIRLS", "Girls", R.string.nav_girls, R.id.nav_girls),
        ANDROID("ANDROID", "Android", R.string.nav_android, R.id.nav_android),
        IOS("IOS", "iOS", R.string.nav_ios, R.id.nav_ios),
        WEB("WEB", "前端", R.string.nav_web, R.id.nav_web),
        APP("APP", "App", R.string.nav_app, R.id.nav_app),
        FUN("FUN", "瞎推荐", R.string.nav_fun, R.id.nav_fun),
        OTHERS("OTHERS", "拓展资源", R.string.nav_others, R.id.nav_others),
        COLLECTIONS("COLLECTIONS", "Collections", R.string.nav_collections, R.id.nav_collections);

        private final String id;
        private final String apiName;
        private final int strId;
        private final int resId;

        TYPE(String id, String apiName, int strId, int resId) {
            this.id = id;
            this.apiName = apiName;
            this.strId = strId;
            this.resId = resId;
        }

        @Override
        public String toString() {
            return id;
        }

        public String getId() {
            return id;
        }

        public String getApiName() {
            return apiName;
        }

        public int getStrId() {
            return strId;
        }

        public int getResId() {
            return resId;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.nav_girls);
        setSupportActionBar(mToolbar);
        mToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        final FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = GirlsFragment.newInstance(TYPE.GIRLS.getId());
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment, TYPE.GIRLS.getId())
                    .commit();
            mCurrFragment = fragment;
            mCurrFragmentType = TYPE.GIRLS.getId();
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TYPE.GIRLS.getId().equals(mCurrFragmentType))
                    ((GirlsFragment) mCurrFragment).smoothScrollToTop();
                else
                    ((StuffFragment) mCurrFragment).smoothScrollToTop();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (reenterState != null && TYPE.GIRLS.getId().equals(mCurrFragmentType)) {
                    GirlsFragment girlsFragment = (GirlsFragment) mCurrFragment;
                    int i = reenterState.getInt(ViewerActivity.INDEX, 0);
//                    Log.d(TAG, "onMapSharedElements: reenter from " + i);

                    sharedElements.clear();
                    sharedElements.put(girlsFragment.getImageUrlAt(i), girlsFragment.getImageViewAt(i));

                    reenterState = null;
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURR_TYPE, mCurrFragmentType);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrFragmentType = savedInstanceState.getString(CURR_TYPE);
        hideAllExcept(mCurrFragmentType);
        mToolbar.setTitle(TYPE.valueOf(mCurrFragmentType).getStrId());
    }

    private void hideAllExcept(String mCurrFragmentType) {
        FragmentManager manager = getSupportFragmentManager();
        for (TYPE type : TYPE.values()) {
            Fragment fragment = manager.findFragmentByTag(type.getId());
            if (type.equals(mCurrFragmentType)) {
                manager.beginTransaction().show(fragment).commit();
                mCurrFragment = fragment;
            } else {
                if (fragment != null) {
                    manager.beginTransaction().hide(fragment).commit();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        if (id == TYPE.GIRLS.getResId()) {
            swithTo(manager, TYPE.GIRLS.getId(), GirlsFragment.newInstance(TYPE.GIRLS.getApiName()));
        } else {
            for (TYPE type : TYPE.values()) {
                if (type.getResId() == id) {
                    swithTo(manager, type.getId(), StuffFragment.newInstance(type.getApiName()));
                    break;
                }
            }
        }
//        else if (id == R.id.nav_test) {
//            Log.d(TAG, "onNavigationItemSelected: test");
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void swithTo(FragmentManager manager, String type, Fragment addedFragment) {
        Fragment fragment = manager.findFragmentByTag(type);
        if (null != fragment) {
            hideAndShow(manager, fragment, type);
        } else {
            hideAndAdd(manager, addedFragment, type);
        }
    }

    private void hideAndAdd(FragmentManager manager, Fragment newFragment, String fragmentIdx) {
        manager.beginTransaction().hide(mCurrFragment).add(R.id.fragment_container, newFragment, fragmentIdx).commit();
        mCurrFragment = newFragment;
        mCurrFragmentType = fragmentIdx;
        mToolbar.setTitle(TYPE.valueOf(fragmentIdx).getStrId());
    }

    private void hideAndShow(FragmentManager manager, Fragment newFragment, String fragmentIdx) {
        manager.beginTransaction().hide(mCurrFragment).show(newFragment).commit();
        updateLikedData(newFragment, fragmentIdx);
        mCurrFragment = newFragment;
        mCurrFragmentType = fragmentIdx;
        mToolbar.setTitle(TYPE.valueOf(fragmentIdx).getStrId());
    }

    private void updateLikedData(Fragment newFragment, String fragmentIdx) {
        if (fragmentIdx.equals(TYPE.GIRLS.getId())) {
            return;
        }
        ((StuffFragment) newFragment).updateData();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        if (TYPE.GIRLS.getId().equals(mCurrFragmentType)) {
            supportPostponeEnterTransition();

            reenterState = new Bundle(data.getExtras());

            final int index = reenterState.getInt(ViewerActivity.INDEX, 0);
            ((GirlsFragment) mCurrFragment).onActivityReenter(index);
        }
    }

//    public void hideFab() {
//        if (mFab != null && isFabShown) {
//            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
//            mFab.animate().translationY(mFab.getHeight() + lp.bottomMargin).setInterpolator(new AccelerateInterpolator(2));
//            isFabShown = false;
//        }
//    }
//
//    public void showFab() {
//        if (mFab != null && !isFabShown) {
//            mFab.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2));
//            isFabShown = true;
//        }
//    }

}
