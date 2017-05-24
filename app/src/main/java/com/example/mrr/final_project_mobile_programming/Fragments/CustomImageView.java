package com.example.mrr.final_project_mobile_programming.Fragments;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


public class CustomImageView extends AppCompatImageView {

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void changeImageSize(int dps) {

        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);

        android.view.ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = pixels;
        layoutParams.height = pixels;
        setLayoutParams(layoutParams);
    }
}
