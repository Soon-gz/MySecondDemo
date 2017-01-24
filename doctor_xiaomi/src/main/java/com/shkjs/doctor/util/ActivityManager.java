package com.shkjs.doctor.util;

import android.app.Activity;
import android.util.Log;


import java.util.Stack;

/**
 * Created by xiaohu on 2016/9/11.
 * <p>
 * Activity 管理类
 */

public class ActivityManager {

    private static ActivityManager manager;
    private static Stack<Activity> stack;

    /**
     * 获取Activity 管理类单例
     *
     * @return manager
     */
    public static ActivityManager getInstance() {
        if (null == manager) {
            synchronized (ActivityManager.class) {
                if (null == manager) {
                    manager = new ActivityManager();
                }
            }
        }
        return manager;
    }

    /**
     * 将activity放入栈中
     *
     * @param activity
     */
    public void putActivity(Activity activity) {
        if (null == stack) {
            stack = new Stack<Activity>();
        }
        Log.i("TAG00", "添加的Activity:" + activity.getLocalClassName());
        stack.add(activity);
    }

    /**
     * 获取栈顶Activity
     *
     * @return
     */
    public Activity getActivity() {
        if (null == stack) {
            return null;
        } else {
            return stack.lastElement();
        }
    }

    /**
     * 结束栈顶activity
     */
    public void finishActivity() {
        finishACtivity(getActivity());
    }

    /**
     * 结束指定activity
     *
     * @param activity
     */
    public void finishACtivity(Activity activity) {
        if ((null != activity) && (null != stack)) {
            activity.finish();
            Log.i("tag00", "finishActivity：" + activity.getLocalClassName());
            stack.remove(activity);
        }
    }

    /**
     * 结束指定类的activity
     *
     * @param clazz
     */
    public void finishActivity(Class<?> clazz) {
        if ((null != clazz) && (null != stack)) {
            //            for (Activity activity : stack) {
            //                if (activity.getClass().equals(clazz)){
            //                    finishACtivity(activity);
            //                }
            //            }

            for (int i = 0; i < stack.size(); i++) {
                Activity activity = stack.get(i);
                if (activity.getClass().equals(clazz)) {
                    finishACtivity(activity);
                }
            }
        }
    }

    /**
     * 结束所有activity
     */
    public void finishAllActivity() {
        if (null != stack) {
            //            for (Activity activity : stack) {
            //                finishACtivity(activity);
            //            }
            for (int i = 0; i < stack.size(); i++) {
                finishACtivity(stack.get(i));
            }
            stack.clear();
            stack = null;
        }
    }

}
