package de.htwberlin.whereismycart.cart;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.htwberlin.whereismycart.store.Store;
import de.htwberlin.whereismycart.settings.SettingsManager;
import de.htwberlin.whereismycart.location.Coordinates;

public class CartManagerImpl implements CartManager {

    private final CartRepository cartRepository;

    private final CartApi cartApi;

    private final SettingsManager settingsManager;

    public CartManagerImpl(CartRepository cartRepository, CartApi cartApi, SettingsManager settingsManager) {
        this.cartRepository = cartRepository;
        this.cartApi = cartApi;
        this.settingsManager = settingsManager;
    }

    @Override
    public List<Cart> findAllCartsInRadius(Store store, Coordinates coordinates, int radius) {
        return cartRepository.findAllNewCartsByStore(store)
                .stream()
                .filter(cart -> isCartInRadius(cart, coordinates, radius))
                .collect(Collectors.toList());
    }

    private boolean isCartInRadius(Cart cart, Coordinates coordinates, int radius) {
        final long distance = cart.getCoordinates().distanceTo(coordinates);
        return distance <= radius;
    }

    @Override
    public Cart addNewCart(Store store, Coordinates coordinates) {

        final long now = System.currentTimeMillis();

        final Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setStore(store);
        cart.setLongitude(coordinates.getLongitude());
        cart.setLatitude(coordinates.getLatitude());
        cart.setStatus(CartStatus.NEW);
        cart.setCreateTime(now);
        cart.setUpdateTime(now);

        cartRepository.insert(cart);
        cartApi.updateCart(cart);

        return cart;
    }

    @Override
    public void markCartAsCollected(UUID cartId) {
        final Cart cart = cartRepository.findById(cartId);
        if (cart != null) {
            cart.setStatus(CartStatus.COLLECTED);
            cart.setUpdateTime(System.currentTimeMillis());
            cartRepository.update(cart);
            cartApi.updateCart(cart);
        }
    }

    @Override
    public void markCartAsNotFound(UUID cartId) {
        final Cart cart = cartRepository.findById(cartId);
        if (cart != null) {
            cart.setStatus(CartStatus.NOT_FOUND);
            cart.setUpdateTime(System.currentTimeMillis());
            cartRepository.update(cart);
            cartApi.updateCart(cart);
        }
    }

    @Override
    public void refreshCarts(Runnable onComplete) {
        final Store store = settingsManager.findSettings().getStore();
        cartApi.findAllCartsByStore(store, updates -> {
            updates.forEach(update -> {
                Cart current = cartRepository.findById(update.getId());
                if (current == null) {
                    cartRepository.insert(update);
                } else if (canUpdate(current, update)) {
                    cartRepository.update(update);
                }
                onComplete.run();
            });
        });
    }

    private static boolean canUpdate(Cart current, Cart update) {
        return current.getStatus() == CartStatus.NEW && update.getStatus() != CartStatus.NEW;
    }
}


