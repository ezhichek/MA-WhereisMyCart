package de.htwberlin.whereismycart.cart;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import de.htwberlin.whereismycart.location.Coordinates;
import de.htwberlin.whereismycart.store.Store;
import de.htwberlin.whereismycart.settings.Settings;
import de.htwberlin.whereismycart.settings.SettingsManager;

public class CartManagerTest {

    private static final UUID ID = UUID.fromString("bf9d4623-d0f9-42b9-95dd-720ae9d35274");

    private static final long NOW = LocalDateTime.of(2022, 6, 11, 17, 5).atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

    CartManager cartManager;

    CartRepository cartRepository;

    CartApi cartApi;

    SettingsManager settingsManager;

    @Before
    public void setup() {
        cartRepository = mock(CartRepository.class);
        cartApi = mock(CartApi.class);
        settingsManager = mock(SettingsManager.class);
        cartManager = new CartManagerImpl(cartRepository, cartApi, settingsManager);
    }

    @Test
    public void addNewCartWorks() {

        // when

        cartManager.addNewCart(Store.REWE, new Coordinates(13.4302034, 52.5508595));

        // then

        verify(cartRepository, times(1)).insert(argThat((Cart cart) -> {
            Assert.assertEquals(13.4302034, cart.getLongitude(), 0.000001);
            Assert.assertEquals(52.5508595, cart.getLatitude(), 0.000001);
            Assert.assertEquals(Store.REWE, cart.getStore());
            Assert.assertEquals(CartStatus.NEW, cart.getStatus());
            return true;
        }));
    }

    @Test
    public void markCartAsCollectedWorks() {

        // given

        Cart cart = createCart();

        given(cartRepository.findById(ID)).willReturn(cart);

        // when

        cartManager.markCartAsCollected(ID);

        // then

        verify(cartRepository, times(1)).update(argThat((Cart result) -> {
            Assert.assertEquals(ID, result.getId());
            Assert.assertEquals(Store.REWE, result.getStore());
            Assert.assertEquals(52.5508595, result.getLatitude(), 0.000001);
            Assert.assertEquals(13.4302034, result.getLongitude(), 0.000001);
            Assert.assertEquals(CartStatus.COLLECTED, result.getStatus());
            Assert.assertEquals(NOW, result.getCreateTime());
            Assert.assertNotEquals(NOW, result.getUpdateTime());
            return true;
        }));
    }

    @Test
    public void markCartAsNotFoundWorks() {

        // given

        Cart cart = createCart();

        given(cartRepository.findById(ID)).willReturn(cart);

        // when

        cartManager.markCartAsNotFound(ID);

        // then

        verify(cartRepository, times(1)).update(argThat((Cart result) -> {
            Assert.assertEquals(ID, result.getId());
            Assert.assertEquals(Store.REWE, result.getStore());
            Assert.assertEquals(52.5508595, result.getLatitude(), 0.000001);
            Assert.assertEquals(13.4302034, result.getLongitude(), 0.000001);
            Assert.assertEquals(CartStatus.NOT_FOUND, result.getStatus());
            Assert.assertEquals(NOW, result.getCreateTime());
            Assert.assertNotEquals(NOW, result.getUpdateTime());
            return true;
        }));
    }

    @Test
    public void refreshCartsWorks() {

        // given

        Cart cart = createCart();

        Settings settings = new Settings(Store.REWE, "some address", 100);

        doAnswer(ans -> {
            ans.getArgument(1, Consumer.class).accept(Collections.singletonList(cart));
            return null;
        }).when(cartApi).findAllCartsByStore(eq(Store.REWE), any(Consumer.class));

        given(settingsManager.findSettings()).willReturn(settings);

        Runnable runnable = mock(Runnable.class);

        // when

        cartManager.refreshCarts(runnable);

        // then

        verify(cartRepository, times(1)).findById(cart.getId());

        verify(cartRepository, times(1)).insert(argThat((Cart result) -> {
            Assert.assertEquals(ID, result.getId());
            Assert.assertEquals(Store.REWE, result.getStore());
            Assert.assertEquals(52.5508595, result.getLatitude(), 0.000001);
            Assert.assertEquals(13.4302034, result.getLongitude(), 0.000001);
            Assert.assertEquals(CartStatus.NEW, result.getStatus());
            Assert.assertEquals(NOW, result.getCreateTime());
            Assert.assertEquals(NOW, result.getUpdateTime());
            return true;
        }));

        verify(runnable, times(1)).run();
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