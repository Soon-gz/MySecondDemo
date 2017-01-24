// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class AboutActivity$$ViewBinder<T extends com.shkjs.patient.activity.AboutActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689615, "field 'androidIV'");
    target.androidIV = finder.castView(view, 2131689615, "field 'androidIV'");
    view = finder.findRequiredView(source, 2131689614, "field 'iosIV'");
    target.iosIV = finder.castView(view, 2131689614, "field 'iosIV'");
    view = finder.findRequiredView(source, 2131689616, "field 'codeTV'");
    target.codeTV = finder.castView(view, 2131689616, "field 'codeTV'");
  }

  @Override public void unbind(T target) {
    target.androidIV = null;
    target.iosIV = null;
    target.codeTV = null;
  }
}
