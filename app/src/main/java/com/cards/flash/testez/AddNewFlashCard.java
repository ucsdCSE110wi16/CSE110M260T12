package com.cards.flash.testez;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;

/**
 * Created by gurkiratsingh on 2/1/16.
 */
public class AddNewFlashCard extends FlashCardFlip {

    private String question;
    private String tf_answer;
    private String[] mult_choice_array;
    private int mult_choice_answer;
    private Context context;
    private EditText editText;
    private Button tf_button;
    private Button mc_button;
    private LinearLayout tf_layout;

    public AddNewFlashCard(Context context) {
        super(context);
        initialize(context);
    }
    public AddNewFlashCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public AddNewFlashCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }
    private void initialize(Context context){
        this.context = context;

        configureQuestionSide();
        configureAnswerSide();
    }

    private void configureQuestionSide(){

        editText = new EditText(context);
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        relParams.addRule(RelativeLayout.CENTER_HORIZONTAL | RelativeLayout.CENTER_VERTICAL);
        editText.setLayoutParams(relParams);
        editText.setBackgroundColor(Color.WHITE);
        editText.setPadding(50, 0, 50, 0);
        editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        editText.setTextSize(22);
        editText.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        editText.setTypeface(null, Typeface.BOLD);
        editText.setHint("Add Question");
        editText.setMaxHeight(this.getLayoutParams().height - 340);
        editText.setMovementMethod(null);

        editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                for (int i = s.length(); i > 0; i--) {
                    if (s.subSequence(i - 1, i).toString().equals("\n"))
                        s.replace(i - 1, i, "");

                }
            }
        });
        frontSide.addView(editText,relParams);
    }

    public String getQuestion(){
        return editText.getText().toString();
    }

    public String getTF_answer(){
        return tf_answer;
    }

    public int getMult_choice_answer(){
        return mult_choice_answer;
    }

    private void createTFLayout(){

        int height_avail = super.getLayoutParams().height - (int) (MainActivity.screenWidth * 0.094);

        tf_layout = new LinearLayout(context);
        tf_layout.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout.LayoutParams tf_params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tf_params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        Button trueButton = new Button(context);
        LinearLayout.LayoutParams t_button_params = new LinearLayout.LayoutParams((int)(MainActivity.screenWidth * 0.5)
                , (int)(MainActivity.screenWidth * 0.12));
        t_button_params.setMargins(0, 0, 0, 80);
        trueButton.setLayoutParams(t_button_params);
        trueButton.setBackground(getResources().getDrawable(R.drawable.truebutton));
        trueButton.setText("TRUE");
        setButtonConfig(trueButton);
        tf_layout.addView(trueButton);

        Button falseButton = new Button(context);
        LinearLayout.LayoutParams f_button_params = new LinearLayout.LayoutParams((int)(MainActivity.screenWidth * 0.5)
                , (int)(MainActivity.screenWidth * 0.12));
        falseButton.setLayoutParams(f_button_params);
        falseButton.setBackground(getResources().getDrawable(R.drawable.falsebutton));
        falseButton.setText("FALSE");
        setButtonConfig(falseButton);

        tf_layout.addView(falseButton);
        tf_layout.setY((height_avail - (int) ((MainActivity.screenWidth * 0.12) + (MainActivity.screenWidth * 0.094)
                + 80)) / 2);

        backSide.addView(tf_layout, tf_params);

    }
    private void setButtonConfig(final Button button){
        button.setTextColor(Color.WHITE);
        button.setTypeface(null, Typeface.BOLD);
        button.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        button.setTextSize(20);
        button.setPadding(10, 10, 10, 10);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = button.getText().toString().toLowerCase();
                if (userAnswer == tf_answer.toLowerCase()){
                    //correct answer TODO
                }else{
                    //wrong answer
                }
            }
        });
    }
    private void configureAnswerSide() {

        RelativeLayout.LayoutParams top_buttons_params = new RelativeLayout.LayoutParams(
                MainActivity.screenWidth/2, (int) (MainActivity.screenWidth * 0.094));
        mc_button = new Button(context);
        mc_button.setText("Multiple Choice");
        mc_button.setTextColor(Color.WHITE);
        mc_button.setBackgroundColor(Color.parseColor("#063aa5"));
        mc_button.setTextSize(13);
        mc_button.getBackground().setAlpha(190);
        mc_button.setLayoutParams(top_buttons_params);
        mc_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mc_button.setEnabled(false);
                mc_button.getBackground().setAlpha(135);
                tf_button.setEnabled(true);
                tf_button.getBackground().setAlpha(190);

            }
        });
        backSide.addView(mc_button);

        tf_button = new Button(context);
        tf_button.setText("True/False");
        tf_button.setTextColor(Color.WHITE);
        tf_button.setBackgroundColor(Color.parseColor("#063aa5"));
        tf_button.setTextSize(13);
        tf_button.getBackground().setAlpha(190);
        tf_button.setLayoutParams(top_buttons_params);
        tf_button.setX(MainActivity.screenWidth / 2);
        tf_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mc_button.setEnabled(true);
                mc_button.getBackground().setAlpha(190);
                tf_button.setEnabled(false);
                tf_button.getBackground().setAlpha(135);
            }
        });
        backSide.addView(tf_button);

        createTFLayout();
    }
}
