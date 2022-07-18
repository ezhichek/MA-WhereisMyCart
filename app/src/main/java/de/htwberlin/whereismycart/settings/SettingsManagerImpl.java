package de.htwberlin.whereismycart.settings;

import android.content.SharedPreferences;

import java.util.Optional;

import de.htwberlin.whereismycart.store.Store;

public class SettingsManagerImpl implements SettingsManager {

    public static final String KEY_STORE = "store";
    public static final String KEY_STORE_ADDRESS = "store_address";
    public static final String KEY_RADIUS = "radius";

    private final SharedPreferences preferences;

    public SettingsManagerImpl(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Settings findSettings() {
        return new Settings(
                Store.valueOf(preferences.getString(KEY_STORE, "LIDL")),
                preferences.getString(KEY_STORE_ADDRESS, ""),
                preferences.getInt(KEY_RADIUS, 1000)
        );
    }

    @Override
    public void updateStore(Store store) {
        preferences.edit().putString(KEY_STORE, Optional.ofNullable(store).map(Store::name).orElse(null)).apply();
    }

    @Override
    public void updateStoreAddress(String address) {
        preferences.edit().putString(KEY_STORE_ADDRESS, address).apply();
    }

    @Override
    public void updateRadius(int radius) {
        preferences.edit().putInt(KEY_RADIUS, radius).apply();
    }


}
