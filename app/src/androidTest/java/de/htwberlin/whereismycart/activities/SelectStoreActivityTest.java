package de.htwberlin.whereismycart.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htwberlin.whereismycart.CartApplication;
import de.htwberlin.whereismycart.R;
import de.htwberlin.whereismycart.store.Store;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SelectStoreActivityTest {

    @Rule
    public ActivityScenarioRule<SelectStoreActivity> activityRule = new ActivityScenarioRule<>(SelectStoreActivity.class);

    @Test
    public void testSelectingLidlWorks() {
        testButtons(R.id.select_store_lidl_button, Store.LIDL);
    }

    @Test
    public void testSelectingEdekaWorks() {
        testButtons(R.id.select_store_edeka_button, Store.EDEKA);
    }

    @Test
    public void testSelectingReweWorks() {
        testButtons(R.id.select_store_rewe_button, Store.REWE);
    }

    @Test
    public void testSelectingAldiWorks() {
        testButtons(R.id.select_store_aldi_button, Store.ALDI);
    }

    private static void testButtons(int id, Store expectedStore) {

        CartApplication application = ApplicationProvider.getApplicationContext();

        onView(withId(id)).check(matches(isDisplayed()));
        onView(withId(id)).perform(click());
        onView(withId(R.id.add_cart_add_cart_button)).check(matches(isDisplayed()));

        Store store = application.getStoreManager().getSelectedStore();

        Assert.assertEquals(store, expectedStore);
    }

}
