// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class RegisterActivity$$ViewBinder<T extends com.shkjs.patient.activity.RegisterActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689885, "field 'userNameET'");
    target.userNameET = finder.castView(view, 2131689885, "field 'userNameET'");
    view = finder.findRequiredView(source, 2131689888, "field 'userPwdET'");
    target.userPwdET = finder.castView(view, 2131689888, "field 'userPwdET'");
    view = finder.findRequiredView(source, 2131689886, "field 'codeET'");
    target.codeET = finder.castView(view, 2131689886, "field 'codeET'");
    view = finder.findRequiredView(source, 2131689887, "field 'codeTV'");
    target.codeTV = finder.castView(view, 2131689887, "field 'codeTV'");
    view = finder.findRequiredView(source, 2131689889, "field 'submitBtn'");
    target.submitBtn = finder.castView(view, 2131689889, "field 'submitBtn'");
    view = finder.findRequiredView(source, 2131689890, "field 'checkBox'");
    target.checkBox = finder.castView(view, 2131689890, "field 'checkBox'");
    view = finder.findRequiredView(source, 2131689891, "field 'agreementTV'");
    target.agreementTV = finder.castView(view, 2131689891, "field 'agreementTV'");
  }

  @Override public void unbind(T target) {
    target.userNameET = null;
    target.userPwdET = null;
    target.codeET = null;
    target.codeTV = null;
    target.submitBtn = null;
    target.checkBox = null;
    target.agreementTV = null;
  }
}
