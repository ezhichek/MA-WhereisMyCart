package de.htwberlin.whereismycart.cart;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

import de.htwberlin.whereismycart.store.Store;

@Dao
public interface CartRepository {

    @Query("select * from carts where id = :id")
    Cart findById(UUID id);

    @Query("select * from carts")
    List<Cart> findAllCarts();

    @Query("select * from carts where store = :store and status = 'NEW'")
    List<Cart> findAllNewCartsByStore(Store store);

    @Query("delete from carts")
    void deleteAllCarts();

    @Insert
    void insert(Cart cart);

    @Update
    void update(Cart cart);

}
