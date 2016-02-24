package com.cards.flash.testez;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by gurkiratsingh on 2/8/16.
 */
public class PracticeFlashCard extends FlashCardFlip{

    private TextView questionTxtView;
    private TextView answerAnswerView;

    public PracticeFlashCard(Context context){
        super(context);
        initialize(context);
    }
    public PracticeFlashCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public PracticeFlashCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context){
        FrameLayout frameQuestionSide = (FrameLayout) View.inflate(context, R.layout.practice_mode_layout, null);
        FrameLayout frameAnswerSide = (FrameLayout) View.inflate(context, R.layout.practice_mode_layout, null);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        frontSide.addView(frameQuestionSide,params);
        backSide.addView(frameAnswerSide,params);

        questionTxtView = (TextView)frameQuestionSide.findViewById(R.id.textView);
        questionTxtView.setAllCaps(true);

        answerAnswerView = (TextView)frameAnswerSide.findViewById(R.id.textView);
        answerAnswerView.setTextColor(getResources().getColor(R.color.brightBlue));
        answerAnswerView.setTextSize(40.f);

    }
    public void setQuestion(String text){
        questionTxtView.setText(text);
    }

    public void setAnswer(String text){
        answerAnswerView.setText(text.toUpperCase());
    }
}
