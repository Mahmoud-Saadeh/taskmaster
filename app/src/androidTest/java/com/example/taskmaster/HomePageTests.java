package com.example.taskmaster;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
public class HomePageTests {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void SettingButtonTest(){
        onView(withId(R.id.settingsButton)).perform(click());

        onView(withId(R.id.saveUserName)).check(matches(isDisplayed()));
        Espresso.pressBack();
    }
    @Test
    public void AddTaskButtonTest() {
        onView(withText("TaskMaster")).check(matches(isDisplayed()));

        onView(withId(R.id.addTaskMenu)).perform(click());

        onView(withId(R.id.taskTitleLayout)).check(matches(isDisplayed()));

    }


}
