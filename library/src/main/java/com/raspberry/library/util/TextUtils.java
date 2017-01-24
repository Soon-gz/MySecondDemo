package com.raspberry.library.util;

import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by xiaohu on 2016/10/8.
 */

public class TextUtils {

    /**
     * 获取文本框内容
     *
     * @param editText 文本框
     * @return 文本框内容
     */
    public static String getText(EditText editText) {
        if (null != editText) {
            return editText.getText().toString().trim();
        } else {
            return null;
        }
    }

    /**
     * 获取文本框内容
     *
     * @param textView 文本框
     * @return 文本框内容
     */
    public static String getText(TextView textView) {
        if (null != textView) {
            return textView.getText().toString().trim();
        } else {
            return null;
        }
    }

    /**
     * 文本内容是否为特定内容
     *
     * @param textView 文本框
     * @param str      特定内容
     * @return
     */
    public static boolean equals(TextView textView, String str) {
        if (getText(textView).equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文本内容是否为特定内容
     *
     * @param editText 文本框
     * @param str      特定内容
     * @return
     */
    public static boolean equals(EditText editText, String str) {
        if (getText(editText).equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文本内容是否为特定内容
     *
     * @param editText1 文本框1
     * @param editText2 文本框2
     * @return
     */
    public static boolean equals(EditText editText1, EditText editText2) {
        if (getText(editText1).equals(getText(editText2))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEmpty(EditText editText) {
        return isEmpty(getText(editText));
    }

    public static boolean isEmpty(TextView textView) {
        return isEmpty(getText(textView));
    }

    public static boolean isEmpty(String str) {
        if (null == str || str.isEmpty() || str.equals("null")) {
            return true;
        } else {
            return false;
        }
    }
}
