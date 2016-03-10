package com.cards.flash.testez;

/**
 * Created by gurkiratsingh on 3/9/16.
 */
import android.view.View;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.is;

public class CustomMatchers {

    public static Matcher<View> withResourceName(String resourceName) {
        return withResourceName(is(resourceName));
    }

    public static Matcher<View> withResourceName(final Matcher<String> resourceNameMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with resource name: ");
                resourceNameMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                int id = view.getId();
                return id != View.NO_ID && id != 0 && view.getResources() != null
                        && resourceNameMatcher.matches(view.getResources().getResourceName(id));
            }
        };
    }
}