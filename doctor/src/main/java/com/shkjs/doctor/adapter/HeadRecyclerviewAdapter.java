package com.shkjs.doctor.adapter;

import android.content.Context;
import com.shkjs.doctor.base.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by Shuwen on 2016/10/8.
 */

public abstract class HeadRecyclerviewAdapter<T> extends BaseRecyclerAdapter<T> {

    private static final int HEADVIEW = -1;
    private static final int OTHERS = -2;

    public HeadRecyclerviewAdapter(Context ctx, List list) {
        super(ctx, list);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return HEADVIEW;
        }else {
            return OTHERS;
        }
    }
}
