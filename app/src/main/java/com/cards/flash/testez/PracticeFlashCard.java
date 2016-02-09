package com.cards.flash.testez;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by gurkiratsingh on 2/8/16.
 */
public class PracticeFlashCard extends FlashCardFlip{
    Context context;
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
        this.context = context;
        FrameLayout frameQuestionSide = (FrameLayout) View.inflate(context, R.layout.practice_mode_layout, null);
        FrameLayout frameAnswerSide = (FrameLayout) View.inflate(context, R.layout.practice_mode_layout, null);

        frontSide.addView(frameQuestionSide);
        backSide.addView(frameAnswerSide);

        questionTxtView = (TextView)frameQuestionSide.findViewById(R.id.textView);
        answerAnswerView = (TextView)frameAnswerSide.findViewById(R.id.textView);

    }
    public void setQuestion(String text){
        questionTxtView.setText(text);
    }

    public void setAnswer(String text){
        answerAnswerView.setText(text);
    }
}
