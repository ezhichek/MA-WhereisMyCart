package de.htwberlin.whereismycart;

import android.app.Application;
import android.preference.PreferenceManager;

import androidx.room.Room;

import com.google.firebase.firestore.FirebaseFirestore;

import de.htwberlin.whereismycart.cart.CartApiImpl;
import de.htwberlin.whereismycart.cart.CartManager;
import de.htwberlin.whereismycart.cart.CartManagerImpl;
import de.htwberlin.whereismycart.store.StoreManager;
import de.htwberlin.whereismycart.store.StoreManagerImpl;
import de.htwberlin.whereismycart.settings.SettingsManager;
import de.htwberlin.whereismycart.settings.SettingsManagerImpl;

public class CartApplication extends Application {

    private AppDatabase database;

    private SettingsManager settingsManager;

    private StoreManager storeManager;

    private CartManager cartManager;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseFirestore.setLoggingEnabled(true);

        database = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration() // delete after development
                .build();

        settingsManager = new SettingsManagerImpl(PreferenceManager.getDefaultSharedPreferences(this));

        storeManager = new StoreManagerImpl();

        cartManager = new CartManagerImpl(database.cartRepository(), new CartApiImpl(), settingsManager);
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public CartManager getCartManager() {
        return cartManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public StoreManager getStoreManager() {
        return storeManager;
    }

}
