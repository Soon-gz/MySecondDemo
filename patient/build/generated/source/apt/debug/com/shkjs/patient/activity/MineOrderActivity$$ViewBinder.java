// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MineOrderActivity$$ViewBinder<T extends com.shkjs.patient.activity.MineOrderActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689795, "field 'tabLayout'");
    target.tabLayout = finder.castView(view, 2131689795, "field 'tabLayout'");
    view = finder.findRequiredView(source, 2131689796, "field 'viewPager'");
    target.viewPager = finder.castView(view, 2131689796, "field 'viewPager'");
  }

  @Override public void unbind(T target) {
    target.tabLayout = null;
    target.viewPager = null;
  }
}
