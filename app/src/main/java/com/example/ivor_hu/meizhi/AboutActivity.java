package com.example.ivor_hu.meizhi;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ivor_hu.meizhi.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivor on 2016/2/25.
 */
public class AboutActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ArrayMap<String, String> mLibsList;
    private List<String> mFeasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mToolbar = $(R.id.about_toolbar);
        mRecyclerView = $(R.id.about_recyclerview);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(mToolbar);
        if (NavUtils.getParentActivityName(this) != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initData();
        AboutAdapter adapter = new AboutAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                if (NavUtils.getParentActivityName(this) != null)
                    NavUtils.navigateUpFromSameTask(this);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initData() {
        mLibsList = new ArrayMap<>();
        mLibsList.put("bumptech / Glide", "https://github.com/bumptech/glide");
        mLibsList.put("Mike Ortiz / TouchImageView", "https://github.com/MikeOrtiz/TouchImageView");
        mLibsList.put("Realm", "https://realm.io");
        mLibsList.put("Square / Retrofit", "https://github.com/square/retrofit");
        mFeasList = new ArrayList<>();
        mFeasList.add("CardView");
        mFeasList.add("CollapsingToolbarLayout");
        mFeasList.add("DrawerLayout");
        mFeasList.add("RecyclerView");
        mFeasList.add("Shared Element Transition");
        mFeasList.add("SnackBar");
        mFeasList.add("TranslucentBar");
    }


    class AboutAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return viewType == 1
                    ? new ItemViewHolder(getLayoutInflater().inflate(R.layout.about_item, parent, false))
                    : new HeaderViewHolder(getLayoutInflater().inflate(R.layout.about_header, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                if (position < mLibsList.size() + 1) {
                    ((ItemViewHolder) holder).textView.setText(mLibsList.keyAt(position - 1));
                } else {
                    ((ItemViewHolder) holder).textView.setText(mFeasList.get(position - 2 - mLibsList.size()));
                    ((ItemViewHolder) holder).textView.setClickable(false);
                }
            } else {
                ((HeaderViewHolder) holder).textView.setText(position == 0 ? R.string.about_libs_used : R.string.about_feas_used);
            }

        }

        @Override
        public int getItemCount() {
            return mLibsList.size() + mFeasList.size() + 2;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 || position == mLibsList.size() + 1 ? 0 : 1;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class HeaderViewHolder extends ViewHolder {
        private TextView textView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView;
        }
    }

    private class ItemViewHolder extends ViewHolder {
        private TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = $(itemView, R.id.item_text);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();
                    if (pos < mLibsList.size() + 1 && pos != 0) {
                        CommonUtil.openUrl(AboutActivity.this, mLibsList.valueAt(pos - 1));
                    }
                }
            });
        }
    }

    private <T extends View> T $(int resId) {
        return (T) findViewById(resId);
    }

    private <T extends View> T $(View view, int resId) {
        return (T) view.findViewById(resId);
    }
}
