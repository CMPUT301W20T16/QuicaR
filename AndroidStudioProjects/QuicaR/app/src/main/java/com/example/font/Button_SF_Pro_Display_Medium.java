package com.example.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;

public class Button_SF_Pro_Display_Medium extends AppCompatButton {

    public Button_SF_Pro_Display_Medium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Button_SF_Pro_Display_Medium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Button_SF_Pro_Display_Medium(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/SF-Pro-Display-Medium.otf");
            setTypeface(tf);
        }
    }
}

