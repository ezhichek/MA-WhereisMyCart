package de.htwberlin.whereismycart.activities;

import static android.location.LocationManager.GPS_PROVIDER;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.common.collect.Sets;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import de.htwberlin.whereismycart.CartApplication;
import de.htwberlin.whereismycart.R;
import de.htwberlin.whereismycart.cart.Cart;
import de.htwberlin.whereismycart.cart.CartRepository;
import de.htwberlin.whereismycart.cart.CartStatus;
import de.htwberlin.whereismycart.location.Coordinates;
import de.htwberlin.whereismycart.store.Store;
import de.htwberlin.whereismycart.util.TestUtils;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddCartActivityTest {

    @Rule
    public ActivityScenarioRule<AddCartActivity> activityRule = new ActivityScenarioRule<>(AddCartActivity.class);

    @Test
    public void testAddCartWorks() {

        final CartApplication application = ApplicationProvider.getApplicationContext();

        application.getStoreManager().setSelectedStore(Store.LIDL);

        final CartRepository repository = application.getDatabase().cartRepository();

        final Set<UUID> oldCartIds = repository.findAllCarts()
                .stream()
                .map(Cart::getId)
                .collect(Collectors.toSet());

        onView(withId(R.id.add_cart_add_cart_button)).check(matches(isDisplayed()));
        onView(withId(R.id.add_cart_add_cart_button)).perform(click());

        final Set<UUID> newCartIds = application.getDatabase().cartRepository()
                .findAllCarts()
                .stream()
                .map(Cart::getId)
                .collect(Collectors.toSet());

        Assert.assertEquals(newCartIds.size(), oldCartIds.size() + 1);

        final Sets.SetView<UUID> diff = Sets.difference(newCartIds, oldCartIds);

        final UUID newCartId = diff.stream().findFirst().get();

        final Cart newCart = repository.findById(newCartId);

        Assert.assertEquals(Store.LIDL, newCart.getStore());
        Assert.assertEquals(CartStatus.NEW, newCart.getStatus());
    }

    @Test
    public void testCancelAddCartWorks() {

        final CartApplication application = ApplicationProvider.getApplicationContext();

        application.getStoreManager().setSelectedStore(Store.LIDL);

        final Set<UUID> oldCartIds = application.getDatabase().cartRepository()
                .findAllCarts()
                .stream()
                .map(Cart::getId)
                .collect(Collectors.toSet());

        onView(withId(R.id.add_cart_cancel_button)).check(matches(isDisplayed()));
        onView(withId(R.id.add_cart_cancel_button)).perform(click());

        final Set<UUID> newCartIds = application.getDatabase().cartRepository()
                .findAllCarts()
                .stream()
                .map(Cart::getId)
                .collect(Collectors.toSet());

        Assert.assertEquals(newCartIds.size(), oldCartIds.size());
    }

}