// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DoctorsDetailDialogActivity$$ViewBinder<T extends com.shkjs.patient.activity.DoctorsDetailDialogActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689671, "field 'doctorIconIV'");
    target.doctorIconIV = finder.castView(view, 2131689671, "field 'doctorIconIV'");
    view = finder.findRequiredView(source, 2131689673, "field 'doctorNameTV'");
    target.doctorNameTV = finder.castView(view, 2131689673, "field 'doctorNameTV'");
    view = finder.findRequiredView(source, 2131689687, "field 'doctorPlatformTV'");
    target.doctorPlatformTV = finder.castView(view, 2131689687, "field 'doctorPlatformTV'");
    view = finder.findRequiredView(source, 2131689679, "field 'departmentTV'");
    target.departmentTV = finder.castView(view, 2131689679, "field 'departmentTV'");
    view = finder.findRequiredView(source, 2131689674, "field 'doctorLevelTV'");
    target.doctorLevelTV = finder.castView(view, 2131689674, "field 'doctorLevelTV'");
    view = finder.findRequiredView(source, 2131689678, "field 'hospitalTV'");
    target.hospitalTV = finder.castView(view, 2131689678, "field 'hospitalTV'");
    view = finder.findRequiredView(source, 2131689682, "field 'excelTV'");
    target.excelTV = finder.castView(view, 2131689682, "field 'excelTV'");
    view = finder.findRequiredView(source, 2131689683, "field 'introduceTV'");
    target.introduceTV = finder.castView(view, 2131689683, "field 'introduceTV'");
    view = finder.findRequiredView(source, 2131689691, "field 'tabLayout'");
    target.tabLayout = finder.castView(view, 2131689691, "field 'tabLayout'");
    view = finder.findRequiredView(source, 2131689686, "field 'linearLayout'");
    target.linearLayout = finder.castView(view, 2131689686, "field 'linearLayout'");
  }

  @Override public void unbind(T target) {
    target.doctorIconIV = null;
    target.doctorNameTV = null;
    target.doctorPlatformTV = null;
    target.departmentTV = null;
    target.doctorLevelTV = null;
    target.hospitalTV = null;
    target.excelTV = null;
    target.introduceTV = null;
    target.tabLayout = null;
    target.linearLayout = null;
  }
}
