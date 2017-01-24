// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class WriteAskQuestionsActivity$$ViewBinder<T extends com.shkjs.patient.activity.WriteAskQuestionsActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131690531, "field 'userNameET'");
    target.userNameET = finder.castView(view, 2131690531, "field 'userNameET'");
    view = finder.findRequiredView(source, 2131689924, "field 'userSexTV'");
    target.userSexTV = finder.castView(view, 2131689924, "field 'userSexTV'");
    view = finder.findRequiredView(source, 2131690533, "field 'userAgeET'");
    target.userAgeET = finder.castView(view, 2131690533, "field 'userAgeET'");
    view = finder.findRequiredView(source, 2131690534, "field 'recordBtn'");
    target.recordBtn = finder.castView(view, 2131690534, "field 'recordBtn'");
    view = finder.findRequiredView(source, 2131690535, "field 'describeET'");
    target.describeET = finder.castView(view, 2131690535, "field 'describeET'");
    view = finder.findRequiredView(source, 2131690536, "field 'describeTV'");
    target.describeTV = finder.castView(view, 2131690536, "field 'describeTV'");
    view = finder.findRequiredView(source, 2131690537, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131690537, "field 'recyclerView'");
    view = finder.findRequiredView(source, 2131689849, "field 'submitBtn'");
    target.submitBtn = finder.castView(view, 2131689849, "field 'submitBtn'");
  }

  @Override public void unbind(T target) {
    target.userNameET = null;
    target.userSexTV = null;
    target.userAgeET = null;
    target.recordBtn = null;
    target.describeET = null;
    target.describeTV = null;
    target.recyclerView = null;
    target.submitBtn = null;
  }
}
