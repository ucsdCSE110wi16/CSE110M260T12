package com.cards.flash.testez;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.AbsoluteLayout.LayoutParams;


/**
 * Created by gurkiratsingh on 2/1/16.
 */

public class FlashCardFlip extends FrameLayout{

    private Context context;
    protected RelativeLayout frontSide;
    protected RelativeLayout backSide;

    public FlashCardFlip(Context context){
        super(context);
        initializeCard(context);
    }

    public FlashCardFlip(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeCard(context);
    }

    public FlashCardFlip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeCard(context);
    }

    private void initializeCard(Context context){

        this.context = context;
        frontSide = (RelativeLayout) View.inflate(context,R.layout.card_question_side,null);
        backSide = (RelativeLayout) View.inflate(context, R.layout.card_answer_side, null);

        setParams(BaseFunction.getFCWidth(), BaseFunction.getFCHeight());

        this.addView(frontSide);
        this.addView(backSide);

        backSide.setVisibility(INVISIBLE);

       // setAnimations();
    }

    public RelativeLayout getFrontSide(){
     return frontSide;
    }
    public RelativeLayout getBackSide(){
        return backSide;
    }
    public void setParams(int width, int height){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,height);
        lp.gravity = Gravity.CENTER;
        this.setLayoutParams(lp);
    }

//    private void setAnimations(){
//        GestureListener listener = new GestureListener();
//        final GestureDetector gestureDetector = new GestureDetector(context,listener);
//
//        frontSide.setOnTouchListener(new OnTouchListener() {
//            @Override
//
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
//                return true;
//
//            }
//        });
//        backSide.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });
//
//    }

//    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
//        static final int SWIPE_MIN_DISTANCE = 120;
//        static final int SWIPE_THRESHOLD_VELOCITY = 200;
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//
//            if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
//                return false;
//            }
//            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
//                //right to left
//                animateTheCard(R.animator.flash_card_in_right, frontSide,backSide);
//            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
//                //left to right
//                animateTheCard(R.animator.flash_card_in_left,backSide,frontSide);
//            }
//            return super.onFling(e1, e2, velocityX, velocityY);
//
//        }
//    }
//    private void animateTheCard(int id, final View targetView,final View newView){
//        Animator left_in = AnimatorInflater.loadAnimator(context, id);
//        left_in.setTarget(targetView);
//        AnimatorSet set = new AnimatorSet();
//        set.play(left_in);
//        set.start();
//        set.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                targetView.setVisibility(INVISIBLE);
//                newView.setVisibility(VISIBLE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//    }

}
