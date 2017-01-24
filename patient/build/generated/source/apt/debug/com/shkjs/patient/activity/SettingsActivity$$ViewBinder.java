// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SettingsActivity$$ViewBinder<T extends com.shkjs.patient.activity.SettingsActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689913, "field 'checkBox'");
    target.checkBox = finder.castView(view, 2131689913, "field 'checkBox'");
    view = finder.findRequiredView(source, 2131689914, "field 'setPayPwdLL'");
    target.setPayPwdLL = finder.castView(view, 2131689914, "field 'setPayPwdLL'");
    view = finder.findRequiredView(source, 2131689916, "field 'modifyPwdLL'");
    target.modifyPwdLL = finder.castView(view, 2131689916, "field 'modifyPwdLL'");
    view = finder.findRequiredView(source, 2131689917, "field 'logoutLL'");
    target.logoutLL = finder.castView(view, 2131689917, "field 'logoutLL'");
    view = finder.findRequiredView(source, 2131689915, "field 'setPayPwdTV'");
    target.setPayPwdTV = finder.castView(view, 2131689915, "field 'setPayPwdTV'");
  }

  @Override public void unbind(T target) {
    target.checkBox = null;
    target.setPayPwdLL = null;
    target.modifyPwdLL = null;
    target.logoutLL = null;
    target.setPayPwdTV = null;
  }
}
