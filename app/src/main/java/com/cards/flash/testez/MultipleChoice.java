package com.cards.flash.testez;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by gurkiratsingh on 2/9/16.
 */
public class MultipleChoice extends LinearLayout{

    private RadioGroup radioGroup;
    private HashMap<Integer,EditText> editTextMap;
    private int lastCheckRadioButtonId = -1;
    private String mult_choice_answer;
    private String curr_answer;
    private Context context;
    private FlashCardEnum currEnum;
    private boolean answerChoosen = false;

    public MultipleChoice(Context context, FlashCardEnum currEnum){
        super(context);
        this.context = context;
        this.currEnum = currEnum;
        createMultiChoiceLayout();
    }

    public MultipleChoice(Context context, FlashCardEnum currEnum, String answer){
        super(context);
        this.context = context;
        this.currEnum = currEnum;
        mult_choice_answer = answer;
        createMultiChoiceLayout();
    }
    private void createMultiChoiceLayout(){

        final RelativeLayout relLayout = (RelativeLayout) View.inflate(context, R.layout.multi_choice_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MainActivity.screenWidth
                , (int) (BaseFunction.getFCHeight() * 0.9));

        relLayout.setPadding(20, 20 + (int) (MainActivity.screenWidth * 0.094), 20, 20);
        this.addView(relLayout, layoutParams);

        radioGroup = (RadioGroup) relLayout.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) group.getChildAt(i);
                    if (rb.getId() != checkedId) {
                        rb.setTextColor(Color.BLACK);
                    } else {
                        rb.setTextColor(context.getResources().getColor(R.color.corrGreen));
                        curr_answer = rb.getText().toString().trim();
                    }
                }

                if (currEnum == FlashCardEnum.QUIZ_MODE){

                    if (mult_choice_answer.equals(curr_answer)) {
                        //correct answer TODO
                        if(answerChoosen == false) {
                            System.out.println("correct add to score");
                            EditCardFragment.tallyScore.increaseScore();
                            answerChoosen = true;
                        }
                        BaseFunction.showCorrWrongIndicators(context, R.drawable.checkmark,
                                relLayout, radioGroup);
                    } else {
                        //wrong answer
                        if(answerChoosen == false) {
                            answerChoosen = true;
                        }
                        BaseFunction.showCorrWrongIndicators(context, R.drawable.wrong,
                                relLayout, radioGroup);
                    }
                }else{
                    setAnswer(curr_answer);
                }
            }
        });

        if (currEnum != FlashCardEnum.QUIZ_MODE)
            setEditTextParamsResRadioButton(relLayout);

        if (currEnum == FlashCardEnum.ADD_MODE)
            //hide layout initially
            hideLayout();

    }
    private void setEditTextParamsResRadioButton(RelativeLayout layout){

        editTextMap = new HashMap<>();
        for (int i = 0; i < radioGroup.getChildCount(); i++){

            final EditText localEditText = new EditText(context);
            int padding = 10;
            final RadioButton radioButton = (RadioButton)radioGroup.getChildAt(i);
            RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(MainActivity.screenWidth -
                    radioButton.getTotalPaddingLeft() - 50, radioButton.getTotalPaddingLeft());
            localEditText.setLayoutParams(editTextParams);
            localEditText.setPadding(0, padding, 0, padding);
            localEditText.setTextSize(16);
            localEditText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            BaseFunction.limitChars(localEditText);
            localEditText.setTypeface(null, Typeface.BOLD);
            localEditText.setBackground(null);
            localEditText.setX(radioButton.getTotalPaddingLeft());
            localEditText.setY(padding + ((3 * padding) * i) + (i * radioButton.getTotalPaddingLeft()));
            localEditText.setSingleLine(true);
            localEditText.setVisibility(View.GONE);
            layout.addView(localEditText);
            localEditText.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        EditText tempEdit = (EditText) v;
                        resetSettingsForET(tempEdit, tempEdit.getText().toString().trim());
                        RadioButton rb =(RadioButton)radioGroup.findViewById(lastCheckRadioButtonId);
                        rb.setTextColor(context.getResources().getColor(R.color.corrGreen));
                        curr_answer = rb.getText().toString().trim();
                        return true;
                    }
                    return false;
                }
            });

            editTextMap.put(radioButton.getId(), localEditText);

            GestureListener listener = new GestureListener();
            final GestureDetector gestureDetector = new GestureDetector(context,listener);
            gestureDetector.setOnDoubleTapListener(listener);
            radioButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    radioButton.onTouchEvent(event);
                    return true;
                }
            });

        }
    }
    public ArrayList<String> getAllValues(){
        ArrayList<String> answerArray = new ArrayList<>();
        for (int i = 0; i < radioGroup.getChildCount(); i++){
            RadioButton rb = (RadioButton)radioGroup.getChildAt(i);
            String val = rb.getText().toString().trim();
            System.out.println("Val is: " + val);
            if (!val.equals("")) answerArray.add(val);
        }
        return answerArray;
    }
    public void showLayout(){
        ((RelativeLayout) radioGroup.getParent()).setVisibility(View.VISIBLE);
    }
    public void hideLayout(){
        ((RelativeLayout) radioGroup.getParent()).setVisibility(View.GONE);
    }
    public void clearAllValues(){
        for (int i = 0; i < radioGroup.getChildCount(); i++){
            RadioButton tempRB = (RadioButton)radioGroup.getChildAt(i);
            tempRB.setText("");
            tempRB.setHint("Answer " + (radioGroup.indexOfChild(tempRB) + 1));
            editTextMap.get(tempRB.getId()).setText("");
        }
        radioGroup.clearCheck();
    }
    public void setAnswerAndCheckedState(String answer, ArrayList<String> choicesArray){
        if (currEnum == FlashCardEnum.EDIT_MODE){
            setAnswer(answer);
            fillMultiChoices(choicesArray);
        }
    }
    public boolean choicesEdited(){
        for (int i = 0; i < radioGroup.getChildCount(); i++){
            RadioButton rb = (RadioButton)radioGroup.getChildAt(i);
            if (!rb.getText().toString().trim().equals(""))
                return true;
        }
        return false;
    }

    public void fillMultiChoices(ArrayList<String> choicesArray){
        choicesArray = randomizeArray(choicesArray);
        for (int i = 0; i < radioGroup.getChildCount(); i++){
            RadioButton tempRB = (RadioButton)radioGroup.getChildAt(i);
            String ans = choicesArray.get(i);
            tempRB.setText(ans);
            if (currEnum == FlashCardEnum.EDIT_MODE){
                if (ans.equals(getAnswer())){
                    tempRB.setTextColor(context.getResources().getColor(R.color.corrGreen));
                    tempRB.setChecked(true);
                }
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            int currRadioID = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton)radioGroup.findViewById(currRadioID);
            EditText tempET = editTextMap.get(radioButton.getId());
            EditText prevET = editTextMap.get(lastCheckRadioButtonId);
            String prevStr = null;

            if (prevET != null){
                prevStr = prevET.getText().toString().trim();
            }
            if (currRadioID != lastCheckRadioButtonId && prevStr != null){
                resetSettingsForET(prevET, prevStr);
            }
            lastCheckRadioButtonId = currRadioID;
            radioButton.setHint("");
            radioButton.setTextColor(Color.WHITE);
            tempET.setVisibility(View.VISIBLE);
            BaseFunction.showKeyboard(context, tempET);

            return true;
        }

    }
    public String getAnswer(){
        return mult_choice_answer;
    }
    public void setAnswer(String answer){
        mult_choice_answer = answer.trim();
    }
    private ArrayList<String> randomizeArray(ArrayList<String> array){

        for (int iters = 0; iters < array.size(); iters++){
            int idx = randInt(0,3);
            String tempStr = array.get(iters);
            array.set(iters, array.get(idx));
            array.set(idx, tempStr);
        }
        return array;
    }

    private static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    private void resetSettingsForET(EditText et, String str){
        et.setVisibility(View.GONE);
        RadioButton prevRadioButton = (RadioButton)radioGroup.findViewById(lastCheckRadioButtonId);
        if (!str.equals("")){
            prevRadioButton.setText(str);
        }else{
            prevRadioButton.setHint("Answer " + (radioGroup.indexOfChild(prevRadioButton) + 1));
        }
        prevRadioButton.setTextColor(Color.BLACK);


        BaseFunction.hideKeyboard(context, et);
    }

}
