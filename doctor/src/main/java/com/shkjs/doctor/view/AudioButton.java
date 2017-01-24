package com.shkjs.doctor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raspberry.library.util.DisplayUtils;
import com.shkjs.doctor.R;

/**
 * Created by xiaohu on 2016/10/22.
 * <p>
 * 自定义录音按钮
 */

public class AudioButton extends LinearLayout {

    private ImageView image;
    private TextView textView;

    private Drawable buttonDrawable;
    private String textStr;
    private int textColor;
    private float textSize;

    public AudioButton(Context context) {
        super(context);
        initView(context);
    }

    public AudioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView(context);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray array = this.getContext().obtainStyledAttributes(attrs, R.styleable.AudioButton);
        buttonDrawable = array.getDrawable(R.styleable.AudioButton_button);
        textStr = array.getString(R.styleable.AudioButton_text);
        textColor = array.getColor(R.styleable.AudioButton_textColor, Color.WHITE);
        textSize = array.getDimension(R.styleable.AudioButton_textSize, 12);
        array.recycle();
    }

    private void initView(Context context) {
        image = new ImageView(context);
        textView = new TextView(context);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(DisplayUtils.dip2px(this.getContext(), 9), DisplayUtils.dip2px(this.getContext(), 5),
                DisplayUtils.dip2px(this.getContext(), 7), DisplayUtils.dip2px(this.getContext(), 5));
        image.setLayoutParams(params);
        image.setClickable(false);
        image.setImageDrawable(buttonDrawable);

        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        params.setMargins(0, DisplayUtils.dip2px(this.getContext(), 5), DisplayUtils.dip2px(this.getContext(), 9),
                DisplayUtils.dip2px(this.getContext(), 5));
        textView.setLayoutParams(params);
        textView.setClickable(false);
        textView.setText(textStr);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);

        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT));
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        this.addView(image);
        this.addView(textView);
    }

    public Drawable getButtonDrawable() {
        return buttonDrawable;
    }

    public void setButtonDrawable(Drawable buttonDrawable) {
        this.buttonDrawable = buttonDrawable;
        image.setImageDrawable(buttonDrawable);
    }

    public void setButtonDrawable(int id) {
        image.setImageResource(id);
        this.buttonDrawable = image.getDrawable();
    }

    public String getTextStr() {
        return textStr;
    }

    public void setTextStr(String textStr) {
        this.textStr = textStr;
        textView.setText(textStr);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textView.setTextColor(textColor);
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textView.setTextSize(textSize);
    }
}
