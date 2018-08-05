package com.example.ivor_hu.meizhi.ui.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.databinding.SearchItemBinding;
import com.example.ivor_hu.meizhi.db.entity.SearchEntity;
import com.example.ivor_hu.meizhi.ui.callback.SearchItemCallback;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.utils.DateUtil;

import java.text.ParseException;

/**
 * Created by ivor on 16-6-17.
 */
public class SearchAdapter extends PagedListAdapter<SearchEntity, SearchAdapter.Viewholder> {
    private static final String TAG = "SearchAdapter";
    private Context mContext;
    private SearchItemCallback mCallback;

    public SearchAdapter(Context context) {
        super(new DiffCallback());
        this.mContext = context;
        setHasStableIds(true);
    }

    public void setCallback(SearchItemCallback callback) {
        mCallback = callback;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        SearchItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.search_item, parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        final SearchEntity searchEntity = getItem(position);
        SearchItemBinding binding = holder.getBinding();
        binding.setSearchEntity(searchEntity);
        try {
            binding.stuffDate.setText(DateUtil.formatSearchDate(searchEntity.getPublishedAt()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (mCallback != null) {
            binding.setCallback(mCallback);
        }

        if (CommonUtil.isWifiConnected(mContext)
                && !TextUtils.isEmpty(searchEntity.getReadability())) {
            binding.readabilityWv.setVisibility(View.VISIBLE);
            binding.readabilityWv.setTag(position);
            binding.readabilityWv.getSettings().setUseWideViewPort(true);
            binding.readabilityWv.getSettings().setLoadWithOverviewMode(true);
            binding.readabilityWv.getSettings().setDefaultFontSize(48);
            binding.readabilityWv.loadData(searchEntity.getReadability(), "text/html; charset=UTF-8", "utf8");
            binding.readabilityWv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_UP:
                            mCallback.onItemClick(view, searchEntity);
                            return true;
                        default:
                            return true;
                    }
                }
            });
        } else {
            binding.readabilityWv.setVisibility(View.GONE);
        }

    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getUrl().hashCode();
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<SearchEntity> {
        @Override
        public boolean areItemsTheSame(SearchEntity oldItem, SearchEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(SearchEntity oldItem, SearchEntity newItem) {
            return oldItem.getUrl().equals(newItem.getUrl());
        }
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private SearchItemBinding mBinding;

        public Viewholder(SearchItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public SearchItemBinding getBinding() {
            return mBinding;
        }
    }

}
