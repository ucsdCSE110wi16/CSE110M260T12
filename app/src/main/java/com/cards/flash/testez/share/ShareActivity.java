package com.cards.flash.testez.share;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.cards.flash.testez.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ShareActivity extends ActionBarActivity implements ShareAdapter.OnShareListener {
    public static final String CATEGORY_ID = "category_ID";
    private List<ParseUser> userList;
    private List<ParseObject> shareList;
    private ParseObject category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        //ToDo add category for sharing
        ((EditText) findViewById(R.id.search_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadUserList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*
        * when you will start this activity you need to send Category id in intent
        * Intent intent = new Intent(MainActivity.this, ShareActivity.class);
        * Bundle b = new Bundle();
        * b.putInt(ShareActivity.CATEGORY_ID, categoryId); //Your id
        * intent.putExtras(b); //Put your id to your next Intent
        * startActivity(intent);
        */

        //loadCategory(getIntent().getExtras().getString(CATEGORY_ID));

        //ToDo remove when category will be done. this function must start in  83 line
        loadUserList();
    }

    private void loadCategoryShareList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Share");
        query.whereEqualTo("category_id",category.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                loadUserList();
            }
        });
    }

    //needs to be runnned only on the main thread
    //hide all view and show progress bar
    private void showLoading() {
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.listView).setVisibility(View.GONE);
    }

    //needs to be runnned only on the main thread
    //hide progress bar and show all view
    private void cancelLoading() {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        findViewById(R.id.search_text).setVisibility(View.VISIBLE);
        findViewById(R.id.listView).setVisibility(View.VISIBLE);

    }

    private void loadCategory(String categoryId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Categories");
        query.getInBackground(categoryId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    category = object;
                    loadCategoryShareList();
                } else {
                    // something went wrong
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadUserList() {
        showLoading();
        String searchText = ((EditText) findViewById(R.id.search_text)).getText().toString();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        if (searchText.length() > 0)//else - load all users
            query.whereStartsWith("name", searchText);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    onLoaded(objects);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onShare(ParseObject user) {
        ParseObject shareObject = new ParseObject("Share");
        shareObject.put("user_id", user.get("id"));
        shareObject.put("category_id", category.get("id"));
        shareObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    // something went wrong
                }
            }
        });

    }

    public void onLoaded(final List<ParseUser> userQueryResult) {
        //because view can be changed only on main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ListView) findViewById(R.id.listView)).setAdapter(new ShareAdapter(getApplicationContext(), userQueryResult,shareList, ShareActivity.this));
                userList = userQueryResult;
                cancelLoading();
            }
        });

    }
}