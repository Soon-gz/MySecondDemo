package com.raspberry.library.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xiaohu on 2016/9/30.
 */

public class RecyclerViewUtlis {

    public static boolean isLastMessageVisible(RecyclerView messageRecyclerView) {
        if (messageRecyclerView == null || messageRecyclerView.getAdapter() == null) {
            return false;
        }

        View lastVisibleChild = messageRecyclerView.getChildAt(messageRecyclerView.getChildCount() - 1);
        int lastVisiblePosition = lastVisibleChild != null ? messageRecyclerView.getChildAdapterPosition
                (lastVisibleChild) : -1;

        if (lastVisiblePosition >= messageRecyclerView.getChildCount() - 1) {
            return true;
        } else {
            return false;
        }
    }

    public static Object getViewHolderByIndex(RecyclerView recyclerView, int index) {
        int firstVisibleFeedPosition = -1;
        int lastVisibleFeedPosition = -1;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //判断是当前layoutManager是否为LinearLayoutManager
        // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            //获取第一个可见view的位置
            firstVisibleFeedPosition = linearManager.findFirstVisibleItemPosition();
            //获取最后一个可见view的位置
            lastVisibleFeedPosition = linearManager.findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            //获取第一个可见view的位置
            firstVisibleFeedPosition = gridLayoutManager.findFirstVisibleItemPosition();
            //获取最后一个可见view的位置
            lastVisibleFeedPosition = gridLayoutManager.findLastVisibleItemPosition();
            lastVisibleFeedPosition = gridLayoutManager.findLastVisibleItemPosition();
        }
        //只有获取可见区域的
        if (index >= firstVisibleFeedPosition && index <= lastVisibleFeedPosition) {
            View view = recyclerView.getChildAt(index - firstVisibleFeedPosition);
            Object tag = view.getTag();
            return tag;
        } else {
            return null;
        }
    }

    /**
     * 第一个item位置
     *
     * @param recyclerView
     * @return
     */
    public static int getFirstVisibleItemPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            return gridLayoutManager.findFirstVisibleItemPosition();
        }
        return -1;
    }

    /**
     * 最后一个item位置
     *
     * @param recyclerView
     * @return
     */
    public static int getLastVisibleItemPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            return gridLayoutManager.findLastVisibleItemPosition();
        }
        return -1;
    }

    /**
     * 是否可以上拉加载更多
     *
     * @param recyclerView
     * @return
     */
    public static boolean canPullUp(RecyclerView recyclerView) {
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        int mFirstVisiblePosition = getFirstVisibleItemPosition(recyclerView);
        int mLastVisiblePosition = getLastVisibleItemPosition(recyclerView);
        int count = recyclerView.getAdapter().getItemCount();

        if (count > 0 && mLastVisiblePosition == (count - 1)) {
            // 滑到底部了可以上拉加载
            int height = 0;
            int size = mLastVisiblePosition - mFirstVisiblePosition;
            for (int i = 0; i <= size; i++) {
                height += recyclerView.getChildAt(i).getMeasuredHeight();
            }
            int measureHeight = recyclerView.getMeasuredHeight();
            if (lm.findViewByPosition(count - 1).getBottom() <= measureHeight && height >= measureHeight) {
                return true;
            }
        }
        return false;
    }
}
