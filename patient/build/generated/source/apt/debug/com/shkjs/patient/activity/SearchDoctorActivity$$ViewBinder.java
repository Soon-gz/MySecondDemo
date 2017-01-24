// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SearchDoctorActivity$$ViewBinder<T extends com.shkjs.patient.activity.SearchDoctorActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131690479, "field 'noMessageTV'");
    target.noMessageTV = finder.castView(view, 2131690479, "field 'noMessageTV'");
    view = finder.findRequiredView(source, 2131690527, "field 'relativeLayout'");
    target.relativeLayout = finder.castView(view, 2131690527, "field 'relativeLayout'");
    view = finder.findRequiredView(source, 2131690528, "field 'swipeRefreshLayout'");
    target.swipeRefreshLayout = finder.castView(view, 2131690528, "field 'swipeRefreshLayout'");
    view = finder.findRequiredView(source, 2131690529, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131690529, "field 'recyclerView'");
    view = finder.findRequiredView(source, 2131689677, "field 'titleTV'");
    target.titleTV = finder.castView(view, 2131689677, "field 'titleTV'");
    view = finder.findRequiredView(source, 2131689910, "field 'titleMenuBtn'");
    target.titleMenuBtn = finder.castView(view, 2131689910, "field 'titleMenuBtn'");
    view = finder.findRequiredView(source, 2131689909, "field 'editText'");
    target.editText = finder.castView(view, 2131689909, "field 'editText'");
  }

  @Override public void unbind(T target) {
    target.noMessageTV = null;
    target.relativeLayout = null;
    target.swipeRefreshLayout = null;
    target.recyclerView = null;
    target.titleTV = null;
    target.titleMenuBtn = null;
    target.editText = null;
  }
}
