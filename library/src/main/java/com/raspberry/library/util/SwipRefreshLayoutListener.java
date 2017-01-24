package com.raspberry.library.util;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by Shuwen on 2016/9/25.
 */

public class SwipRefreshLayoutListener {

    //直接获得刷新监听事件
    public static SwipeRefreshLayout.OnRefreshListener addOnRefreshListener(final SwipeRefreshLayout swipeRefreshLayout, final OnRefreshEndListner onRefreshEndListner){
        /**
         * 刷新监听。
         */
         SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onRefreshEndListner.doSomethings();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        };
        return mOnRefreshListener;
    }

    public interface OnRefreshEndListner{
        void doSomethings();
    }

}
