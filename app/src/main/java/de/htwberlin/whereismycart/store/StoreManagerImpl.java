package de.htwberlin.whereismycart.store;

public class StoreManagerImpl implements StoreManager {

    private Store store;

    @Override
    public void setSelectedStore(Store store) {
        this.store = store;
    }

    @Override
    public Store getSelectedStore() {
        return store;
    }
}
