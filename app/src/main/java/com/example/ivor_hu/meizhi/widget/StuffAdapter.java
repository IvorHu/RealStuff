package com.example.ivor_hu.meizhi.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ivor_hu.meizhi.MainActivity;
import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.db.Stuff;
import com.example.ivor_hu.meizhi.utils.DateUtil;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Ivor on 2016/2/28.
 */
public class StuffAdapter extends RecyclerView.Adapter<StuffAdapter.Viewholder> {
    private static final String TAG = "StuffAdapter";
    private final Context mContext;
    private RealmResults<Stuff> mStuffs;
    private OnItemClickListener mOnItemClickListener;
    private int lastStuffsNum;
    private final Realm realm;
    private final String mType;
    private boolean mIsCollections;

    public void updateInsertedData(int numImages, boolean isMore) {
        if (isMore) {
            notifyItemRangeInserted(lastStuffsNum - 1, numImages);
            Log.d(TAG, "updateInsertedData: from " + (lastStuffsNum - 1) + " by " + numImages);
        } else {
            notifyItemRangeInserted(0, numImages);
            Log.d(TAG, "updateInsertedData: from 0 to " + numImages);
        }
        lastStuffsNum += numImages;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);

        void onItemLongClick(View view, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public StuffAdapter(Context mContext, Realm realm, String type) {
        this.mContext = mContext;
        this.realm = realm;
        this.mType = type;
        this.mIsCollections = MainActivity.TYPE.COLLECTIONS.getApiName().equals(mType) ? true : false;
        if (mIsCollections) {
            mStuffs = Stuff.collections(realm);
        } else {
            mStuffs = Stuff.all(realm, mType);
        }
        lastStuffsNum = mStuffs.size();
        setHasStableIds(true);
    }

    @Override
    public StuffAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(mContext).inflate(R.layout.stuff_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, final int position) {
        final Stuff stuff = mStuffs.get(position);
        holder.author.setText(stuff.getWho());
        holder.title.setText(stuff.getDesc());
        holder.date.setText(DateUtil.format(stuff.getPublishedAt()));
        if (null != mOnItemClickListener) {
            holder.stuff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });

            holder.stuff.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(v, position);
                    return true;
                }
            });
        }
        holder.likeBtn.setTag(position);
        if (mIsCollections) {
            holder.likeBtn.setImageResource(R.drawable.like);
            holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(position);
                }
            });
        } else {
            holder.likeBtn.setImageResource(mStuffs.get(position).isLiked() ? R.drawable.like : R.drawable.unlike);
            holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleLikeBtn((ImageButton) v, position);
                }
            });
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

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView title, author, date;
        private LinearLayout stuff;
        private ImageButton likeBtn;

        public Viewholder(final View itemView) {
            super(itemView);
            title = $(itemView, R.id.stuff_title);
            author = $(itemView, R.id.stuff_author);
            date = $(itemView, R.id.stuff_date);
            stuff = $(itemView, R.id.stuff);
            likeBtn = $(itemView, R.id.like_btn);
        }

    }

    private <T extends View> T $(View view, int resId) {
        return (T) view.findViewById(resId);
    }

    private void deleteItem(int position) {
        realm.beginTransaction();
        mStuffs.where()
                .equalTo("id", mStuffs.get(position).getId())
                .findFirst()
                .setLiked(false);
        realm.commitTransaction();
        notifyDataSetChanged();
    }

    private void toggleLikeBtn(ImageButton likeBtn, int pos) {

        if (mStuffs.get(pos).isLiked()) {
            likeBtn.setImageResource(R.drawable.unlike);
            changeLiked(pos, false);
        } else {
            likeBtn.setImageResource(R.drawable.like);
            changeLiked(pos, true);
        }
    }

    private void changeLiked(int pos, boolean isLiked) {
        realm.beginTransaction();
        Stuff stuff = mStuffs
                .where()
                .equalTo("id", mStuffs.get(pos).getId())
                .findFirst();

        stuff.setLiked(isLiked);
        stuff.setLastChanged(new Date());
        realm.commitTransaction();
        notifyItemChanged(pos);
    }

    public Stuff getStuffAt(int pos) {
        return mStuffs.get(pos);
    }

}
