package com.wuxiaosu.litepal_demo.adapter;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Su on 2016/8/13.
 */
public abstract class BaseAdapter<E> extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {

    private Context context;
    private List<E> list;

    //header 和 footer 类型 基数
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArray<View> mHeaderViews = new SparseArray<>();
    private SparseArray<View> mFootViews = new SparseArray<>();

    public BaseAdapter(Context context, List<E> list) {
        this.context = context;
        this.list = list;
    }

    public Context getContext() {
        return context;
    }

    public List<E> getList() {
        return list;
    }

    public void add(E object) {
        list.add(object);
        notifyDataSetChanged();
    }

    public void addAll(List<E> object) {
        list.addAll(object);
        notifyDataSetChanged();
    }

    public void remove(int location) {
        list.remove(location);
        notifyDataSetChanged();
    }

    public void remove(E object) {
        list.remove(object);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            return (new ViewHolder(mHeaderViews.get(viewType)));
        } else if (mFootViews.get(viewType) != null) {
            return (new ViewHolder(mFootViews.get(viewType)));
        }
        return (new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(viewType), parent, false)));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }
        if (mClickLister != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickLister.onItemClick(holder.itemView, position - getHeadersCount());
                }
            });
        }
        if (mLongClickLister != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLongClickLister.onItemLongClick(holder.itemView, position - getHeadersCount());
                }
            });
        }
        onBindView(holder, getList().get(position - getHeadersCount()), position - getHeadersCount());
    }

    public abstract void onBindView(ViewHolder holder, E data, int position);

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mViews = new SparseArray<>();
        }

        public <T extends View> T getView(@IdRes int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }
    }

    @LayoutRes
    public abstract int getItemLayoutId(int viewType);

    /**
     * 获取item 类型
     *
     * @param position
     * @return
     */
    @CallSuper
    @Override
    @IntRange(from = 1, to = 100000)
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return getHeaderViewType(position);
        } else if (isFooterViewPos(position)) {
            return getFooterViewType(position);
        }
        return super.getItemViewType(position);
    }

    private int getHeaderViewType(int position) {
        return mHeaderViews.keyAt(position);
    }

    private int getFooterViewType(int position) {
        return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    public int getRealItemCount() {
        return list.size();
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    /**
     * 添加 footer
     *
     * @param view
     */
    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    /**
     * 添加 footer
     *
     * @param view
     */
    public void addFootView(View view) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public void removeFootView() {
        mFootViews.remove(mFootViews.size() + BASE_ITEM_TYPE_FOOTER - 1);
    }

    private int getHeadersCount() {
        return mHeaderViews.size();
    }

    private int getFootersCount() {
        return mFootViews.size();
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnRecyclerItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private OnRecyclerItemClickListener mClickLister;
    private OnRecyclerItemLongClickListener mLongClickLister;

    /**
     * 设置 item 点击事件
     *
     * @param mClickLister
     */
    public void setItemClickLister(OnRecyclerItemClickListener mClickLister) {
        this.mClickLister = mClickLister;
    }

    /**
     * 设置 item 长按事件
     *
     * @param mLongClickLister
     */
    public void setItemLongClickLister(OnRecyclerItemLongClickListener mLongClickLister) {
        this.mLongClickLister = mLongClickLister;
    }
}
