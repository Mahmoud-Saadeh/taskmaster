package com.example.taskmaster;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsTest {
    @Rule
    public ActivityScenarioRule<SignInActivity> activityRule =
            new ActivityScenarioRule<>(SignInActivity.class);

    @Before
    public void signIn(){
        onView(withId(R.id.userNameSignIn)).perform(clearText(),typeText("mahmoud"), closeSoftKeyboard());
        onView(withId(R.id.passwordSignIn)).perform(clearText(),typeText("123456789"), closeSoftKeyboard());

        onView(withId(R.id.btnSignIn)).perform(click());
    }
    @Test
    public void userName() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.settingsButton)).perform(click());

        onView(withId(R.id.saveUserName)).check(matches(isDisplayed()));

        onView(withId(R.id.userName)).perform(clearText(),typeText("Mahmoud"), closeSoftKeyboard());

        onView(withId(R.id.task_team_spinner_settings)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("team3"))).perform(click());

        onView(withId(R.id.saveUserName)).perform(click());

        Espresso.pressBack();

        onView(withId(R.id.homePageTitle)).check(matches(isDisplayed()));
        Thread.sleep(1000);
//        onView(withText("Mahmoud's Tasks")).check(matches(isDisplayed()));
        onView(withText("Filtered by: team3")).check(matches(isDisplayed()));
    }
}
