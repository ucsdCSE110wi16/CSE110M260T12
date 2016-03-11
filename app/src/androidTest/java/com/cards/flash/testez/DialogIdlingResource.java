package com.cards.flash.testez;

/**
 * Created by gurkiratsingh on 3/9/16.
 */
import android.support.test.espresso.IdlingResource;
import android.widget.FrameLayout;

public class DialogIdlingResource implements IdlingResource {
    private final FrameLayout frameLayout;
    private ResourceCallback resourceCallback;

    public DialogIdlingResource(FrameLayout layout) {
        this.frameLayout = layout;
    }

    @Override
    public String getName() {
        return DialogIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = frameLayout == null || (!frameLayout.isShown());
        if (idle) {
            resourceCallback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}