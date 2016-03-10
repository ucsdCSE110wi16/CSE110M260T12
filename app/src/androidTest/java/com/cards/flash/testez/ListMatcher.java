package com.cards.flash.testez;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;


/**
 * Created by gurkiratsingh on 3/9/16.
 */
class ListMatcher {

    public static Matcher<View> withListSize (final int size) {
        return new TypeSafeMatcher<View>() {

            @Override public boolean matchesSafely (final View view) {
                return ((AdapterView) view).getChildCount() == size;
            }

            @Override public void describeTo (final Description description) {
                description.appendText ("ListView should have " + size + " items");
            }
        };
    }


}