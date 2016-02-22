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
public class AddEditFlashCard extends FlashCardFlip {

    private Context context;
    private EditText editText;
    private Button tf_button;
    private Button mc_button;
    private FlashCardEnum currTab;
    private TrueFalse trueFalse;
    private MultipleChoice multipleChoice;
    private FlashCardEnum currMode;

    public AddEditFlashCard(Context context, FlashCardEnum mode) {
        super(context);
        this.currMode = mode;
        this.context = context;
        initialize();
    }

    private void initialize(){
        currTab = FlashCardEnum.UNSET;
        configureQuestionSide();
        configureAnswerSide();
        BaseFunction.configureInfinityLoading(context);
    }


    private void configureQuestionSide() {

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
        BaseFunction.limitChars(editText);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    BaseFunction.hideKeyboard(context, editText);
                    return true;
                }
                return false;
            }
        });

        frontSide.addView(editText, relParams);

        Button doneButton = new Button(context);
        int widHeight = (int) (super.getLayoutParams().height * 0.15);
        doneButton.setBackground(getResources().getDrawable(R.drawable.circular_button));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(widHeight,
                widHeight);
        int margin = 5 + BaseFunction.LISTVIEWMARGIN;
        params.setMargins(margin, margin, margin, margin);

        doneButton.setText("Done");

        doneButton.setX(BaseFunction.getFCWidth() - (4 * margin + widHeight));
        doneButton.setTextSize(12.f);
        frontSide.addView(doneButton, params);

        doneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String question = getQuestion();
                if (!question.equals("")) {

                    if (currTab == FlashCardEnum.UNSET)
                        createAlertDialogSendingToDatabase("Answer");
                    else {
                        ParseObject parseObject = new ParseObject("FlashCards");
                        Boolean isTF = (currTab == FlashCardEnum.MULTI_CHOICE) ? false : true;
                        parseObject.put("question", question);
                        parseObject.put("isTF", isTF);
                        if (isTF) {
                            if (trueFalse.getAnswer().equals("")) {
                                createAlertDialogSendingToDatabase("True/False");
                            } else {
                                parseObject.put("answer", trueFalse.getAnswer());
                                saveParseObject(parseObject);
                            }
                        } else {
                            ArrayList<String> allMultiValues = multipleChoice.getAllValues();
                            if (allMultiValues.size() < 4) {
                                createAlertDialogSendingToDatabase("Multiple choice answer");
                            } else {
                                parseObject.addAll("multi_choice", allMultiValues);
                                parseObject.put("answer", multipleChoice.getAnswer());
                                saveParseObject(parseObject);
                            }
                        }
                    }
                } else {
                    createAlertDialogSendingToDatabase("Question");
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
                if (trueFalse.choicesEdited()) {
                    createAlertDialogSwitchTab(true);
                } else {
                    changeInterfaceOfTrueFalse();
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
                if (multipleChoice.choicesEdited()) {
                    createAlertDialogSwitchTab(false);
                } else {
                    changeInterfaceOfMultiChoice();
                }
            }
        });
        backSide.addView(tf_button);

        createAddBasicView();
    }

    public void setTrueFalseSettings(String answer){
        if (currMode == FlashCardEnum.EDIT_MODE){
            trueFalse.setDefaultAnswerAndChangeButtonColor(answer);
            changeInterfaceOfMultiChoice();
        }
    }
    public void setMultiChoiceSettings(String answer, ArrayList<String> choicesArray){
        if (currMode == FlashCardEnum.EDIT_MODE){
            multipleChoice.setAnswerAndCheckedState(answer, choicesArray);
            changeInterfaceOfTrueFalse();
        }
    }
    public String getQuestion(){
        return editText.getText().toString().trim();
    }
    public void setQuestion(String question){
        editText.setText(question.trim());
    }
    private void createAddBasicView(){
        trueFalse = new TrueFalse(context, currMode);
        multipleChoice = new MultipleChoice(context, currMode);
        backSide.addView(trueFalse);
        backSide.addView(multipleChoice);
    }
    private void setTopButtonSettingsOnSelect(Button buttonOn, Button buttonOf){
        buttonOn.setEnabled(true);
        buttonOn.getBackground().setAlpha(190);
        buttonOf.setEnabled(false);
        buttonOf.getBackground().setAlpha(135);
    }

    private void changeInterfaceOfMultiChoice(){
        currTab = FlashCardEnum.TRUE_FALSE;
        setTopButtonSettingsOnSelect(mc_button, tf_button);
        trueFalse.showLayout();
        multipleChoice.hideLayout();
    }
    private void changeInterfaceOfTrueFalse(){
        currTab = FlashCardEnum.MULTI_CHOICE;
        setTopButtonSettingsOnSelect(tf_button, mc_button);
        trueFalse.hideLayout();
        multipleChoice.showLayout();
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
                            changeInterfaceOfTrueFalse();
                        } else{
                            multipleChoice.clearAllValues();
                            changeInterfaceOfMultiChoice();
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
    private void saveParseObject(ParseObject parseObject){
        BaseFunction.showInfLoading(context);
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                BaseFunction.destroyInfLoading(context);
                if (e != null) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
