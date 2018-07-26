package com.example.ivor_hu.meizhi.ui.adapter;

import android.arch.paging.PagedListAdapter;
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

/**
 * Created by ivor on 16-6-21.
 */
public class StuffAdapter extends PagedListAdapter<Stuff, StuffAdapter.Viewholder> {
    private static final String TAG = "StuffAdapter";
    protected final Context mContext;
    protected StuffItemCallback mCallback;

    public StuffAdapter(Context context) {
        super(new DiffCallback());
        this.mContext = context;
        setHasStableIds(true);
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        StuffItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.stuff_item, parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, final int position) {
        Stuff stuff = getItem(position);
        holder.getBinding().setStuff(stuff);
        holder.getBinding().stuffDate.setText(DateUtil.format(stuff.getPublishedAt()));
        if (mCallback != null) {
            holder.getBinding().setCallback(mCallback);
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId().hashCode();
    }

    public void setCallback(StuffItemCallback callback) {
        mCallback = callback;
    }

//    public void addStuffs(List<Stuff> stuffs) {
//        if (stuffs == null) {
//            return;
//        }
//
//        mStuffs.addAll(stuffs);
//        notifyItemRangeInserted(getItemCount(), stuffs.size());
//    }

//    public void clearStuff() {
//        mStuffs.clear();
//    }

//    public void updateStuffs(List<Stuff> stuffs) {
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(mStuffs, stuffs), true);
//        diffResult.dispatchUpdatesTo(this);
//        mStuffs = stuffs;
//    }

    public static class DiffCallback extends DiffUtil.ItemCallback<Stuff> {

        @Override
        public boolean areItemsTheSame(Stuff oldItem, Stuff newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(Stuff oldItem, Stuff newItem) {
            return oldItem.getType().equals(newItem.getType())
                    && oldItem.getUrl().equals(newItem.getUrl())
                    && oldItem.getDesc().equals(newItem.getDesc());
        }
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
}
