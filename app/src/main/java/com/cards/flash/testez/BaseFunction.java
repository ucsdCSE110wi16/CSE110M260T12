package com.cards.flash.testez;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

/**
 * Created by gurkiratsingh on 2/9/16.
 */
public final class BaseFunction {

    public static FrameLayout infinityLayout;
    public static int LISTVIEWMARGIN = 8;

    public static void hideKeyboard(Context context, View tempET){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tempET.getWindowToken(), 0);
    }
    public static void showKeyboard(Context context, View tempET){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        tempET.requestFocus();
        imm.showSoftInput(tempET, InputMethodManager.SHOW_IMPLICIT);
    }
    public static void limitChars(EditText editT){
        editT.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                for (int i = s.length(); i > 0; i--) {
                    if (s.subSequence(i - 1, i).toString().equals("\n"))
                        s.replace(i - 1, i, "");

                }
            }
        });
    }
    public static  void showCorrWrongIndicators(Context context, int imgID, final LinearLayout layout){

        int layoutYPos = BaseFunction.getYPosTF() + layout.getHeight();
        int heightRem = getFCHeight()-layoutYPos;
        int imageWH = (int)((heightRem)*0.4);
        int pos = (heightRem - imageWH)/2;

        final LinearLayout frameLayout = (LinearLayout) View.inflate(context,R.layout.indicator_layout,null);
        LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(layout.getWidth(),
                heightRem);
        frameLayout.setLayoutParams(frameParams);

        ImageView checkMark = (ImageView)frameLayout.findViewById(R.id.imageView);
        checkMark.setImageResource(imgID);
        checkMark.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWH, imageWH);
        params.setMargins(0,pos, 0, 0);
        params.gravity = Gravity.CENTER;
        checkMark.setLayoutParams(params);

        layout.addView(frameLayout);
        setStartAnimation(layout, frameLayout);
    }

    public static void setStartAnimation(final ViewGroup baseLayout, final View viewToAnimate){
        Animation scale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        scale.setDuration(400);
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // delay of 0.5 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        baseLayout.removeView(viewToAnimate);
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        viewToAnimate.startAnimation(scale);
    }
    public static void showCorrWrongIndicators(Context context, int imgID,RelativeLayout relLayout,
                                               RadioGroup radioGroup){
        int imageWH = 120;

        ImageView checkMark = new ImageView(context);
        checkMark.setImageResource(imgID);
        checkMark.setScaleType(ImageView.ScaleType.FIT_CENTER);
        relLayout.addView(checkMark);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageWH, imageWH);
        params.addRule(RelativeLayout.BELOW, radioGroup.getId());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL | RelativeLayout.ALIGN_PARENT_BOTTOM);
        checkMark.setLayoutParams(params);
        setStartAnimation(relLayout, checkMark);
    }

    public static void configureInfinityLoading(final Context context){
        if (infinityLayout == null){
            infinityLayout = (FrameLayout) View.inflate(context,R.layout.infinity_layout,null);
            final InfinityLoading infLoading = (InfinityLoading)infinityLayout.findViewById(R.id.infinity_loading);
            FrameLayout.LayoutParams infParams = new FrameLayout.LayoutParams((MainActivity.screenWidth/2),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            infParams.gravity = Gravity.CENTER;
            infLoading.setLayoutParams(infParams);
            Button cancelButton = (Button)infinityLayout.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseFunction.destroyInfLoading(context);
                }
            });
        }

    }
    public static void showInfLoading(Context context){
        configureInfinityLoading(context);
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams winParams=new WindowManager.LayoutParams(
                MainActivity.screenWidth,
                MainActivity.screenHeight,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        winParams.gravity= Gravity.CENTER|Gravity.CENTER;
        winParams.x=0;
        winParams.y=0;
        windowManager.addView(infinityLayout, winParams);
    }
    public static void destroyInfLoading(Context context){
        if (infinityLayout != null) {
            WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            if (infinityLayout.isShown()) {
                windowManager.removeViewImmediate(infinityLayout);
            }
        }
    }
    public static int getFCHeight(){
        return (int) (MainActivity.screenWidth - (MainActivity.screenWidth * 0.2));
    }
    public static int getFCWidth(){
        return MainActivity.screenWidth - (2 * BaseFunction.LISTVIEWMARGIN);
    }
    public static int getYPosTF(){
        int height_avail = (BaseFunction.getFCHeight()) - (int) (MainActivity.screenWidth * 0.094);
        int yPos = (int) ((height_avail - ((2 * (MainActivity.screenWidth * 0.12)) + 80)) / 2);
        return yPos;
    }
}
