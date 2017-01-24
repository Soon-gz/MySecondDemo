// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class RetrievePwdActivity$$ViewBinder<T extends com.shkjs.patient.activity.RetrievePwdActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689905, "field 'userNameET'");
    target.userNameET = finder.castView(view, 2131689905, "field 'userNameET'");
    view = finder.findRequiredView(source, 2131689908, "field 'userPwdET'");
    target.userPwdET = finder.castView(view, 2131689908, "field 'userPwdET'");
    view = finder.findRequiredView(source, 2131689906, "field 'codeET'");
    target.codeET = finder.castView(view, 2131689906, "field 'codeET'");
    view = finder.findRequiredView(source, 2131689907, "field 'codeTV'");
    target.codeTV = finder.castView(view, 2131689907, "field 'codeTV'");
    view = finder.findRequiredView(source, 2131689849, "field 'submitBtn'");
    target.submitBtn = finder.castView(view, 2131689849, "field 'submitBtn'");
  }

  @Override public void unbind(T target) {
    target.userNameET = null;
    target.userPwdET = null;
    target.codeET = null;
    target.codeTV = null;
    target.submitBtn = null;
  }
}
