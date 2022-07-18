package de.htwberlin.whereismycart.activities;

import static android.location.LocationManager.GPS_PROVIDER;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htwberlin.whereismycart.CartApplication;
import de.htwberlin.whereismycart.R;
import de.htwberlin.whereismycart.cart.Cart;
import de.htwberlin.whereismycart.cart.CartManager;
import de.htwberlin.whereismycart.cart.CartRepository;
import de.htwberlin.whereismycart.cart.CartStatus;
import de.htwberlin.whereismycart.location.Coordinates;
import de.htwberlin.whereismycart.settings.SettingsManager;
import de.htwberlin.whereismycart.store.Store;
import de.htwberlin.whereismycart.util.TestUtils;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CartListActivityTest {

    @Rule
    public ActivityScenarioRule<CartListActivity> activityRule = new ActivityScenarioRule<>(CartListActivity.class);

    private static final Coordinates currentLocation = new Coordinates(TestUtils.createMockLocation(GPS_PROVIDER));

    @Before
    public void setup() {

        final CartApplication application = ApplicationProvider.getApplicationContext();

        final CartRepository cartRepository = application.getDatabase().cartRepository();
        cartRepository.deleteAllCarts();

        Intents.init();
        TestUtils.resetSettings();
    }

    @After
    public void cleanup() {
        TestUtils.resetSettings();
        Intents.release();
    }

    @Test
    public void testOnlyNewCartsAreShownInList() {

        final Store store = Store.LIDL;

        final CartApplication application = ApplicationProvider.getApplicationContext();

        final CartManager manager = application.getCartManager();
        manager.addNewCart(store, currentLocation);

        onView(withId(R.id.cart_list_list_view)).check(itemCountIs(1));
    }

    @Test
    public void testCartsCanBeMarkedAsCollected() {

        final Store store = Store.LIDL;

        final CartApplication application = ApplicationProvider.getApplicationContext();

        final CartRepository repository = application.getDatabase().cartRepository();

        final CartManager manager = application.getCartManager();
        final Cart cart = manager.addNewCart(store, currentLocation);

        onView(viewInListItem(0, R.id.cart_list_item_confirm_button)).perform(click());
        onView(withText("Mark as collected")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

        final Cart update = repository.findById(cart.getId());

        assertEquals(CartStatus.COLLECTED, update.getStatus());
    }

    @Test
    public void testCartsCanBeMarkedAsCollectedCanBeCancelled() {

        final Store store = Store.LIDL;

        final CartApplication application = ApplicationProvider.getApplicationContext();

        final CartRepository repository = application.getDatabase().cartRepository();

        final CartManager manager = application.getCartManager();
        final Cart cart = manager.addNewCart(store, currentLocation);

        onView(viewInListItem(0, R.id.cart_list_item_confirm_button)).perform(click());
        onView(withText("Mark as collected")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());

        final Cart update = repository.findById(cart.getId());

        assertEquals(CartStatus.NEW, update.getStatus());
    }

    @Test
    public void testCartsCanBeMarkedAsNotFound() {

        final Store store = Store.LIDL;

        final CartApplication application = ApplicationProvider.getApplicationContext();

        final CartRepository repository = application.getDatabase().cartRepository();

        final CartManager manager = application.getCartManager();
        final Cart cart = manager.addNewCart(store, currentLocation);

        onView(viewInListItem(0, R.id.cart_list_item_delete_button)).perform(click());
        onView(withText("Mark as not found")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

        final Cart update = repository.findById(cart.getId());

        assertEquals(CartStatus.NOT_FOUND, update.getStatus());
    }

    @Test
    public void testCartsCanBeMarkedAsNotFoundCanBeCancelled() {

        final Store store = Store.LIDL;

        final CartApplication application = ApplicationProvider.getApplicationContext();

        final CartRepository repository = application.getDatabase().cartRepository();

        final CartManager manager = application.getCartManager();
        final Cart cart = manager.addNewCart(store, currentLocation);

        onView(viewInListItem(0, R.id.cart_list_item_delete_button)).perform(click());
        onView(withText("Mark as not found")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());

        final Cart update = repository.findById(cart.getId());

        assertEquals(CartStatus.NEW, update.getStatus());
    }

    @Test
    public void testNavigationToGoogleMapsWorks() {

        final Store store = Store.LIDL;

        final CartApplication application = ApplicationProvider.getApplicationContext();

        final CartManager manager = application.getCartManager();
        manager.addNewCart(store, currentLocation);

        onView(viewInListItem(0, R.id.cart_list_item_map_button)).perform(click());

        final String uri = "geo:0,0?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();

        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData(uri)));
    }

    @Test
    public void testListWorksWithEmptySettings() {

        final CartApplication application = ApplicationProvider.getApplicationContext();

        final SettingsManager settingsManager = application.getSettingsManager();
        settingsManager.updateStore(null);
        settingsManager.updateStoreAddress("");
        settingsManager.updateRadius(0);

        onView(withId(R.id.cart_list_list_view)).check(itemCountIs(0));
    }

    private ViewAssertion itemCountIs(int expectedCount) {
        return (view, noViewFoundException) -> {
            RecyclerView recyclerView = (RecyclerView) view;
            assertThat(recyclerView.getAdapter().getItemCount(), is(expectedCount));
        };
    }

    private static Matcher<View> listItem(int position) {
        return allOf(withParent(withId(R.id.cart_list_list_view)), withParentIndex(position));
    }

    private static Matcher<View> viewInListItem(int position, int id) {
        return allOf(withParent(listItem(position)), withId(id));
    }


}
