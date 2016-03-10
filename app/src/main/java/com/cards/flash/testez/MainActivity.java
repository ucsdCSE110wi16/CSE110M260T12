package com.cards.flash.testez;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActionBarDrawerToggle;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;


import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

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
    static ArrayList<ParseObject> cateList;

    private static ArrayList<EditCardFragment> fragments;
    private static FragmentManager fragmentManager;
    private static EditCardFragment currFrag;

    private DrawerLayout mDrawlayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private static ActionBar bar;
    /**
     * Used to store the added categories from the "Add Category" button
     */
    public static ArrayList<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DD", "here");
        setContentView(R.layout.main_activity);
        Log.d("DD", "here1");
        bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2a4989")));

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mDrawlayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer, mDrawlayout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawlayout, R.drawable.ic_drawer,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            //close keyboard when opening drawer
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                InputMethodManager im = (InputMethodManager) getCurrentFocus().getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

        };
        mDrawlayout.setDrawerListener(mDrawerToggle);
        fragmentManager = getSupportFragmentManager();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        System.out.println("here");

        mNavigationDrawerFragment.fetchAllCategories();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("destro");
    }

    public static void initFragmentList(){
        if (currFrag != null){
            fragmentManager.beginTransaction().remove(currFrag).commit();
            currFrag = null;
        }
        fragments = new ArrayList<>();
        if (categories != null)
            for (int i = 0; i < categories.size(); i++){
                fragments.add(null);
            }
    }
    public static void removeFragment(int pos){
        if (fragments != null){
            if (currFrag ==  fragments.get(pos)){
                fragmentManager.beginTransaction().remove(fragmentManager.
                        findFragmentById(R.id.container)).commit();
                currFrag = null;
            }
            fragments.remove(pos);
        }
    }
    public static EditCardFragment getCurrFrag(){
        return currFrag;
    }
    private Bitmap resizeBitmapImageFn(
            Bitmap bmpSource, int maxResolution){
        int iWidth = bmpSource.getWidth();
        int iHeight = bmpSource.getHeight();
        int newWidth = iWidth ;
        int newHeight = iHeight ;
        float rate = 0.0f;

        if(iWidth > iHeight ){
            if(maxResolution < iWidth ){
                rate = maxResolution / (float) iWidth ;
                newHeight = (int) (iHeight * rate);
                newWidth = maxResolution;
            }
        }else{
            if(maxResolution < iHeight ){
                rate = maxResolution / (float) iHeight ;
                newWidth = (int) (iWidth * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(
                bmpSource, newWidth, newHeight, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        Bitmap add_icon = BitmapFactory.decodeResource(getResources(), R.drawable.addbutton); //Converting drawable into bitmap
        Bitmap new_add_icon = resizeBitmapImageFn(add_icon, 80); //resizing the bitmap
        Drawable d = new BitmapDrawable(getResources(),new_add_icon); //Converting bitmap into drawable
        menu.getItem(1).setIcon(d);

        Bitmap edit_icon = BitmapFactory.decodeResource(getResources(), R.drawable.editbutton); //Converting drawable into bitmap
        Bitmap new_edit_icon = resizeBitmapImageFn(edit_icon, 80); //resizing the bitmap
        Drawable edit_d = new BitmapDrawable(getResources(),new_edit_icon); //Converting bitmap into drawable
        menu.getItem(0).setIcon(edit_d);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addbutton:
                if (currFrag == null)
                    Toast.makeText(getApplicationContext(), "Create a category first.", Toast.LENGTH_SHORT).show();
                else
                    currFrag.addCard();
                break;
            case R.id.action_editbutton:
                if (currFrag == null)
                    Toast.makeText(getApplicationContext(), "Create a category first.", Toast.LENGTH_SHORT).show();
                else
                    currFrag.editCard();
                break;
        }
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction tf = fragmentManager.beginTransaction();

        if (currFrag != null){
            tf.hide(currFrag);
        }

        System.out.println(position);
        EditCardFragment frag = fragments.get(position);


        if (frag == null){
            frag = new EditCardFragment();
            fragments.add(position, frag);
            tf.add(R.id.container, frag);
            Bundle p = new Bundle();
            p.putInt("position", position);
            frag.setArguments(p);
        }else{
            tf.show(frag);
        }
        currFrag = frag;

        tf.commit();

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
                mTitle = getString(R.string.title_section3);;
                break;

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
    public static ActionBar getMainActionBar(){
        return bar;
    }

}
