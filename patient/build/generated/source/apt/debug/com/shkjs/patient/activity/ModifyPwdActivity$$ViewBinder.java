// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ModifyPwdActivity$$ViewBinder<T extends com.shkjs.patient.activity.ModifyPwdActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689799, "field 'userPwdLL'");
    target.userPwdLL = finder.castView(view, 2131689799, "field 'userPwdLL'");
    view = finder.findRequiredView(source, 2131689800, "field 'payPwdLL'");
    target.payPwdLL = finder.castView(view, 2131689800, "field 'payPwdLL'");
    view = finder.findRequiredView(source, 2131689801, "field 'retrievePayPwdLL'");
    target.retrievePayPwdLL = finder.castView(view, 2131689801, "field 'retrievePayPwdLL'");
  }

  @Override public void unbind(T target) {
    target.userPwdLL = null;
    target.payPwdLL = null;
    target.retrievePayPwdLL = null;
  }
}
