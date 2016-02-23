package com.cards.flash.testez;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gurkiratsingh on 2/5/16.
 */
public class FlashCard extends FrameLayout{
    //only one of these classes can be instantiated; everything else will be null
    private View addEditFlashCard;
    private View practiceFC;
    private View quizFC;
    private int currPos;
    private ArrayList<View> views;
    private ParseObject object;
    public FlashCardEnum currMode;

    public FlashCard(Context context, FlashCardEnum mode, ParseObject object){
        super(context);
        views.add(addEditFlashCard);
        views.add(practiceFC);
        views.add(quizFC);

        this.object = object;
        this.currMode = mode;
        configureLayout(mode);
    }
    private void configureLayout(FlashCardEnum mode){
        switch (mode){
            case ADD_MODE:
                currPos = 0;
                currMode = FlashCardEnum.ADD_MODE;
                addEditFlashCard = new AddEditFlashCard(getContext(), FlashCardEnum.ADD_MODE);
                break;
            case PRACTICE_MODE:
                currPos = 1;
                currMode = FlashCardEnum.PRACTICE_MODE;
                practiceFC = new PracticeFlashCard(getContext());
                break;
            case QUIZ_MODE:
                currPos = 2;
                currMode = FlashCardEnum.QUIZ_MODE;
                quizFC = new QuizFlashCard(getContext(), object);
                break;
            default:
                break;
        }
        addViewToRoot();
    }
    private void addViewToRoot(){
        this.addView(views.get(currPos));
    }

    public void changeMode(FlashCardEnum mode){
        removeViewFromRoot(views.get(currPos));
        configureLayout(mode);
    }

    public void setTrueFalseSettings(String answer){
        ((AddEditFlashCard) addEditFlashCard).setTrueFalseSettings(answer);
    }

    public void setMultiChoiceSettings(String answer, ArrayList<String> choicesArray){
        ((AddEditFlashCard) addEditFlashCard).setMultiChoiceSettings(answer, choicesArray);
    }

    public void setQuestion(String question){
        if (currPos == 0)
            ((AddEditFlashCard) addEditFlashCard).setQuestion(question);
        else if (currPos == 1)
            ((PracticeFlashCard) practiceFC).setQuestion(question);
    }

    public void setAnswer(String answer){
        ((PracticeFlashCard) practiceFC).setAnswer(answer);
    }

    public void removeViewFromRoot(View view){
        this.removeView(view);
        view = null;
    }

}
