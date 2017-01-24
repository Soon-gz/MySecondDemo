// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class AddMemberActivity$$ViewBinder<T extends com.shkjs.patient.activity.AddMemberActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689618, "field 'addMemberLL'");
    target.addMemberLL = finder.castView(view, 2131689618, "field 'addMemberLL'");
    view = finder.findRequiredView(source, 2131689621, "field 'resultLL'");
    target.resultLL = finder.castView(view, 2131689621, "field 'resultLL'");
    view = finder.findRequiredView(source, 2131689619, "field 'phoneNumET'");
    target.phoneNumET = finder.castView(view, 2131689619, "field 'phoneNumET'");
    view = finder.findRequiredView(source, 2131689620, "field 'submitBtn'");
    target.submitBtn = finder.castView(view, 2131689620, "field 'submitBtn'");
    view = finder.findRequiredView(source, 2131689623, "field 'returnBtn'");
    target.returnBtn = finder.castView(view, 2131689623, "field 'returnBtn'");
    view = finder.findRequiredView(source, 2131689624, "field 'continueBtn'");
    target.continueBtn = finder.castView(view, 2131689624, "field 'continueBtn'");
  }

  @Override public void unbind(T target) {
    target.addMemberLL = null;
    target.resultLL = null;
    target.phoneNumET = null;
    target.submitBtn = null;
    target.returnBtn = null;
    target.continueBtn = null;
  }
}
