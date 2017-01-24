package com.shkjs.patient.util;

import android.app.Activity;

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
        synchronized (stack) {
            stack.add(activity);
        }
    }

    /**
     * 获取栈顶activity
     *
     * @return
     */
    public Activity getActivity() {
        if (null == stack) {
            return null;
        } else {
            synchronized (stack) {
                return stack.lastElement();
            }
        }
    }

    /**
     * 结束栈顶activity
     */
    public void finishActivity() {
        finishActivity(getActivity());
    }

    /**
     * 结束指定activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if ((null != activity) && (null != stack)) {
            synchronized (stack) {
                activity.finish();
                stack.remove(activity);
                activity = null;
            }
        }
    }

    /**
     * 结束指定类的activity
     *
     * @param clazz
     */
    public void finishActivity(Class<?> clazz) {
        if ((null != clazz) && (null != stack)) {
            synchronized (stack) {
                for (int i = 0; i < stack.size(); i++) {
                    Activity activity = stack.get(i);
                    if (activity.getClass().equals(clazz)) {
                        finishActivity(activity);
                    }
                }
            }
        }
    }

    /**
     * 结束所有activity
     */

    public void finishAllActivity() {
        if (null != stack) {
            synchronized (stack) {
                while (stack.size() > 0) {
                    finishActivity();
                }
                stack.clear();
            }
        }
    }

}
