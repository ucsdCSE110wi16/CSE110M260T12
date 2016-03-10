package com.cards.flash.testez;

/**
 * Created by Vincent on 2/11/2016.
 */

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditCardFragment extends ListFragment {


    private ImageAdapter imAdapter;
    private ListView listView;
    private Button quizButton, practiceButton, inviteButton, scoresButton;
    static boolean hasTakenQuiz = false;
    public static TallyScore tallyScore;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private  final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        tallyScore = new TallyScore();
        imAdapter = new ImageAdapter(getActivity());
        this.setListAdapter(imAdapter);

        //Default is practice mode
        if(MainActivity.cateList.size() != 0){
            imAdapter.cardsQuery(FlashCardEnum.PRACTICE_MODE);
        }



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            RelativeLayout rootView = (RelativeLayout)inflater.inflate(R.layout.fragment_main, container, false);
            LinearLayout linearLayout = (LinearLayout)rootView.findViewById(R.id.linearLayout);
            TypedValue tv = new TypedValue();
            int actionBarHeight = R.integer.action_bar_height;
            if (getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MainActivity.screenWidth,
                    actionBarHeight);
            linearLayout.setLayoutParams(params);

            quizButton = (Button) rootView.findViewById(R.id.quiz_button);
            practiceButton = (Button) rootView.findViewById(R.id.practice_button);
            inviteButton = (Button) rootView.findViewById(R.id.invite_button);
            String cateName = MainActivity.categories.get(NavigationDrawerFragment.getCurrentSelectedPos());
            inviteButton.setTag(cateName);
            scoresButton = (Button) rootView.findViewById(R.id.scores_button);

            setUpButtons();
            listView = (ListView)rootView.findViewById(android.R.id.list);


        final GestureDetector gestureDetector = new GestureDetector(new FlipDetector());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        listView.setOnTouchListener(gestureListener);
        return rootView;
    }

    // set on click listeners for buttons
    private void setUpButtons(){

        inviteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShareActivity.class);
                startActivity(intent);

            }
        });

        scoresButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BaseFunction.hideKeyboard(getContext(), v);
                if (hasTakenQuiz) {
                    hasTakenQuiz = false;
                    updateScores();
                }
                Intent intent = new Intent(getContext(), ScoreBoard.class);
                intent.putExtra("cat", MainActivity.cateList.get(NavigationDrawerFragment
                        .getCurrentSelectedPos()).getObjectId());
                startActivity(intent);
            }
        });
        quizButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imAdapter.cardsQuery(FlashCardEnum.QUIZ_MODE);
                hasTakenQuiz = true;
            }
        });

        practiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imAdapter.cardsQuery(FlashCardEnum.PRACTICE_MODE);
            }
        });


    }
    public void updateScores(){

        final ParseObject catObject = MainActivity.cateList.get(NavigationDrawerFragment.getCurrentSelectedPos());
        final ParseRelation<ParseObject> relation = catObject.getRelation("userscores");

        ParseQuery<ParseObject> query = relation.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        System.out.println("updating Scores");

        // Check if we have a score for the user already
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject == null) {
                    System.out.println("null score");
                    ParseObject userscore = new ParseObject("Scores");
                    userscore.put("score", tallyScore.getScore());
                    userscore.put("user", ParseUser.getCurrentUser());
                    userscore.put("attempts", 0);
                    saveScoreObject(userscore);
                    tallyScore.resetScore();
                } else {
                     //What to do if you retake quiz
                    try {
                        parseObject.fetchIfNeeded();
                        if(parseObject.getInt("score") < tallyScore.getScore()) {
                            parseObject.put("score", tallyScore.getScore());
                            System.out.println("new attemptscore");
                            Toast.makeText(getContext(), "Score Updated", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        parseObject.put("attempts", parseObject.getInt("attempts") + 1);
                        parseObject.saveInBackground();
                        tallyScore.resetScore();
                    }catch(ParseException exc){exc.printStackTrace();}
                }
            }
        });
    }
    public void saveScoreObject(final ParseObject parseObject){

              parseObject.saveInBackground(new SaveCallback() {
                  @Override
                  public void done(ParseException e) {

                      if (e == null) {
                          ParseObject category = MainActivity.cateList.get(NavigationDrawerFragment.
                                  getCurrentSelectedPos());

                          ParseRelation<ParseObject> relation = category.getRelation("userscores");
                          relation.add(parseObject);

                          category.saveInBackground(new SaveCallback() {
                              @Override
                              public void done(ParseException e) {
                                  if (e == null) {
                                      Toast.makeText(getContext(), "Score Updated", Toast.LENGTH_SHORT)
                                              .show();
                                  } else {
                                      Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                  }
                              }
                          });
                      }

                  }
              });
    }
    public void addCard(){
        imAdapter.addCard();
        imAdapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(0);
    }
    public void editCard(){
        if (listView.getChildCount() > 0){
            imAdapter.editCard();
            listView.smoothScrollToPosition(0);
        }else{
            Toast.makeText(getActivity(), "No cards to edit.", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt("position"));
    }

    class FlipDetector extends android.view.GestureDetector.SimpleOnGestureListener{

        static final int SWIPE_MIN_DISTANCE = 120;
        static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int pos = getListView().pointToPosition((int) e1.getX(), (int) e1.getY());
            if(pos < 0)
                return false;
            FlashCard v = (FlashCard) getListView().getAdapter().getItem(pos);
            FlashCardFlip c ;
            switch(v.getCurrMode()){
                case ADD_MODE: c = (FlashCardFlip) v.getCardType(FlashCardEnum.ADD_MODE);
                    break;
                case EDIT_MODE: c = (FlashCardFlip) v.getCardType(FlashCardEnum.EDIT_MODE);
                    break;
                case PRACTICE_MODE:c = (FlashCardFlip) v.getCardType(FlashCardEnum.PRACTICE_MODE);
                    break;
                case QUIZ_MODE: c = (FlashCardFlip) v.getCardType(FlashCardEnum.QUIZ_MODE);
                    break;
                default:
                    c = new FlashCardFlip(getContext());

            }

            if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                return false;
            }
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                //right to left
                animateTheCard(R.animator.flash_card_in_right, c.getFrontSide(),c.getBackSide());
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                //left to right
                animateTheCard(R.animator.flash_card_in_left, c.getBackSide(), c.getFrontSide());
            }
            return super.onFling(e1, e2, velocityX, velocityY);

        }

        private void animateTheCard(int id, final View targetView,final View newView){
            Animator left_in = AnimatorInflater.loadAnimator(getContext(), id);
            left_in.setTarget(targetView);
            AnimatorSet set = new AnimatorSet();
            set.play(left_in);
            set.start();
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    targetView.setVisibility(View.INVISIBLE);
                    newView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    class ImageAdapter extends BaseAdapter {
        private ArrayList<FlashCard> cardsList = new ArrayList<>();

        public ImageAdapter(Context c) {

        }

        public void addCard(){
            cardsList.add(0, new FlashCard(getContext(), FlashCardEnum.ADD_MODE, null));
            tallyScore.addNewCard();
        }

        public void removeIncompletes(){
            System.out.println("BEINGS");
            System.out.println(cardsList);
            System.out.println(tallyScore.getIncompleteCards());
            for(int i =0 ; i < tallyScore.getIncompleteCards(); i++)
                cardsList.remove(0);
            tallyScore.resetCardCount();
        }

        public void editCard(){
            cardsQuery(FlashCardEnum.EDIT_MODE);
        }

        private List<ParseObject> cardsQuery(final FlashCardEnum cardMode){
            removeIncompletes();
            BaseFunction.showInfLoading(getActivity());

            final ParseObject parseObject = MainActivity.cateList.get(NavigationDrawerFragment.getCurrentSelectedPos());
            ParseRelation<ParseObject> relation = parseObject.getRelation("flashcards");
            ParseQuery query = relation.getQuery();
            query.addDescendingOrder("updatedAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> cardsObjects, ParseException e) {
                    if (e == null) {
                        if (cardMode == FlashCardEnum.EDIT_MODE) {
                            for (int i = 0; i < cardsList.size(); i++) {
                                ParseObject databaseObject = cardsObjects.get(i);

                                FlashCard card = cardsList.get(i);
                                card.changeMode(FlashCardEnum.EDIT_MODE, databaseObject);
                                card.setQuestion(databaseObject.getString("question"));

                                if (databaseObject.getBoolean("isTF")) {
                                    card.setTrueFalseSettings(databaseObject.getString("answer"));
                                } else {
                                    ArrayList<String> list;
                                    list = (ArrayList<String>) databaseObject.get("multi_choice");
                                    card.setMultiChoiceSettings(databaseObject.getString("answer"), list);
                                }
                            }
                            notifyDataSetChanged();
                        } else if (cardMode == FlashCardEnum.PRACTICE_MODE) {
                            if (cardsList.size() == 0) {
                                for (ParseObject object : cardsObjects) {
                                    FlashCard card = new FlashCard(getContext(), FlashCardEnum.PRACTICE_MODE, null);
                                    card.setQuestion(object.getString("question"));
                                    card.setAnswer(object.getString("answer"));
                                    cardsList.add(card);
                                }
                            } else {
                                for (int i = 0; i < cardsList.size(); i++) {
                                    FlashCard card = cardsList.get(i);
                                    card.changeMode(FlashCardEnum.PRACTICE_MODE);

                                    ParseObject databaseObject = cardsObjects.get(i);
                                    card.setQuestion(databaseObject.getString("question"));
                                    card.setAnswer(databaseObject.getString("answer"));
                                }
                            }
                            notifyDataSetChanged();
                        } else if (cardMode == FlashCardEnum.QUIZ_MODE) {

                            for (int i = 0; i < cardsList.size(); i++) {
                                ParseObject databaseObject = cardsObjects.get(i);

                                FlashCard card = cardsList.get(i);
                                card.changeMode(FlashCardEnum.QUIZ_MODE, databaseObject);

                            }
                            notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(getContext(), "Failed to fetch cards", Toast.LENGTH_LONG).show();
                    }
                    BaseFunction.destroyInfLoading(getActivity());
                }
            });
            return null;
        }

        @Override
        public int getCount() {
        return cardsList.size();
    }

        @Override
        public Object getItem(int position) {
            return cardsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return cardsList.get(position);
        }
    }
}