package com.cards.flash.testez;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by gurkiratsingh on 2/9/16.
 */
public class QuizFlashCard extends FlashCardFlip{

    private Context context;
    private TextView questionTxtView;
    private TrueFalse trueFalse;
    private MultipleChoice multipleChoice;
    private ParseObject parseObject;

    public QuizFlashCard(Context context, ParseObject parseObject){
        super(context);
        this.context = context;
        this.parseObject = parseObject;
        initialize();
    }
    private void initialize(){
        //Question Side
        FrameLayout frameQuestionSide = (FrameLayout) View.inflate(context,
                R.layout.practice_mode_layout, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        frontSide.addView(frameQuestionSide, params);

        questionTxtView = (TextView)frameQuestionSide.findViewById(R.id.textView);
        questionTxtView.setAllCaps(true);

        //Answer Side
        if (parseObject.getBoolean("isTF")){
            trueFalse = new TrueFalse(context, FlashCardEnum.QUIZ_MODE, parseObject.getString("answer"));
            backSide.addView(trueFalse, params);
        }else{
            multipleChoice = new MultipleChoice(context, FlashCardEnum.QUIZ_MODE, parseObject.getString("answer"));
            backSide.addView(multipleChoice, params);
        }
        setAllFields();
    }
    private void setAllFields(){
        questionTxtView.setText(parseObject.getString("question"));

        if (parseObject.getBoolean("isTF")){
            trueFalse.setAnswer(parseObject.getString("answer"));
        }else{
            ArrayList<String> arrayList = (ArrayList<String>)parseObject.get("multi_choice");
            multipleChoice.fillMultiChoices(arrayList);
        }

    }
    private void updateParseObject(ParseObject object){
        parseObject = object;
    }
}
