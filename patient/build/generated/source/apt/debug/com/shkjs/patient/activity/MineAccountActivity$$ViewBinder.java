// Generated code from Butter Knife. Do not modify!
package com.shkjs.patient.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MineAccountActivity$$ViewBinder<T extends com.shkjs.patient.activity.MineAccountActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689785, "field 'abalanceLL'");
    target.abalanceLL = finder.castView(view, 2131689785, "field 'abalanceLL'");
    view = finder.findRequiredView(source, 2131689786, "field 'balanceTV'");
    target.balanceTV = finder.castView(view, 2131689786, "field 'balanceTV'");
    view = finder.findRequiredView(source, 2131689787, "field 'alipayLL'");
    target.alipayLL = finder.castView(view, 2131689787, "field 'alipayLL'");
    view = finder.findRequiredView(source, 2131689788, "field 'alipayTV'");
    target.alipayTV = finder.castView(view, 2131689788, "field 'alipayTV'");
    view = finder.findRequiredView(source, 2131689789, "field 'homeGroupLL'");
    target.homeGroupLL = finder.castView(view, 2131689789, "field 'homeGroupLL'");
    view = finder.findRequiredView(source, 2131689791, "field 'mineHomeGroupLL'");
    target.mineHomeGroupLL = finder.castView(view, 2131689791, "field 'mineHomeGroupLL'");
    view = finder.findRequiredView(source, 2131689790, "field 'homeGroupTV'");
    target.homeGroupTV = finder.castView(view, 2131689790, "field 'homeGroupTV'");
    view = finder.findRequiredView(source, 2131689793, "field 'rechargeBtn'");
    target.rechargeBtn = finder.castView(view, 2131689793, "field 'rechargeBtn'");
  }

  @Override public void unbind(T target) {
    target.abalanceLL = null;
    target.balanceTV = null;
    target.alipayLL = null;
    target.alipayTV = null;
    target.homeGroupLL = null;
    target.mineHomeGroupLL = null;
    target.homeGroupTV = null;
    target.rechargeBtn = null;
  }
}
