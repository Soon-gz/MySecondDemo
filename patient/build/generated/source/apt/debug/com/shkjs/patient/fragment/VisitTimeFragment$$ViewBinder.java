// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class VisitTimeFragment$$ViewBinder<T extends com.shkjs.patient.fragment.VisitTimeFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131690123, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131690123, "field 'recyclerView'");
    view = finder.findRequiredView(source, 2131690479, "field 'noMsgTV'");
    target.noMsgTV = finder.castView(view, 2131690479, "field 'noMsgTV'");
  }

  @Override public void unbind(T target) {
    target.recyclerView = null;
    target.noMsgTV = null;
  }
}
