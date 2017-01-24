package com.shkjs.patient.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.raspberry.library.util.SharedPreferencesUtils;
import com.shkjs.patient.LoginManager;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.cache.DataCache;

/**
 * 基础Fragment，子类Fragment都继承自它，尤其是ViewPager中的Fragment
 * 因为ViewPager+Fragment组合使用时会产生预先加载前后Fragment，若再加上比较大的数据量，就会造成卡顿的现象，
 * 为防止发生，可以在用户可见的时候加载数据，因此才会写一个BaseFragment
 */
public class BaseFragment extends Fragment {

    protected BackHandledInterface mBackHandledInterface;

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getActivity() instanceof BackHandledInterface)) {
            this.mBackHandledInterface = (BackHandledInterface) getActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!DataCache.getInstance().isLoginNim() && SharedPreferencesUtils.getBoolean(MyApplication.IS_AUTO_LOGIN)) {
            LoginManager.loginNim(getActivity(), null);
        }
    }

    /**
     * 当前界面是否呈现给用户的状态标志
     */
    protected boolean isVisible;

    /**
     * 重写Fragment父类生命周期方法，在onCreate之前调用该方法，实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser 当前是否已将界面显示给用户的状态
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 当界面呈现给用户，即设置可见时执行，进行加载数据的方法
     * 在用户可见时加载数据，而不在用户不可见的时候加载数据，是为了防止控件对象出现空指针异常
     */
    protected void onVisible() {
        if (null != mBackHandledInterface) {
            //告诉FragmentActivity，当前Fragment在栈顶
            mBackHandledInterface.setSelectedFragment(this);
        }
        setlazyLoad();
    }

    /**
     * 当界面还没呈现给用户，即设置不可见时执行
     */
    protected void onInvisible() {
    }

    /**
     * 加载数据方法
     */
    protected void setlazyLoad() {
    }

    public interface BackHandledInterface {

        public abstract void setSelectedFragment(BaseFragment selectedFragment);
    }
}
