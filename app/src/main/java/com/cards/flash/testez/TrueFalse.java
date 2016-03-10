package com.cards.flash.testez;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by gurkiratsingh on 2/9/16.
 */
public class TrueFalse extends LinearLayout{
    private LinearLayout tf_layout;
    private boolean answerChoosen = false;
    private String tf_answer;
    private Context context;
    private FlashCardEnum currEnum;

    public TrueFalse(Context context, FlashCardEnum currEnum){
        super(context);
        this.context = context;
        this.currEnum = currEnum;
        createTFLayout();
    }

    public TrueFalse(Context context, FlashCardEnum currEnum, String answer){
        super(context);
        this.context = context;
        this.currEnum = currEnum;
        setAnswer(answer);
        createTFLayout();
    }

    private void createTFLayout(){

        this.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout.LayoutParams tf_params = new RelativeLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tf_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        this.setLayoutParams(tf_params);

        this.setY(BaseFunction.getYPosTF());

        tf_answer = new String("");
        tf_layout = new LinearLayout(context);
        tf_layout.setOrientation(LinearLayout.VERTICAL);

        Button trueButton = new Button(context);
        LinearLayout.LayoutParams t_button_params = new LinearLayout.LayoutParams((int)(MainActivity.screenWidth * 0.5)
                , (int)(MainActivity.screenWidth * 0.12));
        t_button_params.gravity = Gravity.CENTER_HORIZONTAL;
        t_button_params.setMargins(0, 0, 0, 80);
        trueButton.setLayoutParams(t_button_params);
        trueButton.setBackground(context.getResources().getDrawable(R.drawable.truebutton));
        trueButton.setText("TRUE");
        setButtonConfig(trueButton);
        tf_layout.addView(trueButton);

        Button falseButton = new Button(context);
        LinearLayout.LayoutParams f_button_params = new LinearLayout.LayoutParams((int)(MainActivity.screenWidth * 0.5)
                , (int)(MainActivity.screenWidth * 0.12));
        falseButton.setLayoutParams(f_button_params);
        f_button_params.gravity = Gravity.CENTER_HORIZONTAL;
        falseButton.setBackground(context.getResources().getDrawable(R.drawable.falsebutton));
        falseButton.setText("FALSE");
        setButtonConfig(falseButton);
        tf_layout.addView(falseButton);

        this.addView(tf_layout);

        if (currEnum == FlashCardEnum.ADD_MODE)
            //layout is initially hidden
            hideLayout();

    }
    public void hideLayout(){
        tf_layout.setVisibility(View.GONE);
    }
    public void showLayout(){
        tf_layout.setVisibility(View.VISIBLE);
    }
    public void setDefaultAnswerAndChangeButtonColor(String answer){
        if (currEnum == FlashCardEnum.EDIT_MODE){
            Boolean ans = answer.trim().equals("true");
            if (ans) tf_answer = "true";
            else tf_answer = "false";

            Button button = (Button)tf_layout.getChildAt((ans)?0:1);
            changeButtonToGreen(button);
        }
    }
    public void setAnswer(String answer){
        tf_answer = answer.trim();
    }
    public String getAnswer(){
        return tf_answer;
    }
    public void clearAllValues(){
        Button button = (Button)tf_layout.getChildAt(tf_answer.equals("true")?0:1);
        changeButtonToDefault(button);
        tf_answer = "";
    }
    public boolean choicesEdited(){
        if (tf_answer.equals("")) return false;
        return true;
    }
    public void changeButtonToGreen(Button button){
        GradientDrawable bgShape = (GradientDrawable)button.getBackground();
        bgShape.setColor(context.getResources().getColor(R.color.corrGreen));
        bgShape.setStroke(3, context.getResources().getColor(R.color.strokeGreen));
    }

    public void changeButtonToDefault(Button button){
        String str = button.getText().toString().trim().toLowerCase();
        int colorInt = (str.equals("true"))? context.getResources().getColor(R.color.mudBlue) :
                context.getResources().getColor(R.color.brightBlue);

        GradientDrawable bgShape = (GradientDrawable)button.getBackground();
        bgShape.setColor(colorInt);
        bgShape.setStroke(3, context.getResources().getColor(R.color.strokeGreen));
    }

    private void setButtonConfig(final Button button){
        button.setTextColor(Color.WHITE);
        button.setTypeface(null, Typeface.BOLD);
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        button.setTextSize(20);
        button.setPadding(10, 10, 10, 10);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //change color of the buttons
                Button button = (Button)v;
                String buttonStr = button.getText().toString().toLowerCase().trim();
                changeButtonToGreen(button);

                Boolean ans = buttonStr.trim().equals("true");
                Button prevButton = (Button) tf_layout.getChildAt((ans)? 1 : 0);
                changeButtonToDefault(prevButton);

                if (currEnum == FlashCardEnum.QUIZ_MODE){
                    if (buttonStr.equals(tf_answer.toLowerCase())) {
                        //correct answer TODO
                        if(answerChoosen == false) {
                            EditCardFragment.tallyScore.increaseScore();
                            answerChoosen = true;
                        }
                        BaseFunction.showCorrWrongIndicators(context, R.drawable.checkmark, tf_layout);

                    } else {
                        //wrong answer
                        if(answerChoosen == false) {
                            answerChoosen = true;
                        }
                        BaseFunction.showCorrWrongIndicators(context, R.drawable.wrong, tf_layout);
                    }
                }else{
                    setAnswer(buttonStr);
                }
            }
        });
    }
}
