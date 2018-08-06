package com.example.ivor_hu.meizhi.ui.adapter;

import android.annotation.TargetApi;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.databinding.GirlsItemBinding;
import com.example.ivor_hu.meizhi.db.entity.Image;
import com.example.ivor_hu.meizhi.ui.callback.GirlItemCallback;

import java.util.ArrayList;


/**
 * Created by Ivor on 2016/2/6.
 */
public class GirlsAdapter extends PagedListAdapter<Image, GirlsAdapter.MyViewHolder> {
    private static final String TAG = "GirlsAdapter";

    private final Context mContext;
    private GirlItemCallback mCallback;

    public GirlsAdapter(Context context) {
        super(new DiffCallback());
        mContext = context;
        setHasStableIds(true);
    }

    public void setCallback(GirlItemCallback callback) {
        mCallback = callback;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GirlsItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.girls_item, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId().hashCode();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Image image = getItem(position);
        GirlsItemBinding binding = holder.getBinding();
        binding.networkImageview.setOriginalSize(image.getWidth(), image.getHeight());
        Glide.with(mContext)
                .load(image.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.networkImageview);
        ViewCompat.setTransitionName(binding.networkImageview, image.getUrl());

        if (mCallback != null) {
            binding.setIndex(position);
            binding.setCallback(mCallback);
        }
    }

    public String getUrlAt(int pos) {
        return getItem(pos).getUrl();
    }

    public ArrayList<Image> getImages() {
        PagedList<Image> imagePagedList = getCurrentList();
        ArrayList<Image> images = new ArrayList<>(imagePagedList.size());
        for (Image image : imagePagedList) {
            images.add(image);
        }
        return images;
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<Image> {

        @Override
        public boolean areItemsTheSame(Image oldItem, Image newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(Image oldItem, Image newItem) {
            return oldItem.getId().equals(newItem.getId())
                    && oldItem.getUrl().equals(newItem.getUrl());
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private GirlsItemBinding mBinding;

        public MyViewHolder(GirlsItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public GirlsItemBinding getBinding() {
            return mBinding;
        }
    }

}