// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SettingSecurityQuestionActivity$$ViewBinder<T extends com.shkjs.patient.activity.SettingSecurityQuestionActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689898, "field 'questionOne'");
    target.questionOne = finder.castView(view, 2131689898, "field 'questionOne'");
    view = finder.findRequiredView(source, 2131689899, "field 'answerOne'");
    target.answerOne = finder.castView(view, 2131689899, "field 'answerOne'");
    view = finder.findRequiredView(source, 2131689900, "field 'questionTwo'");
    target.questionTwo = finder.castView(view, 2131689900, "field 'questionTwo'");
    view = finder.findRequiredView(source, 2131689901, "field 'answerTwo'");
    target.answerTwo = finder.castView(view, 2131689901, "field 'answerTwo'");
    view = finder.findRequiredView(source, 2131689902, "field 'questionThree'");
    target.questionThree = finder.castView(view, 2131689902, "field 'questionThree'");
    view = finder.findRequiredView(source, 2131689903, "field 'answerThree'");
    target.answerThree = finder.castView(view, 2131689903, "field 'answerThree'");
    view = finder.findRequiredView(source, 2131689849, "field 'submitBtn'");
    target.submitBtn = finder.castView(view, 2131689849, "field 'submitBtn'");
  }

  @Override public void unbind(T target) {
    target.questionOne = null;
    target.answerOne = null;
    target.questionTwo = null;
    target.answerTwo = null;
    target.questionThree = null;
    target.answerThree = null;
    target.submitBtn = null;
  }
}
