// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ModifyUserPwdActivity$$ViewBinder<T extends com.shkjs.patient.activity.ModifyUserPwdActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689802, "field 'oldPwdET'");
    target.oldPwdET = finder.castView(view, 2131689802, "field 'oldPwdET'");
    view = finder.findRequiredView(source, 2131689803, "field 'newPwdET'");
    target.newPwdET = finder.castView(view, 2131689803, "field 'newPwdET'");
    view = finder.findRequiredView(source, 2131689805, "field 'submitBtn'");
    target.submitBtn = finder.castView(view, 2131689805, "field 'submitBtn'");
  }

  @Override public void unbind(T target) {
    target.oldPwdET = null;
    target.newPwdET = null;
    target.submitBtn = null;
  }
}
