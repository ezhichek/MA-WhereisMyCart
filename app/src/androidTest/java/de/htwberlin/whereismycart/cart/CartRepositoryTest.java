package de.htwberlin.whereismycart.cart;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import de.htwberlin.whereismycart.AppDatabase;
import de.htwberlin.whereismycart.store.Store;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CartRepositoryTest {

    private static final UUID ID = UUID.fromString("bf9d4623-d0f9-42b9-95dd-720ae9d35274");

    private static final long NOW = LocalDateTime.of(2022, 6, 11, 17, 5).atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

    private CartRepository cartRepository;

    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        cartRepository = db.cartRepository();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void testInsertWorks() {

        final Cart cart = createCart();

        cartRepository.insert(cart);

        final Cart result = cartRepository.findById(ID);

        Assert.assertNotNull(result);

        Assert.assertEquals(ID, result.getId());
        Assert.assertEquals(Store.REWE, result.getStore());
        Assert.assertEquals(52.5508595, result.getLatitude(), 0.000001);
        Assert.assertEquals(13.4302034, result.getLongitude(), 0.000001);
        Assert.assertEquals(CartStatus.NEW, result.getStatus());
        Assert.assertEquals(NOW, result.getCreateTime());
        Assert.assertEquals(NOW, result.getUpdateTime());

        // 2nd insert fails
        cartRepository.insert(cart);
    }

    @Test
    public void testUpdateWorks() {

        final Cart cart = createCart();

        cartRepository.insert(cart);

        final Cart result1 = cartRepository.findById(ID);

        Assert.assertNotNull(result1);

        Assert.assertEquals(ID, result1.getId());
        Assert.assertEquals(Store.REWE, result1.getStore());
        Assert.assertEquals(52.5508595, result1.getLatitude(), 0.000001);
        Assert.assertEquals(13.4302034, result1.getLongitude(), 0.000001);
        Assert.assertEquals(CartStatus.NEW, result1.getStatus());
        Assert.assertEquals(NOW, result1.getCreateTime());
        Assert.assertEquals(NOW, result1.getUpdateTime());

        final long now = System.currentTimeMillis();

        result1.setStatus(CartStatus.COLLECTED);
        result1.setUpdateTime(now);

        cartRepository.update(result1);

        final Cart result2 = cartRepository.findById(ID);

        Assert.assertNotNull(result2);

        Assert.assertEquals(ID, result2.getId());
        Assert.assertEquals(Store.REWE, result2.getStore());
        Assert.assertEquals(52.5508595, result2.getLatitude(), 0.000001);
        Assert.assertEquals(13.4302034, result2.getLongitude(), 0.000001);
        Assert.assertEquals(CartStatus.COLLECTED, result2.getStatus());
        Assert.assertEquals(NOW, result2.getCreateTime());
        Assert.assertEquals(now, result2.getUpdateTime());
    }

    @Test
    public void testFindAllNewWorks() {

        final Cart cart1 = createCart();
        cart1.setId(UUID.randomUUID());

        final Cart cart2 = createCart();
        cart2.setId(UUID.randomUUID());
        cart2.setStatus(CartStatus.COLLECTED);

        final Cart cart3 = createCart();
        cart3.setId(UUID.randomUUID());
        cart3.setStore(Store.LIDL);

        cartRepository.insert(cart1);
        cartRepository.insert(cart2);
        cartRepository.insert(cart3);

        final List<Cart> newCarts = cartRepository.findAllNewCartsByStore(Store.REWE);

        Assert.assertEquals(1, newCarts.size());

        final List<Cart> allCarts = cartRepository.findAllCarts();

        Assert.assertEquals(3, allCarts.size());
    }

    private static Cart createCart() {
        final Cart cart = new Cart();
        cart.setId(ID);
        cart.setStore(Store.REWE);
        cart.setLatitude(52.5508595);
        cart.setLongitude(13.4302034);
        cart.setStatus(CartStatus.NEW);
        cart.setCreateTime(NOW);
        cart.setUpdateTime(NOW);
        return cart;
    }
}