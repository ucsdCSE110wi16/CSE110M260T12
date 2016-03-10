package com.cards.flash.testez;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by gurkiratsingh on 3/10/16.
 */
public class InviteMatcher {

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static Matcher<View> noDrawable() {
        return new DrawableMatcher(-1);
    }

    public static Matcher<Object> withStringMatching(String expectedText) {

        return withStringMatching(equalTo(expectedText));
    }


    @SuppressWarnings("rawtypes")
    public static Matcher<Object> withStringMatching(final Matcher<String> itemTextMatcher) {

        return new BoundedMatcher<Object, String>(String.class) {
            @Override
            public boolean matchesSafely(String string) {
                return itemTextMatcher.matches(string);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with string: ");
                itemTextMatcher.describeTo(description);
            }
        };
    }
}
