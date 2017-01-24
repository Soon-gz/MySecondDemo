// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class OrderTimeActivity$$ViewBinder<T extends com.shkjs.patient.activity.OrderTimeActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689832, "field 'visit_time_viewpager_vp'");
    target.visit_time_viewpager_vp = finder.castView(view, 2131689832, "field 'visit_time_viewpager_vp'");
    view = finder.findRequiredView(source, 2131689831, "field 'visit_time_date_ll'");
    target.visit_time_date_ll = finder.castView(view, 2131689831, "field 'visit_time_date_ll'");
  }

  @Override public void unbind(T target) {
    target.visit_time_viewpager_vp = null;
    target.visit_time_date_ll = null;
  }
}
