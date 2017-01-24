// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SearchDoctorFragment1$$ViewBinder<T extends com.shkjs.patient.fragment.SearchDoctorFragment1> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131690121, "field 'listView'");
    target.listView = finder.castView(view, 2131690121, "field 'listView'");
  }

  @Override public void unbind(T target) {
    target.listView = null;
  }
}
