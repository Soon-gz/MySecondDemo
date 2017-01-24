// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ModifyPayPwdActivity$$ViewBinder<T extends com.shkjs.patient.activity.ModifyPayPwdActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689797, "field 'titleTV'");
    target.titleTV = finder.castView(view, 2131689797, "field 'titleTV'");
    view = finder.findRequiredView(source, 2131689798, "field 'payPwdEditText'");
    target.payPwdEditText = finder.castView(view, 2131689798, "field 'payPwdEditText'");
  }

  @Override public void unbind(T target) {
    target.titleTV = null;
    target.payPwdEditText = null;
  }
}
