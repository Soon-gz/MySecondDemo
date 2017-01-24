// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class RechargeActivity$$ViewBinder<T extends com.shkjs.patient.activity.RechargeActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689862, "field 'rechargeLL'");
    target.rechargeLL = finder.castView(view, 2131689862, "field 'rechargeLL'");
    view = finder.findRequiredView(source, 2131689874, "field 'resultLL'");
    target.resultLL = finder.castView(view, 2131689874, "field 'resultLL'");
    view = finder.findRequiredView(source, 2131689869, "field 'otherMoneyLL'");
    target.otherMoneyLL = finder.castView(view, 2131689869, "field 'otherMoneyLL'");
    view = finder.findRequiredView(source, 2131689863, "field 'radioGroup'");
    target.radioGroup = finder.castView(view, 2131689863, "field 'radioGroup'");
    view = finder.findRequiredView(source, 2131689865, "field 'moneyFirst'");
    target.moneyFirst = finder.castView(view, 2131689865, "field 'moneyFirst'");
    view = finder.findRequiredView(source, 2131689866, "field 'moneySecond'");
    target.moneySecond = finder.castView(view, 2131689866, "field 'moneySecond'");
    view = finder.findRequiredView(source, 2131689867, "field 'moneyThird'");
    target.moneyThird = finder.castView(view, 2131689867, "field 'moneyThird'");
    view = finder.findRequiredView(source, 2131689868, "field 'moneyOther'");
    target.moneyOther = finder.castView(view, 2131689868, "field 'moneyOther'");
    view = finder.findRequiredView(source, 2131689872, "field 'alipayCB'");
    target.alipayCB = finder.castView(view, 2131689872, "field 'alipayCB'");
    view = finder.findRequiredView(source, 2131689873, "field 'submitBtn'");
    target.submitBtn = finder.castView(view, 2131689873, "field 'submitBtn'");
    view = finder.findRequiredView(source, 2131689876, "field 'setPayPwdBtn'");
    target.setPayPwdBtn = finder.castView(view, 2131689876, "field 'setPayPwdBtn'");
    view = finder.findRequiredView(source, 2131689877, "field 'continueRechargeBtn'");
    target.continueRechargeBtn = finder.castView(view, 2131689877, "field 'continueRechargeBtn'");
    view = finder.findRequiredView(source, 2131689870, "field 'otherMoneyET'");
    target.otherMoneyET = finder.castView(view, 2131689870, "field 'otherMoneyET'");
  }

  @Override public void unbind(T target) {
    target.rechargeLL = null;
    target.resultLL = null;
    target.otherMoneyLL = null;
    target.radioGroup = null;
    target.moneyFirst = null;
    target.moneySecond = null;
    target.moneyThird = null;
    target.moneyOther = null;
    target.alipayCB = null;
    target.submitBtn = null;
    target.setPayPwdBtn = null;
    target.continueRechargeBtn = null;
    target.otherMoneyET = null;
  }
}
