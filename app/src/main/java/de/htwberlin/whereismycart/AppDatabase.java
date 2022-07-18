package de.htwberlin.whereismycart;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import de.htwberlin.whereismycart.cart.Cart;
import de.htwberlin.whereismycart.cart.CartRepository;

@Database(entities = {Cart.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public static final String NAME = "cartdb";

    public abstract CartRepository cartRepository();

}
