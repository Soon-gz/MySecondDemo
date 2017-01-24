// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HomeGroupActivity$$ViewBinder<T extends com.shkjs.patient.activity.HomeGroupActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689715, "field 'initialLL'");
    target.initialLL = finder.castView(view, 2131689715, "field 'initialLL'");
    view = finder.findRequiredView(source, 2131689717, "field 'notInitialLL'");
    target.notInitialLL = finder.castView(view, 2131689717, "field 'notInitialLL'");
    view = finder.findRequiredView(source, 2131689716, "field 'addMemberBtn'");
    target.addMemberBtn = finder.castView(view, 2131689716, "field 'addMemberBtn'");
    view = finder.findRequiredView(source, 2131690528, "field 'refreshLayout'");
    target.refreshLayout = finder.castView(view, 2131690528, "field 'refreshLayout'");
    view = finder.findRequiredView(source, 2131690529, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131690529, "field 'recyclerView'");
  }

  @Override public void unbind(T target) {
    target.initialLL = null;
    target.notInitialLL = null;
    target.addMemberBtn = null;
    target.refreshLayout = null;
    target.recyclerView = null;
  }
}
