// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ModifyUserSynopsis$$ViewBinder<T extends com.shkjs.patient.activity.ModifyUserSynopsis> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689806, "field 'editText'");
    target.editText = finder.castView(view, 2131689806, "field 'editText'");
    view = finder.findRequiredView(source, 2131689807, "field 'textView'");
    target.textView = finder.castView(view, 2131689807, "field 'textView'");
  }

  @Override public void unbind(T target) {
    target.editText = null;
    target.textView = null;
  }
}
