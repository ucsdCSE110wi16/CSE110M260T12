package com.cards.flash.testez;


import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Handler;

import static android.R.color.holo_blue_bright;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {


    private static ArrayAdapter<String> arrayAdapter;
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static int mCurrentSelectedPosition = -1;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;


    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DD", "in navi");
        MainActivity.categories = new ArrayList<>();
        MainActivity.cateList = new ArrayList<>();
        this.setRetainInstance(true);
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, true);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

    }


    private void initiateRefresh() {
        fetchAllCategories();
    }

    private void onRefreshComplete(String cateName, int pos) {

        MainActivity.initFragmentList();
        arrayAdapter.notifyDataSetChanged();
        if (cateName != null){
            selectItem(pos);
        }else if (MainActivity.categories.size() != 0){
            selectItem(0);
        }else{
            mCurrentSelectedPosition = -1;
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    private void selectItemLong(final int position)
    {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(),AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            alertDialogBuilder.setTitle("Delete Category?");

            final TextView et = new TextView(getContext());
            alertDialogBuilder.setView(et);

            // set dialog message
            alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    final ParseObject object = MainActivity.cateList.get(position);
                    ParseRelation<ParseUser> rel = object.getRelation("users");
                    try {
                        List<ParseUser> parseUsers = rel.getQuery().find();
                        if (parseUsers.size() == 1) {
                            object.deleteInBackground();
                            deleteCategory(position);
                        } else {
                            rel.remove(ParseUser.getCurrentUser());

                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserToCategory");
                                        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject o, ParseException e) {
                                                if (e == null) {
                                                    ParseRelation<ParseObject> obj = o.getRelation("category");
                                                    obj.remove(object);
                                                    o.saveInBackground();

                                                    deleteCategory(position);

                                                } else {
                                                    Toast.makeText(getContext(), "Error in deleting", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(getContext(), "Error in deleting", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    } catch (Exception ex) {

                    }

                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });



            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();

    }

    private void deleteCategory(int position){
        MainActivity.cateList.remove(position);
        MainActivity.categories.remove(position);

        arrayAdapter.notifyDataSetChanged();
        MainActivity.removeFragment(position);
        if (MainActivity.getCurrFrag() == null) {
            if (MainActivity.categories.size() != 0)
                selectItem(0);
            else
                selectItem(-1);
        }
        Toast.makeText(getContext(), "Category Deleted", Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
            View view = inflater.inflate(R.layout.drawer_main, container, false);

            Button cateButton = (Button)view.findViewById(R.id.cateButton);
            cateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDrawer();
                    configureAddCategoryButton();
                }
            });

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
            mSwipeRefreshLayout.setColorSchemeResources(
                    R.color.color_scheme_1_1, R.color.color_scheme_1_2,
                    R.color.color_scheme_1_3, R.color.color_scheme_1_4);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initiateRefresh();
                }
            });



            mDrawerListView = (ListView) view.findViewById(android.R.id.list);

            mDrawerListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    selectItemLong(pos);
                    return true;
                }
            });


            mDrawerListView.setAdapter(arrayAdapter = new ArrayAdapter<String>(
                    getActionBar().getThemedContext(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    MainActivity.categories

            ));
            mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectItem(position);
                    closeDrawer();
                }
            });


        return view;
    }

    private void configureAddCategoryButton(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);

        builder.setTitle("Add New Category");

        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setTextColor(Color.WHITE);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String categoryName = input.getText().toString().trim();

                if (categoryName.equals(""))
                    Toast.makeText(getContext(), "You must enter a name", Toast.LENGTH_SHORT).show();
                else{
                    final ParseObject newCategory = new ParseObject("Categories");
                    newCategory.put("name", categoryName);
                    ParseRelation<ParseObject> rel = newCategory.getRelation("users");
                    rel.add(ParseUser.getCurrentUser());
                    newCategory.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("UserToCategory");
                                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(final ParseObject parseObject, ParseException e) {
                                        if (e == null) {
                                            ParseRelation<ParseObject> relation = parseObject.getRelation("category");
                                            relation.add(newCategory);
                                            parseObject.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e != null) {
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                    } else {

                                                        Toast.makeText(getContext(), "Category Added!",
                                                                Toast.LENGTH_SHORT).show();
                                                        MainActivity.categories.add(categoryName);
                                                        MainActivity.cateList.add(newCategory);
                                                        Collections.sort(MainActivity.categories);
                                                        Collections.sort(MainActivity.cateList, new CustomComparator());

                                                        int pos = MainActivity.categories.indexOf(categoryName);
                                                        arrayAdapter.notifyDataSetChanged();
                                                        MainActivity.initFragmentList();
                                                        selectItem(pos);

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }

        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void fetchAllCategories(){
        System.out.println("fetching");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserToCategory");
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ParseRelation<ParseObject> relation = parseObject.getRelation("category");
                    ParseQuery query = relation.getQuery();
                    query.addAscendingOrder("name");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if (e == null) {
                                String cateName = null;
                                if (MainActivity.categories.size() != 0)
                                    cateName = MainActivity.categories.get(mCurrentSelectedPosition);

                                MainActivity.categories.clear();
                                MainActivity.cateList.clear();
                                for (ParseObject category : parseObjects) {
                                    String toRetrieve = (String) category.get("name");
                                    MainActivity.categories.add(toRetrieve);
                                }
                                MainActivity.cateList.addAll(parseObjects);
                                int pos = MainActivity.categories.indexOf(cateName);
                                if (pos == -1) {
                                    cateName = null;
                                }
                                onRefreshComplete(cateName, pos);
                            } else {
                                Toast.makeText(getContext(), "Retrieval failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Retrieval failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {

        mCurrentSelectedPosition = position;
        if (MainActivity.categories.size() != 0)
            MainActivity.getMainActionBar().setTitle(arrayAdapter.getItem(mCurrentSelectedPosition));
        else
            getActionBar().setTitle("TestEZ");

        if (position != -1){
            mDrawerListView.setItemChecked(position, true);
            mCallbacks.onNavigationDrawerItemSelected(position);
        }

    }

    private void closeDrawer(){
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }
    /**
     *
     * @return String the current Category
     */
    public static int getCurrentSelectedPos(){
        return mCurrentSelectedPosition;
    }
    public static ParseObject getCurrCateObject(){
        return MainActivity.cateList.get(mCurrentSelectedPosition);
    }
    private String getCategoryTitle()
    {
        return getActionBar().getTitle().toString();
    }

    /**
     * Helper method to find the category to be deleted
     * @param toDelete
     * @param theList
     * @return
     */
    private int findCategoryDelete(String toDelete, ArrayList<String> theList)
    {
        int i = 0;
        int toReturn = -1;


        while(i < theList.size())
        {
            if(theList.get(i) == toDelete)
            {
                toReturn = i;
            }
            else
                i++;
        }

        return toReturn;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    public ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();

    }
    public class CustomComparator implements Comparator<ParseObject> {
        @Override
        public int compare(ParseObject o1, ParseObject o2) {
            return o1.getString("name").compareTo(o2.getString("name"));
        }
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
