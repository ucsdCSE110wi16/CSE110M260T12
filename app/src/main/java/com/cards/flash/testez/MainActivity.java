package com.cards.flash.testez;

import android.app.Activity;
import android.content.Intent;

import android.content.res.Configuration;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v4.app.ActionBarDrawerToggle;

import android.graphics.Point;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;


import android.util.Base64;
import android.util.Log;

import android.view.Display;
import android.view.GestureDetector;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.inputmethod.InputMethodManager;


import java.util.ArrayList;

import com.cards.flash.testez.share.ShareActivity;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    static String userName;
    static String userEmail;
    static String userId;
    static int screenWidth;
    static int screenHeight;


    private EditCardFragment editCard;

    private DrawerLayout mDrawlayout;
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     * Used to store the added categories from the "Add Category" button
     */
    public static ArrayList<String> categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mDrawlayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer, mDrawlayout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawlayout, R.drawable.ic_drawer,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerOpened(View view) {                       //close keyboard when opening drawer
                super.onDrawerOpened(view);
                InputMethodManager im = (InputMethodManager) getCurrentFocus().getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

        };
        mDrawlayout.setDrawerListener(mDrawerToggle);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /** Called when the user touches the Quiz button */
    public void setQuiz(View view) {

    }
    /** Called when the user touches the Practice button */
    public void setPractice(View view) {

    }
    /** Called when the user touches the Edit button */
    public void setEdit(View view) {

    }
    /** Called when the user touches the Add button */
    public void setAdd(View view) {

    }
    private void resetButtonPress(){
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        editCard = new EditCardFragment();
        Bundle p = new Bundle();
        p.putInt("position",position);
        editCard.setArguments(p);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, editCard)
                .commit();
        if (position == 3) {
            Intent intent = new Intent(MainActivity.this, ShareActivity.class);
            startActivity(intent);
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = "test";
                break;

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

}
