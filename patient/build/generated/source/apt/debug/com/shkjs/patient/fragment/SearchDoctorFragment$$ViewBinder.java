// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SearchDoctorFragment$$ViewBinder<T extends com.shkjs.patient.fragment.SearchDoctorFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131690116, "field 'adLL'");
    target.adLL = finder.castView(view, 2131690116, "field 'adLL'");
    view = finder.findRequiredView(source, 2131690046, "field 'departmentLL'");
    target.departmentLL = finder.castView(view, 2131690046, "field 'departmentLL'");
    view = finder.findRequiredView(source, 2131690115, "field 'viewPager'");
    target.viewPager = finder.castView(view, 2131690115, "field 'viewPager'");
    view = finder.findRequiredView(source, 2131690119, "field 'refreshLayout'");
    target.refreshLayout = finder.castView(view, 2131690119, "field 'refreshLayout'");
    view = finder.findRequiredView(source, 2131690120, "field 'doctorRCV'");
    target.doctorRCV = finder.castView(view, 2131690120, "field 'doctorRCV'");
    view = finder.findRequiredView(source, 2131690117, "field 'doctorTypeIV'");
    target.doctorTypeIV = finder.castView(view, 2131690117, "field 'doctorTypeIV'");
    view = finder.findRequiredView(source, 2131690118, "field 'doctorTypeTV'");
    target.doctorTypeTV = finder.castView(view, 2131690118, "field 'doctorTypeTV'");
  }

  @Override public void unbind(T target) {
    target.adLL = null;
    target.departmentLL = null;
    target.viewPager = null;
    target.refreshLayout = null;
    target.doctorRCV = null;
    target.doctorTypeIV = null;
    target.doctorTypeTV = null;
  }
}
