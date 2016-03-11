package com.cards.flash.testez;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.instanceOf;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;

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
    private String personToInvite;
    private int waitTime = 5;

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initObjects(){
        testCategoryName = "Espresso";
        personToInvite = "Airrick Train";
    }

    @Test
    public void categoryCanBeAdded() {
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.cateButton)).perform(click());
        onView(withClassName(endsWith("EditText"))).perform(typeText(testCategoryName), ViewActions.closeSoftKeyboard());
        onView(withText("Add")).perform(click());

        long time = TimeUnit.SECONDS.toMillis(waitTime);


        IdlingResource idlingResource2 = new ElapsedTimeIdlingResource(time);
        registerIdlingResources(idlingResource2);

        onView(allOf(isDescendantOfA(withId(R.id.swiperefresh)), is(instanceOf(ListView.class)))).
                check(matches(hasDescendant(withText(testCategoryName))));

        unregisterIdlingResources(idlingResource2);
    }
    @Test
    public void categoryNavigationDrawerOpens(){
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        closeDrawer(R.id.drawer_layout);
    }

    @Test
    public void categoryNavigationFlashCardsAreAddedInEachCategory(){

        //make sure we are in the right category
        IdlingResource idlingRes = new ElapsedTimeIdlingResource(4000);
        registerIdlingResources(idlingRes);

        openDrawer(R.id.drawer_layout);

        try{
            Thread.sleep(8000);
        }catch (InterruptedException e){}

        onView(allOf(isDescendantOfA(withId(android.R.id.list)), not(is(instanceOf(FlashCard.class))),
                withText(testCategoryName))).perform(click());

        unregisterIdlingResources(idlingRes);

        IdlingResource idlingResource = new DialogIdlingResource(BaseFunction.infinityLayout);
        registerIdlingResources(idlingResource);

        onView(withId(R.id.action_addbutton)).perform(click());
        onView(allOf(withId(android.R.id.list), isDescendantOfA(withId(R.id.mainRelLayout)),
                withChild(allOf(withClassName(endsWith("FlashCard")),
                        hasDescendant(withClassName(endsWith("AddEditFlashCard")))))))
                .check(matches(ListMatcher.withListSize(1)));

        unregisterIdlingResources(idlingResource);
    }

    @Test
    public void inviteAnotherMember(){
        IdlingResource idlingRes = new ElapsedTimeIdlingResource(4000);
        registerIdlingResources(idlingRes);

        openDrawer(R.id.drawer_layout);
        try{
            Thread.sleep(6000);
        }catch (InterruptedException e){}

        onView(allOf(isDescendantOfA(withId(android.R.id.list)), not(is(instanceOf(FlashCard.class))),
                withText(testCategoryName))).perform(click());

        unregisterIdlingResources(idlingRes);
        IdlingResource idlingResource1 = new DialogIdlingResource(BaseFunction.infinityLayout);
        registerIdlingResources(idlingResource1);

        onView(allOf(withId(R.id.invite_button), withTagValue(InviteMatcher.withStringMatching(testCategoryName))))
                .perform(click());

        unregisterIdlingResources(idlingResource1);

        onView(withId(R.id.search_text)).perform(typeText(personToInvite), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.share_button)).perform(click());

        IdlingResource idlingResource2 = new DialogIdlingResource(BaseFunction.infinityLayout);
        registerIdlingResources(idlingResource2);

        IdlingResource idlingRes2 = new ElapsedTimeIdlingResource(4000);
        registerIdlingResources(idlingRes2);

        onView(allOf(withId(R.id.share_button), withTagValue(InviteMatcher.withStringMatching(personToInvite)))).
                        check(matches(InviteMatcher.withDrawable(R.drawable.ic_unshare)));
        unregisterIdlingResources(idlingResource2);

        unregisterIdlingResources(idlingRes2);
        Espresso.pressBack();
    }

    @Test
    public void removeCategory(){
        openDrawer(R.id.drawer_layout);
        try{
            Thread.sleep(6000);
        }catch (InterruptedException e){}

        onView(allOf(isDescendantOfA(withId(android.R.id.list)), not(is(instanceOf(FlashCard.class))),
                withText(testCategoryName))).perform(longClick());
        onView(withText(equalToIgnoringCase("YES"))).perform(click());

        IdlingResource idlingResource = new DialogIdlingResource(BaseFunction.infinityLayout);
        registerIdlingResources(idlingResource);

        IdlingResource idlingRes = new ElapsedTimeIdlingResource(4000);
        registerIdlingResources(idlingRes);
        onView(allOf(isDescendantOfA(withId(android.R.id.list)), not(is(instanceOf(FlashCard.class))),
                withText(testCategoryName))).check(doesNotExist());

        unregisterIdlingResources(idlingRes);
        unregisterIdlingResources(idlingResource);
    }

}