// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ResultActivity$$ViewBinder<T extends com.shkjs.patient.activity.ResultActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689892, "field 'userIcon'");
    target.userIcon = finder.castView(view, 2131689892, "field 'userIcon'");
    view = finder.findRequiredView(source, 2131689895, "field 'userHospital'");
    target.userHospital = finder.castView(view, 2131689895, "field 'userHospital'");
    view = finder.findRequiredView(source, 2131689896, "field 'userDepartment'");
    target.userDepartment = finder.castView(view, 2131689896, "field 'userDepartment'");
    view = finder.findRequiredView(source, 2131689893, "field 'userName'");
    target.userName = finder.castView(view, 2131689893, "field 'userName'");
    view = finder.findRequiredView(source, 2131689894, "field 'userLevel'");
    target.userLevel = finder.castView(view, 2131689894, "field 'userLevel'");
    view = finder.findRequiredView(source, 2131689897, "field 'addTV'");
    target.addTV = finder.castView(view, 2131689897, "field 'addTV'");
  }

  @Override public void unbind(T target) {
    target.userIcon = null;
    target.userHospital = null;
    target.userDepartment = null;
    target.userName = null;
    target.userLevel = null;
    target.addTV = null;
  }
}
