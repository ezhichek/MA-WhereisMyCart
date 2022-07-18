package de.htwberlin.whereismycart.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htwberlin.whereismycart.R;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testUserButtonOpensSelectStoreActivity() {
        onView(withId(R.id.main_user_button)).check(matches(isDisplayed()));
        onView(withId(R.id.main_user_button)).perform(click());
        onView(withId(R.id.select_store_lidl_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmployeeButtonOpensCartListActivity() {
        onView(withId(R.id.main_employee_button)).check(matches(isDisplayed()));
        onView(withId(R.id.main_employee_button)).perform(click());
        onView(withId(R.id.cart_list_header_view)).check(matches(isDisplayed()));
    }

}
