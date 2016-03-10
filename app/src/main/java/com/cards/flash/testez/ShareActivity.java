package com.cards.flash.testez;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import bolts.Task;

public class ShareActivity extends ActionBarActivity implements ShareAdapter.OnShareListener {
    public static final String CATEGORY_ID = "category_ID";
    private List<Boolean> shareList;
    private static List<ParseUser> userList;

    private ParseObject cateObject;
    private ShareAdapter adapter;

    private ProgressBar progressBar;
    private EditText searchText;
    private ListView listView;
    private TextView notFoundTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2a4989")));

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        searchText = (EditText) findViewById(R.id.search_text);
        listView = (ListView) findViewById(R.id.listView);
        notFoundTxtView = (TextView) findViewById(R.id.notFoundTxtView);

        userList = new ArrayList<>();
        adapter = new ShareAdapter(getApplicationContext(), userList, ShareActivity.this);
        listView.setAdapter(adapter);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                loadSearchUserList(s.toString());
            }
        });

        cateObject = NavigationDrawerFragment.getCurrCateObject();
        loadAddedUsersList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseFunction.hideKeyboard(getApplicationContext(), searchText);
    }

    @Override
    public void onBackPressed() {

        ShareActivity.this.finish();

    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    private void cancelLoading() {
        progressBar.setVisibility(View.GONE);
        searchText.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
    }

    public static List<ParseUser> getBaseUserList(){
        return userList;
    }
    private void loadAddedUsersList(){
        try{
            ParseObject category = cateObject.fetch();
            ParseRelation users = category.getRelation("users");
            List<ParseUser> res = users.getQuery().find();
            userList = res;

            determineNotFound(res);
            adapter.updateData(res);
            adapter.notifyDataSetChanged();


        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Could fetch users.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void determineNotFound(List<ParseUser>list){
        if (list.size() == 0){
            notFoundTxtView.setVisibility(View.VISIBLE);
        }else{
            notFoundTxtView.setVisibility(View.GONE);
        }
    }
    private void loadSearchUserList(String text) {

        if (text.trim().isEmpty()){
            loadAddedUsersList();
        }else{
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereStartsWith("name", text.trim());

            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        determineNotFound(objects);
                        adapter.updateData(objects);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to fetch users", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
    private List<ParseUser>getUpdatedUserList(){
        try {
            ParseObject fetchedObject = cateObject.fetch();
            ParseRelation<ParseUser> users = fetchedObject.getRelation("users");
            List<ParseUser> res = users.getQuery().find();
            return res;
        }catch (Exception e){

        }
        return null;
    }
    @Override
    public void onShare(final ParseUser user) {
        BaseFunction.showInfLoading(getApplicationContext());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserToCategory");
        query.whereEqualTo("userId", user.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ParseRelation<ParseObject> relation = parseObject.getRelation("category");
                    relation.add(cateObject);

                    ParseRelation<ParseUser> users = cateObject.getRelation("users");
                    users.add(user);
                    cateObject.saveInBackground();

                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                try {
                                    List<ParseUser> newList = getUpdatedUserList();
                                    if (newList != null)
                                        userList = newList;
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getApplicationContext(), "User added!", Toast.LENGTH_SHORT).show();
                                }catch (Exception ex){

                                }
                            } else {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            BaseFunction.destroyInfLoading(getApplicationContext());
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    BaseFunction.destroyInfLoading(getApplicationContext());
                }

            }
        });
    }

    @Override
    public void onUnshare(final ParseUser user) {
        BaseFunction.showInfLoading(getApplicationContext());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserToCategory");
        query.whereEqualTo("userId", user.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ParseRelation<ParseObject> relation = parseObject.getRelation("category");
                    relation.remove(cateObject);

                    ParseRelation<ParseUser> users = cateObject.getRelation("users");
                    users.remove(user);
                    cateObject.saveInBackground();
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                try {
                                    List<ParseUser> newList = getUpdatedUserList();
                                    if (newList != null)
                                        userList = newList;
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getApplicationContext(), "User removed!", Toast.LENGTH_SHORT).show();
                                }catch (Exception ex){

                                }

                            } else {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            BaseFunction.destroyInfLoading(getApplicationContext());
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    BaseFunction.destroyInfLoading(getApplicationContext());
                }

            }
        });
    }

}
