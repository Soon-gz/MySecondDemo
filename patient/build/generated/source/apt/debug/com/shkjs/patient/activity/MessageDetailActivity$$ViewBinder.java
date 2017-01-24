// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MessageDetailActivity$$ViewBinder<T extends com.shkjs.patient.activity.MessageDetailActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689784, "field 'contentTV'");
    target.contentTV = finder.castView(view, 2131689784, "field 'contentTV'");
  }

  @Override public void unbind(T target) {
    target.contentTV = null;
  }
}
