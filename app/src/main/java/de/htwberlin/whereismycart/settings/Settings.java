package de.htwberlin.whereismycart.settings;

import de.htwberlin.whereismycart.store.Store;

public class Settings {

    private final Store store;

    private final String storeAddress;

    private final int radius;

    public Settings(Store store, String storeAddress, int radius) {
        this.store = store;
        this.storeAddress = storeAddress;
        this.radius = radius;
    }

    public Store getStore() {
        return store;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public int getRadius() {
        return radius;
    }
}
