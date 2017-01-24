// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class BalanceDetailActivity$$ViewBinder<T extends com.shkjs.patient.activity.BalanceDetailActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131690479, "field 'noMessageTV'");
    target.noMessageTV = finder.castView(view, 2131690479, "field 'noMessageTV'");
    view = finder.findRequiredView(source, 2131690540, "field 'swipeRefreshLayout'");
    target.swipeRefreshLayout = finder.castView(view, 2131690540, "field 'swipeRefreshLayout'");
    view = finder.findRequiredView(source, 2131690123, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131690123, "field 'recyclerView'");
  }

  @Override public void unbind(T target) {
    target.noMessageTV = null;
    target.swipeRefreshLayout = null;
    target.recyclerView = null;
  }
}
