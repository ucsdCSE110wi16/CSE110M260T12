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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

        setParams(MainActivity.screenWidth, (int) (MainActivity.screenWidth - (MainActivity.screenWidth * 0.2)));

        this.addView(frontSide);
        this.addView(backSide);

        backSide.setVisibility(INVISIBLE);

        setAnimations();
    }

    public void setParams(int width, int height){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,height);
        lp.gravity = Gravity.CENTER;
        this.setLayoutParams(lp);
    }

    private void setAnimations(){
        frontSide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateTheCard(R.animator.flash_card_in_right, frontSide,backSide);
            }
        });
        backSide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateTheCard(R.animator.flash_card_in_left,backSide,frontSide);
            }
        });
    }
    private void animateTheCard(int id, final View targetView,final View newView){
        Animator left_in = AnimatorInflater.loadAnimator(context, id);
        left_in.setTarget(targetView);
        AnimatorSet set = new AnimatorSet();
        set.play(left_in);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                targetView.setVisibility(INVISIBLE);
                newView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}
