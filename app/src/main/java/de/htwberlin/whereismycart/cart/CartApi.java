package de.htwberlin.whereismycart.cart;

import java.util.List;
import java.util.function.Consumer;

import de.htwberlin.whereismycart.store.Store;

public interface CartApi {

    void findAllCartsByStore(Store store, Consumer<List<Cart>> consumer);

    void updateCart(Cart cart);

}
