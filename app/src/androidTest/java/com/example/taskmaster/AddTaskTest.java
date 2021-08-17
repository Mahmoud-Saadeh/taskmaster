package com.example.taskmaster;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
//import androidx.test.espresso.contrib.RecyclerViewActions;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddTaskTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void addTaskTest(){
        onView(withId(R.id.addTaskMenu)).perform(click());

        onView(withId(R.id.task_state_spinner)).check(matches(isDisplayed()));

        onView(withId(R.id.taskTitle)).perform(typeText("write espresso test"), closeSoftKeyboard());
        onView(withId(R.id.taskDescription)).perform(typeText("Write tests for all of the pages and their functionality"), closeSoftKeyboard());
        onView(withId(R.id.task_state_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("COMPLETE"))).perform(click());

        onView(withId(R.id.addTask)).perform(click());

        Espresso.pressBack();

        onView(withId(R.id.homePageTitle)).check(matches(isDisplayed()));
//        onView(withText("write espresso test3")).check(matches(isDisplayed()));
    }

    @Test
    public void taskDetails() throws InterruptedException {
//        onView(withId(R.id.task_list))
//                .perform(actionOnItemAtPosition(0, click()));
        //progress dialog is now shown
        Thread.sleep(1500);
        onView(withText("write espresso test")).perform(click());
        Thread.sleep(1500);
        onView(withText("write espresso test")).check(matches(isDisplayed()));
    }
}
