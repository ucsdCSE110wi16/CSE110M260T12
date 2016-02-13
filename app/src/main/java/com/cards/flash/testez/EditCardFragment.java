package com.cards.flash.testez;

/**
 * Created by Vincent on 2/11/2016.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditCardFragment extends Fragment {

    private FlashCard.AddOrEdit flashCard;
    private ImageAdapter imAdapter;
    public ListView lView;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private  final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout)inflater.inflate(R.layout.fragment_main, container, false);
        flashCard = new FlashCard.AddOrEdit(getContext(),FlashCardEnum.ADD_MODE);

        rootView.addView(flashCard,0);


        // Add listview to frame
        ListView lView = (ListView)rootView.findViewById(R.id.listView);
        imAdapter = new ImageAdapter(rootView.getContext());
        lView.setAdapter(imAdapter);


        //'rootView.addView(flashCard);
            /*FlashCard.AddOrEdit flashCard = new FlashCard.AddOrEdit(getContext(), FlashCardEnum.EDIT_MODE);
            flashCard.setMultiChoiceSettings(object.getString("answer"), array);
            flashCard.setQuestion(object.getString("quetion"));*/


        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.add_flashcards:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt("position"));
    }


    class ImageAdapter extends BaseAdapter {
        private ArrayList<String> qs = new ArrayList<String>();
        private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
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
        FlashCard.AddOrEdit view = new FlashCard.AddOrEdit(mContext,FlashCardEnum.ADD_MODE);
        return view;
    }
}
}