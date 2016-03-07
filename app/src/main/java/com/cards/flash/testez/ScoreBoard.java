package com.cards.flash.testez;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import bolts.Task;

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
        updateTopScores();
    }

    private void updateTopScores(){

        System.out.println("fetching scores");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Categories");
        query.whereEqualTo("objectId", getIntent().getStringExtra("cat"));

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    System.out.println("populating views" );
                    populateScoreAdapter(parseObject);
                } else {
                    Toast.makeText(getApplicationContext(), "Retrieval failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void populateScoreAdapter(final ParseObject parseObject) {
        final ArrayList<String> ids = new ArrayList<>();
        final ArrayList<Map.Entry<String, Integer>> scoresList = new ArrayList<>();
        ParseRelation<ParseObject> relation = parseObject.getRelation("userscores");
        ParseQuery query = relation.getQuery();
        query.orderByDescending("score");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject user;
                if (e == null ) {
                    for (final ParseObject scores : list) {
                        user = scores.getParseObject("user");
                        try {
                            user.fetchIfNeeded();
                        }catch(ParseException exc){exc.printStackTrace();}
                                scoresList.add(new AbstractMap.SimpleEntry<String, Integer>
                                        (user.getString("name"), new Integer(scores.getInt("score"))));
                                ids.add(user.getString("id"));
                    }

                    scoreAdapter.addScoreList(scoresList, ids);
                    scoreAdapter.notifyDataSetChanged();
                }

                System.out.println("done retrieving scores");
            }
        });

    }
    class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflator;
        private ArrayList<Map.Entry<String,Integer>> scoresList = new ArrayList<>();
        private ArrayList<String> profilePics;


        public ImageAdapter(Context c) {
            mContext = c;
            mInflator = (LayoutInflater) getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return scoresList.size();
        }

        public void addScoreList(ArrayList<Map.Entry<String,Integer>> scores, ArrayList<String> pics){

            System.out.println("adding to adapter");
            scoresList = scores;
            profilePics = pics;
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
            byte[] prof;
            if(convertView == null){
                System.out.println("in getView");
                LinearLayout rootView = (LinearLayout) mInflator.inflate(R.layout.score_list_item, parent, false);

                TextView name = (TextView) rootView.findViewById(R.id.score_name);
                TextView score = (TextView) rootView.findViewById(R.id.score_value);
                ProfilePictureView pic =  (ProfilePictureView) rootView.findViewById(R.id.profile_pic);

                pic.setPresetSize(ProfilePictureView.SMALL);
                pic.setProfileId(profilePics.get(position));

                name.setText(scoresList.get(position).getKey());

                score.setText(" " + scoresList.get(position).getValue());
                return rootView;
            }else {
                return (LinearLayout) convertView;
            }
        }
    }
}
