package com.example.ivor_hu.meizhi;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ivor_hu.meizhi.databinding.AboutHeaderBinding;
import com.example.ivor_hu.meizhi.databinding.AboutItemBinding;
import com.example.ivor_hu.meizhi.databinding.ActivityAboutBinding;
import com.example.ivor_hu.meizhi.ui.callback.AboutTextCallback;
import com.example.ivor_hu.meizhi.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivor on 2016/2/25.
 */
public class AboutActivity extends AppCompatActivity {
    private ArrayMap<String, String> mLibsList;
    private List<String> mFeasList;
    private List<String> mCompsList;
    private List<String> mHeaderList;
    private AboutAdapter mAdapter;
    private ActivityAboutBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_about);

        Toolbar toolbar = mBinding.aboutToolbar;
        RecyclerView recyclerview = mBinding.aboutRecyclerview;

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        if (NavUtils.getParentActivityName(this) != null
                && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        try {
            mBinding.setVersion(CommonUtil.getVersionName(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initData();
        mAdapter = new AboutAdapter();
        recyclerview.setAdapter(mAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initData() {
        mHeaderList = new ArrayList<>();
        mHeaderList.add("Android Architecture Components");
        mHeaderList.add(getString(R.string.about_libs_used));
        mHeaderList.add(getString(R.string.about_feas_used));
        mCompsList = new ArrayList<>();
        mCompsList.add("Lifecycle");
        mCompsList.add("LiveData");
        mCompsList.add("ViewModel");
        mCompsList.add("Room");
        mCompsList.add("DataBinding");
        mCompsList.add("Paging");
        mLibsList = new ArrayMap<>();
        mLibsList.put("bumptech / Glide", "https://github.com/bumptech/glide");
        mLibsList.put("Mike Ortiz / TouchImageView", "https://github.com/MikeOrtiz/TouchImageView");
        mLibsList.put("Square / Retrofit", "https://github.com/square/retrofit");
        mFeasList = new ArrayList<>();
        mFeasList.add("DataBinding");
        mFeasList.add("CardView");
        mFeasList.add("CollapsingToolbarLayout");
        mFeasList.add("DrawerLayout");
        mFeasList.add("RecyclerView");
        mFeasList.add("Shared Element Transition");
        mFeasList.add("SnackBar");
        mFeasList.add("TranslucentBar");
    }

    class AboutAdapter extends RecyclerView.Adapter<ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;
        private final int firstHeaderPosition = 0;
        private final int secondHeaderPosition = 1 + mCompsList.size();
        private final int thirdHeaderPosition = 2 + mCompsList.size() + mLibsList.size();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return viewType == TYPE_ITEM
                    ? new ItemViewHolder((AboutItemBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.about_item, parent, false))
                    : new HeaderViewHolder((AboutHeaderBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.about_header, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (holder.getItemViewType() == TYPE_ITEM) {
                AboutItemBinding binding = ((ItemViewHolder) holder).getBinding();
                binding.itemText.setClickable(false);
                if (position < secondHeaderPosition) {
                    binding.setText(mCompsList.get(position - 1));
                } else if (position < thirdHeaderPosition) {
                    binding.setText(mLibsList.keyAt(position - secondHeaderPosition - 1));
                    binding.itemText.setClickable(true);
                    binding.setCallback(new AboutTextCallback() {
                        @Override
                        public void onTextClick() {
                            int pos = holder.getAdapterPosition();
                            if (pos < mAdapter.getThirdHeaderPosition() && pos > mAdapter.getSecondHeaderPosition()) {
                                CommonUtil.openUrl(AboutActivity.this, mLibsList.valueAt(pos - mAdapter.getSecondHeaderPosition() - 1));
                            }
                        }
                    });
                } else {
                    binding.setText(mFeasList.get(position - thirdHeaderPosition - 1));
                }
                binding.executePendingBindings();
            } else {
                String text;
                if (position == firstHeaderPosition) {
                    text = mHeaderList.get(0);
                } else if (position == thirdHeaderPosition) {
                    text = mHeaderList.get(mHeaderList.size() - 1);
                } else {
                    text = mHeaderList.get(1);
                }
                ((HeaderViewHolder) holder).getBinding().setText(text);
                ((HeaderViewHolder) holder).getBinding().executePendingBindings();
            }

        }

        @Override
        public int getItemCount() {
            return mLibsList.size() + mFeasList.size() + mHeaderList.size() + mCompsList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position == firstHeaderPosition
                    || position == secondHeaderPosition
                    || position == thirdHeaderPosition
                    ? TYPE_HEADER
                    : TYPE_ITEM;
        }

        public int getFirstHeaderPosition() {
            return firstHeaderPosition;
        }

        public int getSecondHeaderPosition() {
            return secondHeaderPosition;
        }

        public int getThirdHeaderPosition() {
            return thirdHeaderPosition;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class HeaderViewHolder extends ViewHolder {
        private AboutHeaderBinding mBinding;

        public HeaderViewHolder(AboutHeaderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public AboutHeaderBinding getBinding() {
            return mBinding;
        }
    }

    private class ItemViewHolder extends ViewHolder {
        private AboutItemBinding mBinding;

        public ItemViewHolder(AboutItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public AboutItemBinding getBinding() {
            return mBinding;
        }
    }
}
