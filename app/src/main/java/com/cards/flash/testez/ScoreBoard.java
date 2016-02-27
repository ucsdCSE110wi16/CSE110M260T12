package com.cards.flash.testez;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

        getTopScores(scoreAdapter);

    }
    private void getTopScores(ImageAdapter imageAdapter ){

        System.out.println("fetching scores");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Categories");
        query.whereEqualTo("objectId", getIntent().getStringExtra("cat"));

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    System.out.println("populating views");
                    populateScoreAdapter(parseObject);
                } else {
                    Toast.makeText(getApplicationContext(), "Retrieval failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void populateScoreAdapter(final ParseObject parseObject) {

        final ArrayList<Map.Entry<String, Integer>> scoresList = new ArrayList<>();
        ParseRelation<ParseObject> relation = parseObject.getRelation("userscores");
        ParseQuery query = relation.getQuery();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {
                    for (ParseObject scores : list) {
                        System.out.println(scores.getString("username") + " " +
                                new Integer(scores.getInt("score")));
                        scoresList.add(new AbstractMap.SimpleEntry<String, Integer>
                                (scores.getString("username"), new Integer(scores.getInt("score"))));
                    }
                }

                Collections.sort(scoresList, new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs) {
                        return (lhs.getValue() > rhs.getValue()) ? 1 : -1;
                    }
                });

                scoreAdapter.addScoreList(scoresList);
            }
        });

    }
    class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflator;
        private ArrayList<Map.Entry<String,Integer>> scoresList = new ArrayList<>();


        public ImageAdapter(Context c) {
            mContext = c;
            mInflator = (LayoutInflater) getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return scoresList.size();
        }
        public void addScoreList(ArrayList<Map.Entry<String,Integer>> scores){

            System.out.println("adding to adapter");
            scoresList = scores;

            notifyDataSetChanged();

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
            if(convertView == null){
                System.out.println("in getView");
               LinearLayout rootView = (LinearLayout) mInflator.inflate(R.layout.score_list_item, parent, false);

                TextView name = (TextView) rootView.findViewById(R.id.score_name);
                TextView score = (TextView) rootView.findViewById(R.id.score_value);

                name.setText(scoresList.get(position).getKey());

                score.setText(" " + scoresList.get(position).getValue());
                return rootView;
            }else {

                return (LinearLayout) convertView;
            }
        }
    }

}
