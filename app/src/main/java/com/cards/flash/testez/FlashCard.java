package com.cards.flash.testez;

import android.content.Context;
import android.widget.FrameLayout;

import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by gurkiratsingh on 2/5/16.
 */
public class FlashCard{

    //only one of these classes can be instantiated; everything else will be null

    protected static class AddOrEdit extends FrameLayout{
        private AddEditFlashCard addEditFlashCard;

        public AddOrEdit(Context context, FlashCardEnum mode){
            super(context);
            addEditFlashCard = new AddEditFlashCard(context, mode);
            addViewToRoot();
        }
        private void addViewToRoot(){
            this.addView(addEditFlashCard);
        }
        public void setTrueFalseSettings(String answer){
            addEditFlashCard.setTrueFalseSettings(answer);
        }
        public void setMultiChoiceSettings(String answer, ArrayList<String> choicesArray){
            addEditFlashCard.setMultiChoiceSettings(answer, choicesArray);
        }
        public void setQuestion(String question){
            addEditFlashCard.setQuestion(question);
        }
        public void removeViewFromRoot(){
            this.removeView(addEditFlashCard);
            addEditFlashCard = null;
        }
    }

    protected static class Practice extends FrameLayout{
        private PracticeFlashCard practiceFC;

        public Practice(Context context){
            super(context);
            practiceFC = new PracticeFlashCard(context);
            addViewToRoot();
        }
        public void setQuestion(String question){
            practiceFC.setQuestion(question);
        }

        public void setAnswer(String answer){
            practiceFC.setAnswer(answer);
        }

        private void addViewToRoot(){
            this.addView(practiceFC);
        }
        public void removeViewFromRoot(){
            this.removeView(practiceFC);
            practiceFC = null;
        }
    }

    protected static class Quiz extends FrameLayout{
        private QuizFlashCard quizFC;

        public Quiz(Context context, ParseObject object){
            super(context);

            quizFC = new QuizFlashCard(context, object);
            addViewToRoot();
        }

        private void addViewToRoot(){
            this.addView(quizFC);
        }
        public void removeViewFromRoot(){
            this.removeView(quizFC);
            quizFC = null;
        }

    }

}
