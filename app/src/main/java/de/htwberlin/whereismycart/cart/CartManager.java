package de.htwberlin.whereismycart.cart;

import java.util.List;
import java.util.UUID;

import de.htwberlin.whereismycart.store.Store;
import de.htwberlin.whereismycart.location.Coordinates;

public interface CartManager {

    List<Cart> findAllCartsInRadius(Store store, Coordinates coordinates, int radius);

    Cart addNewCart(Store store, Coordinates coordinates);

    void markCartAsCollected(UUID cartId);

    void markCartAsNotFound(UUID cartId);

    void refreshCarts(Runnable onComplete);

}
