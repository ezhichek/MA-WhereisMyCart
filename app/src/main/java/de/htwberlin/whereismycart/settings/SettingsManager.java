package de.htwberlin.whereismycart.settings;

import de.htwberlin.whereismycart.store.Store;

public interface SettingsManager {

    Settings findSettings();

    void updateStore(Store store);

    void updateStoreAddress(String address);

    void updateRadius(int radius);
}
