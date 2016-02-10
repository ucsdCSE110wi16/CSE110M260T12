package com.cards.flash.testez;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.parse.ParseObject;

import java.util.HashMap;

/**
 * Created by gurkiratsingh on 2/5/16.
 */
public class FlashCard{

    //only one of these classes can be instantiated; everything else will be null

    protected static class AddOrEdit extends FrameLayout{
        private AddFlashCard addEditFlashCard;

        public AddOrEdit(Context context){
            super(context);
            addEditFlashCard = new AddFlashCard(context);
            addViewToRoot();
        }
        public void addViewToRoot(){
            this.addView(addEditFlashCard);
        }
        public void removeViewFromRoot(){
            this.removeView(addEditFlashCard);
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

        public void addViewToRoot(){
            this.addView(practiceFC);
        }
        public void removeViewFromRoot(){
            this.removeView(practiceFC);
        }
    }

    protected static class Quiz extends FrameLayout{
        private QuizFlashCard quizFC;

        public Quiz(Context context, ParseObject object){
            super(context);

            quizFC = new QuizFlashCard(context, object);
            addViewToRoot();
        }

        public void setAllFields(){
            quizFC.setAllFields();
        }

        public void updateParseObject(ParseObject object){
            quizFC.updateParseObject(object);
        }
        private void addViewToRoot(){
            this.addView(quizFC);
        }
        public void removeViewFromRoot(){
            this.removeView(quizFC);
        }

    }

}
