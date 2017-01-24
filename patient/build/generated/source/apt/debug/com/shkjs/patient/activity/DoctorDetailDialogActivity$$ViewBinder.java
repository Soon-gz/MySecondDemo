// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DoctorDetailDialogActivity$$ViewBinder<T extends com.shkjs.patient.activity.DoctorDetailDialogActivity> implements ViewBinder<T> {
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
    view = finder.findRequiredView(source, 2131689688, "field 'contentTV'");
    target.contentTV = finder.castView(view, 2131689688, "field 'contentTV'");
    view = finder.findRequiredView(source, 2131689689, "field 'excelBtn'");
    target.excelBtn = finder.castView(view, 2131689689, "field 'excelBtn'");
    view = finder.findRequiredView(source, 2131689690, "field 'introduceBtn'");
    target.introduceBtn = finder.castView(view, 2131689690, "field 'introduceBtn'");
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
    target.contentTV = null;
    target.excelBtn = null;
    target.introduceBtn = null;
    target.linearLayout = null;
  }
}
