package com.example.taskmaster;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void userName(){
        onView(withId(R.id.settingsButton)).perform(click());

        onView(withId(R.id.saveUserName)).check(matches(isDisplayed()));

        onView(withId(R.id.userName)).perform(typeText("Mahmoud"), closeSoftKeyboard());

        onView(withId(R.id.saveUserName)).perform(click());

        Espresso.pressBack();

        onView(withId(R.id.homePageTitle)).check(matches(isDisplayed()));

        onView(withText("Mahmoud's Tasks")).check(matches(isDisplayed()));
    }
}
