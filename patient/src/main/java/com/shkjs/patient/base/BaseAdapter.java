package com.shkjs.patient.base;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;

import java.util.List;

/**
 * Created by xiaohu on 2016/10/27.
 * <p>
 * adapter基类
 */

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    private Context context;
    private List<T> datalist;
    private LayoutInflater inflater;

    public BaseAdapter(Context context, List<T> datalist) {
        if (null == datalist) {
            throw new NullPointerException("datalist can not be null !");
        }
        this.context = context;
        this.datalist = datalist;
        inflater = LayoutInflater.from(context);
    }

    public void add(T t) {
        synchronized (datalist) {
            datalist.add(t);
        }
        notifyDataSetChanged();
    }

    public void add(int position, T t) {
        synchronized (datalist) {
            datalist.add(position, t);
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        synchronized (datalist) {
            if (position >= 0 && position < datalist.size()) {
                datalist.remove(position);
            }
        }
        notifyDataSetChanged();
    }

    public void remove(T t) {
        synchronized (datalist) {
            datalist.remove(t);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public T getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        BaseRecyclerViewHolder holder = null;
        if (null == view) {
            view = inflater.inflate(getItemLayoutId(getViewType()), null);
            holder = new BaseRecyclerViewHolder(context, view);
            view.setTag(holder);
        } else {
            holder = (BaseRecyclerViewHolder) view.getTag();
        }
        bindData(holder, position, datalist.get(position));
        return view;
    }

    public int getViewType() {
        return 0;
    }

    public abstract int getItemLayoutId(int viewType);

    public abstract void bindData(BaseRecyclerViewHolder holder, int position, T item);
}
