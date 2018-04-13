package com.example.ivor_hu.meizhi.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.databinding.StuffItemBinding;
import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.ui.callback.StuffItemCallback;
import com.example.ivor_hu.meizhi.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivor on 16-6-21.
 */
public class StuffAdapter extends RecyclerView.Adapter<StuffAdapter.Viewholder> {
    private static final String TAG = "StuffAdapter";
    protected final Context mContext;
    protected final String mType;
    protected List<Stuff> mStuffs;
    protected StuffItemCallback mCallback;

    public StuffAdapter(Context context, String type) {
        this.mContext = context;
        this.mType = type;
        mStuffs = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        StuffItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.stuff_item, parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, final int position) {
        Stuff stuff = mStuffs.get(position);
        holder.getBinding().setStuff(stuff);
        holder.getBinding().stuffDate.setText(DateUtil.format(stuff.getPublishedAt()));
        if (mCallback != null) {
            holder.getBinding().setCallback(mCallback);
        }
    }

    @Override
    public int getItemCount() {
        return mStuffs.size();
    }

    @Override
    public long getItemId(int position) {
        return mStuffs.get(position).getId().hashCode();
    }

    public void setCallback(StuffItemCallback callback) {
        mCallback = callback;
    }

    public void addStuffs(List<Stuff> stuffs) {
        if (stuffs == null) {
            return;
        }

        mStuffs.addAll(stuffs);
        notifyItemRangeInserted(getItemCount(), stuffs.size());
    }

    public void clearStuff() {
        mStuffs.clear();
    }

    public void updateStuffs(List<Stuff> stuffs) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(mStuffs, stuffs), true);
        diffResult.dispatchUpdatesTo(this);
        mStuffs = stuffs;
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private StuffItemBinding mBinding;

        public Viewholder(StuffItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public StuffItemBinding getBinding() {
            return mBinding;
        }
    }

    public class DiffCallback extends DiffUtil.Callback {
        private List<Stuff> mOld, mNew;

        public DiffCallback(List<Stuff> mOld, List<Stuff> mNew) {
            this.mOld = mOld;
            this.mNew = mNew;
        }

        @Override
        public int getOldListSize() {
            return mOld.size();
        }

        @Override
        public int getNewListSize() {
            return mNew.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOld.get(oldItemPosition).getId().equals(mNew.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Stuff oldItem = mOld.get(oldItemPosition);
            Stuff newItem = mNew.get(newItemPosition);
            return !oldItem.getType().equals(newItem.getType())
                    && !oldItem.getDesc().equals(newItem.getDesc())
                    && !oldItem.getUrl().equals(newItem.getUrl());
        }
    }
}
