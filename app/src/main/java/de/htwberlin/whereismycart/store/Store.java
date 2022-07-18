package de.htwberlin.whereismycart.store;

public enum Store {
    LIDL("Lidl"),
    EDEKA("Edeka"),
    REWE("Rewe"),
    ALDI("Aldi");

    private final String displayName;

    Store(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
