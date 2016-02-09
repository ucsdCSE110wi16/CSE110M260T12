package com.cards.flash.testez;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by gurkiratsingh on 2/5/16.
 */
public class FlashCard extends FrameLayout {

    private PracticeFlashCard practiceFC;
    private AddFlashCard addEditFlashCard;
    private FlashCardEnum currMode;
    private Context context;

    public FlashCard(Context context, FlashCardEnum mode) {
        super(context);
        currMode = mode;
        this.context = context;
        setupAppropriateCard();
    }

    public FlashCard(Context context, AttributeSet attrs, FlashCardEnum mode) {
        super(context, attrs);
        currMode = mode;
        this.context = context;
        setupAppropriateCard();
    }

    public FlashCard(Context context, AttributeSet attrs, int defStyleAttr, FlashCardEnum mode) {
        super(context, attrs, defStyleAttr);
        currMode = mode;
        this.context = context;
        setupAppropriateCard();
    }

    private void setupAppropriateCard(){

        if (currMode == FlashCardEnum.EDIT_MODE){
            this.addEditFlashCard = new AddFlashCard(context);
            this.addView(addEditFlashCard);

        }else if (currMode == FlashCardEnum.PRACTICE_MODE){
            practiceFC = new PracticeFlashCard(context);
            this.addView(practiceFC);

        }else if (currMode == FlashCardEnum.QUIZ_MODE){

        }
    }

    public void setQuestion(String question){
        if (currMode == FlashCardEnum.PRACTICE_MODE){
            practiceFC.setQuestion(question);
        }
    }

    public void setAnswer(String answer){
        if (currMode == FlashCardEnum.PRACTICE_MODE){
            practiceFC.setAnswer(answer);
        }
    }
}
