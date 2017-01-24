// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class RefundActivity$$ViewBinder<T extends com.shkjs.patient.activity.RefundActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689878, "field 'totalTV'");
    target.totalTV = finder.castView(view, 2131689878, "field 'totalTV'");
    view = finder.findRequiredView(source, 2131689880, "field 'deductTV'");
    target.deductTV = finder.castView(view, 2131689880, "field 'deductTV'");
    view = finder.findRequiredView(source, 2131689881, "field 'realTV'");
    target.realTV = finder.castView(view, 2131689881, "field 'realTV'");
    view = finder.findRequiredView(source, 2131689883, "field 'explainTV'");
    target.explainTV = finder.castView(view, 2131689883, "field 'explainTV'");
    view = finder.findRequiredView(source, 2131689879, "field 'problemIV'");
    target.problemIV = finder.castView(view, 2131689879, "field 'problemIV'");
    view = finder.findRequiredView(source, 2131689882, "field 'refundBtn'");
    target.refundBtn = finder.castView(view, 2131689882, "field 'refundBtn'");
  }

  @Override public void unbind(T target) {
    target.totalTV = null;
    target.deductTV = null;
    target.realTV = null;
    target.explainTV = null;
    target.problemIV = null;
    target.refundBtn = null;
  }
}
