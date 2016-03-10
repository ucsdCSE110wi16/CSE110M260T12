package com.cards.flash.testez;

import android.app.Application;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityTestCase;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.instanceOf;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static com.cards.flash.testez.CustomMatchers.withResourceName;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.junit.FixMethodOrder;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)

public class EspressoTest {

    private String testCategoryName;
    private int waitTime = 2;

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initObjects(){
        testCategoryName = "Espresso";
        //IdlingPolicies.setMasterPolicyTimeout(waitTime * 2, TimeUnit.SECONDS);
        //IdlingPolicies.setIdlingResourceTimeout(waitTime * 2, TimeUnit.SECONDS);
    }

    @Test
    public void newCategoryCanBeAdded() {
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.cateButton)).perform(click());
        onView(withClassName(endsWith("EditText"))).perform(typeText(testCategoryName), ViewActions.closeSoftKeyboard());
        //onView(withClassName(endsWith("EditText"))).check(matches(isDisplayed()));
        onView(withText("Add")).perform(click());

        //IdlingResource idlingResource = new ElapsedTimeIdlingResource(waitTime);
        //registerIdlingResources(idlingResource);

        //onView(allOf(isDescendantOfA(withId(R.id.action_bar_container)), withText(testCategoryName))).check(matches(isDisplayed()));
        //onView(withText(testCategoryName)).check(matches(isDisplayed()));
        //unregisterIdlingResources(idlingResource);
    }
    @Test
    public void navigationDrawerOpens(){
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
    }

    /*@Test
    public void cardsAreAddedInEachCategory(){
        //onView(withId(R.id.action_addbutton)).perform(click());
    }*/


}