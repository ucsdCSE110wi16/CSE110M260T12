package com.cards.flash.testez;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gurkiratsingh on 2/1/16.
 */
public class AddFlashCard extends FlashCardFlip {

    private Context context;
    private EditText editText;
    private Button tf_button;
    private Button mc_button;
    private FrameLayout infinityLayout;
    private FlashCardEnum currTab;
    private TrueFalse trueFalse;
    private MultipleChoice multipleChoice;

    public AddFlashCard(Context context) {
        super(context);
        initialize(context);
    }
    public AddFlashCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public AddFlashCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }
    private void initialize(Context context){
        this.context = context;
        currTab = FlashCardEnum.UNSET;
        configureQuestionSide();
        configureAnswerSide();
        configureInfinityLoading();
    }

    private void configureInfinityLoading(){
        infinityLayout = (FrameLayout) View.inflate(context,R.layout.infinity_layout,null);
        InfinityLoading infLoading = (InfinityLoading)infinityLayout.findViewById(R.id.infinity_loading);
        FrameLayout.LayoutParams infParams = new FrameLayout.LayoutParams((MainActivity.screenWidth/2),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        infParams.gravity = Gravity.CENTER;
        infLoading.setLayoutParams(infParams);

        Button cancelButton = (Button)infinityLayout.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyInfLoading();
            }
        });

    }
    private void showInfLoading(){
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams winParams=new WindowManager.LayoutParams(
                MainActivity.screenWidth,
                MainActivity.screenHeight,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        winParams.gravity=Gravity.CENTER|Gravity.CENTER;
        winParams.x=0;
        winParams.y=0;
        windowManager.addView(infinityLayout, winParams);
    }
    private void destroyInfLoading(){
        if (infinityLayout != null) {
            WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            if (infinityLayout.isShown()) {
                windowManager.removeViewImmediate(infinityLayout);
            }
        }
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
        limitChars(editText);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(editText);
                    return true;
                }
                return false;
            }
        });

        frontSide.addView(editText, relParams);

        Button doneButton = new Button(context);
        int widHeight = (int)(super.getLayoutParams().height * 0.15);
        doneButton.setBackground(getResources().getDrawable(R.drawable.circular_button));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(widHeight,
                widHeight);
        params.setMargins(5, 5, 5, 5);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        doneButton.setText("Done");
        doneButton.setPadding(5, 5, 5, 5);
        doneButton.setTextSize(12.f);
        frontSide.addView(doneButton, params);

        doneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String question = editText.getText().toString().trim();
                if (!question.equals("")){

                    if (currTab == FlashCardEnum.UNSET)
                        createAlertDialogSendingToDatabase("Answer");
                    else {
                        ParseObject parseObject = new ParseObject("FlashCards");
                        Boolean isTF = (currTab == FlashCardEnum.MULTI_CHOICE) ? false : true;
                        parseObject.put("question", question);
                        parseObject.put("isTF", isTF);
                        if (isTF) {
                            if (trueFalse.tf_answer.equals("")) {
                                createAlertDialogSendingToDatabase("True/False");
                            } else {
                                parseObject.put("answer", trueFalse.tf_answer);
                                saveParseObject(parseObject);
                            }
                        } else {
                            ArrayList<String> allMultiValues = multipleChoice.getAllValues();
                            if (allMultiValues.size() < 4) {
                                createAlertDialogSendingToDatabase("Multiple choice answer");
                            } else {
                                parseObject.addAll("multi_choice",allMultiValues);
                                parseObject.put("answer", multipleChoice.mult_choice_answer);
                                saveParseObject(parseObject);
                            }
                        }
                    }
                }else{
                    createAlertDialogSendingToDatabase("Question");
                }

            }
        });

    }
    private void saveParseObject(ParseObject parseObject){
        showInfLoading();
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                destroyInfLoading();
                if (e != null) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void limitChars(EditText editT){
        editT.addTextChangedListener(new TextWatcher() {
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
                if (trueFalse.choicesEdited()){
                    createAlertDialogSwitchTab(true);
                }else{
                    trueFalse.changeInterface();
                }

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
                if (multipleChoice.choicesEdited()){
                    createAlertDialogSwitchTab(false);
                }else{
                    multipleChoice.changeInterface();
                }
            }
        });
        backSide.addView(tf_button);

        trueFalse = new TrueFalse();
        multipleChoice = new MultipleChoice();

    }

    private void setTopButtonSettingsOnSelect(Button buttonOn, Button buttonOf){
        buttonOn.setEnabled(true);
        buttonOn.getBackground().setAlpha(190);
        buttonOf.setEnabled(false);
        buttonOf.getBackground().setAlpha(135);
    }

    private void hideKeyboard(EditText tempET){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tempET.getWindowToken(), 0);
    }
    private void showKeyboard(EditText tempET){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        tempET.requestFocus();
        imm.showSoftInput(tempET, InputMethodManager.SHOW_IMPLICIT);
    }
    private void showCorrWrongIndicators(int imgID, View layout){
        int layoutYPos = (int)layout.getY() + layout.getHeight();
        final ImageView checkMark = new ImageView(context);
        checkMark.setImageResource(imgID);
        checkMark.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int)((backSide.getHeight()-layoutYPos)*0.5), (int)((backSide.getHeight()-layoutYPos)*0.5));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        checkMark.setLayoutParams(params);
        int remHeight = backSide.getHeight()-layoutYPos;
        int yPos = (int)(remHeight - ((backSide.getHeight()- layoutYPos) * 0.5))/2;
        checkMark.setY(yPos + layoutYPos);
        backSide.addView(checkMark);

        Animation scale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        scale.setDuration(400);
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // delay of 0.5 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backSide.removeView(checkMark);
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        checkMark.startAnimation(scale);

    }
    private void createAlertDialogSwitchTab(final Boolean fromTF){
        new AlertDialog.Builder(context)
                .setTitle("Delete Data?")
                .setMessage("Your data will be deleted upon choosing a different answer interface. " +
                        "Do you wish to proceed?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        if (fromTF){
                            trueFalse.clearAllValues();
                            trueFalse.changeInterface();
                        } else{
                            multipleChoice.clearAllValues();
                            multipleChoice.changeInterface();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void createAlertDialogSendingToDatabase(String missingVal){
        new AlertDialog.Builder(context)
                .setTitle("Values Missing")
                .setMessage(missingVal + " is missing.")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    class TrueFalse{
        private LinearLayout tf_layout;
        private String tf_answer;

        TrueFalse(){
            createTFLayout();
        }
        private void createTFLayout(){

            int height_avail = getLayoutParams().height - (int) (MainActivity.screenWidth * 0.094);
            tf_answer = new String("");
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

            tf_layout.setY((int)((height_avail - ((2*(MainActivity.screenWidth * 0.12)) + 80)) / 2)
                    );

            backSide.addView(tf_layout, tf_params);

            //layout is initially hidden
            hideLayout();

        }
        private void hideLayout(){
            tf_layout.setVisibility(GONE);
        }
        private void showLayout(){
            tf_layout.setVisibility(VISIBLE);
        }
        private void setAnswer(String answer){
            Boolean ans = answer.trim().equals("true");
            if (ans) tf_answer = "true";
            else tf_answer = "false";

            Button button = (Button)tf_layout.getChildAt((ans)?0:1);
            changeButtonToGreen(button);

        }
        private void clearAllValues(){
            Button button = (Button)tf_layout.getChildAt(tf_answer.equals("true")?0:1);
            changeButtonToDefault(button);
            tf_answer = "";
        }
        private boolean choicesEdited(){
            if (tf_answer.equals("")) return false;
            return true;
        }
        private void changeButtonToGreen(Button button){
            GradientDrawable bgShape = (GradientDrawable)button.getBackground();
            bgShape.setColor(getResources().getColor(R.color.corrGreen));
            bgShape.setStroke(3, getResources().getColor(R.color.strokeGreen));
        }
        private void changeInterface(){
            currTab = FlashCardEnum.MULTI_CHOICE;
            setTopButtonSettingsOnSelect(tf_button, mc_button);
            trueFalse.hideLayout();
            multipleChoice.showLayout();
        }
        private void changeButtonToDefault(Button button){
            String str = button.getText().toString().trim().toLowerCase();
            int colorInt = (str.equals("true"))? getResources().getColor(R.color.mudBlue) :
                    getResources().getColor(R.color.brightBlue);

            GradientDrawable bgShape = (GradientDrawable)button.getBackground();
            bgShape.setColor(colorInt);
            bgShape.setStroke(3,getResources().getColor(R.color.strokeGreen));
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
                    Button button = (Button)v;
                    String buttonStr = button.getText().toString().toLowerCase().trim();
                    tf_answer = buttonStr;
                    changeButtonToGreen(button);

                    Boolean ans = buttonStr.trim().equals("true");
                    Button prevButton = (Button) tf_layout.getChildAt((ans)? 1 : 0);
                    changeButtonToDefault(prevButton);


                    Log.d("DD", tf_answer);
                    /*if (buttonStr.equals(tf_answer.toLowerCase())) {
                        //correct answer TODO
                        showCorrWrongIndicators(R.drawable.checkmark, tf_layout);

                    } else {
                        //wrong answer
                        showCorrWrongIndicators(R.drawable.wrong, tf_layout);
                    }*/
                }
            });
        }
    }
    class MultipleChoice {
        private RadioGroup radioGroup;
        private HashMap<Integer,EditText> editTextMap;
        private int lastCheckRadioButtonId = -1;
        private String mult_choice_answer;

        MultipleChoice(){
            createMultiChoiceLayout();
        }

        private void createMultiChoiceLayout(){

            RelativeLayout relLayout = (RelativeLayout) View.inflate(context,R.layout.multi_choice_layout,null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MainActivity.screenWidth
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            relLayout.setPadding(20, 20 + (int) (MainActivity.screenWidth * 0.094), 20, 20);
            backSide.addView(relLayout, layoutParams);

            radioGroup = (RadioGroup) relLayout.findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    for (int i = 0; i < group.getChildCount(); i++){
                        RadioButton rb = (RadioButton)group.getChildAt(i);
                        if (rb.getId() != checkedId){
                            rb.setTextColor(Color.BLACK);
                        }else{
                            rb.setTextColor(getResources().getColor(R.color.corrGreen));
                            mult_choice_answer = rb.getText().toString().trim();
                        }
                    }
                }
            });
            setEditTextParamsResRadioButton(relLayout);

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
                localEditText.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
                limitChars(localEditText);
                localEditText.setTypeface(null, Typeface.BOLD);
                localEditText.setBackground(null);
                localEditText.setX(radioButton.getTotalPaddingLeft());
                localEditText.setY(padding + ((3 * padding) * i) + (i * radioButton.getTotalPaddingLeft()));
                localEditText.setSingleLine(true);
                localEditText.setVisibility(GONE);
                layout.addView(localEditText);
                localEditText.setOnKeyListener(new OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            EditText tempEdit = (EditText) v;
                            resetSettingsForET(tempEdit, tempEdit.getText().toString().trim());
                            RadioButton rb =(RadioButton)findViewById(lastCheckRadioButtonId);
                            rb.setTextColor(getResources().getColor(R.color.corrGreen));
                            mult_choice_answer = rb.getText().toString().trim();
                            return true;
                        }
                        return false;
                    }
                });

                editTextMap.put(radioButton.getId(), localEditText);

                GestureListener listener = new GestureListener();
                final GestureDetector gestureDetector = new GestureDetector(context,listener);
                gestureDetector.setOnDoubleTapListener(listener);
                radioButton.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        radioButton.onTouchEvent(event);
                        return true;
                    }
                });

            }
        }
        private ArrayList<String> getAllValues(){
            ArrayList<String> answerArray = new ArrayList<>();
            for (int i = 0; i < radioGroup.getChildCount(); i++){
                RadioButton rb = (RadioButton)radioGroup.getChildAt(i);
                String val = rb.getText().toString().trim();
                if (!val.equals("")) answerArray.add(val);
            }
            return answerArray;
        }
        private void showLayout(){
            ((RelativeLayout) radioGroup.getParent()).setVisibility(VISIBLE);
        }
        private void hideLayout(){
            ((RelativeLayout) radioGroup.getParent()).setVisibility(GONE);
        }
        private void clearAllValues(){
            for (int i = 0; i < radioGroup.getChildCount(); i++){
                RadioButton tempRB = (RadioButton)radioGroup.getChildAt(i);
                tempRB.setText("");
                tempRB.setHint("Answer " + (radioGroup.indexOfChild(tempRB) + 1));
                editTextMap.get(tempRB.getId()).setText("");
            }
            radioGroup.clearCheck();
        }
        private boolean choicesEdited(){
            for (int i = 0; i < radioGroup.getChildCount(); i++){
                RadioButton rb = (RadioButton)radioGroup.getChildAt(i);
                if (!rb.getText().toString().trim().equals(""))
                    return true;
            }
            return false;
        }
        private void changeInterface(){
            currTab = FlashCardEnum.TRUE_FALSE;
            setTopButtonSettingsOnSelect(mc_button, tf_button);
            trueFalse.showLayout();
            multipleChoice.hideLayout();
        }
        private void fillMultiChoices(String[] choicesArray){
            for (int i = 0; i < radioGroup.getChildCount(); i++){
                RadioButton tempRB = (RadioButton)radioGroup.getChildAt(i);
                tempRB.setText(choicesArray[i]);
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
                tempET.setVisibility(VISIBLE);
                showKeyboard(tempET);

                return true;
            }

        }
        private void resetSettingsForET(EditText et, String str){
            et.setVisibility(GONE);
            RadioButton prevRadioButton = (RadioButton)radioGroup.findViewById(lastCheckRadioButtonId);
            if (!str.equals("")){
                prevRadioButton.setText(str);
            }else{
                prevRadioButton.setHint("Answer " + (radioGroup.indexOfChild(prevRadioButton) + 1));
            }
            prevRadioButton.setTextColor(Color.BLACK);


            hideKeyboard(et);
        }
    }

}
