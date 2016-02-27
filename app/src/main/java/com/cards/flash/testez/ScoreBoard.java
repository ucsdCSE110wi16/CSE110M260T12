package com.cards.flash.testez;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoard extends ActionBarActivity {

    ImageAdapter scoreAdapter;
    ListView scoresList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        ActionBar bar = getSupportActionBar();

        bar.setDisplayShowTitleEnabled(false);
        bar.setDisplayHomeAsUpEnabled(true);
        scoreAdapter = new ImageAdapter(this);

        scoresList = (ListView) findViewById(R.id.scores_list);
        scoresList.setAdapter(scoreAdapter);

        getTopScores();

    }
    private void getTopScores(){




    }
    class ImageAdapter extends BaseAdapter {
        private ArrayList<FlashCard> scoresList = new ArrayList<>();


        public ImageAdapter(Context c) {
        }
        @Override
        public int getCount() {
            return scoresList.size();
        }

        @Override
        public Object getItem(int position) {
            return scoresList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return scoresList.get(position);
        }
    }

}
