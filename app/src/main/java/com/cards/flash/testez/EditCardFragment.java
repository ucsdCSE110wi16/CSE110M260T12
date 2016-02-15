package com.cards.flash.testez;

/**
 * Created by Vincent on 2/11/2016.
 */

import android.app.Activity;
import android.support.v7.app.ActionBar;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditCardFragment extends ListFragment {


    private ImageAdapter imAdapter;
    public ListView lView;
    public Button quizButton, practiceButton, editButton, addButton;


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private  final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imAdapter = new ImageAdapter(getActivity());

        this.setListAdapter(imAdapter);
        RelativeLayout rootView = (RelativeLayout)inflater.inflate(R.layout.fragment_main, container, false);



        quizButton = (Button) rootView.findViewById(R.id.quiz_button);
        practiceButton = (Button) rootView.findViewById(R.id.practice_button);
        editButton = (Button) rootView.findViewById(R.id.edit_button);
        addButton = (Button) rootView.findViewById(R.id.add_button);

        setUpButtons();
        return rootView;
    }

    // set on click listeners for buttons
    private void setUpButtons(){
        quizButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        practiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager im = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                imAdapter.addCard();
                imAdapter.notifyDataSetChanged();
            }
        });


    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt("position"));
    }


    class ImageAdapter extends BaseAdapter {
        private ArrayList<AddEditFlashCard> qs = new ArrayList<>();


    public ImageAdapter(Context c) {
    }
    public void addCard(){
       qs.add(new AddEditFlashCard(getContext(),FlashCardEnum.ADD_MODE));
    }
    public void changeMode(FlashCardEnum mode){


    }
    @Override
    public int getCount() {
        return qs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return qs.get(position);
    }
}
}