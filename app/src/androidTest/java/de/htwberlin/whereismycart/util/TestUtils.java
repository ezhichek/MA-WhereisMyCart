package de.htwberlin.whereismycart.util;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.location.Location;
import android.os.SystemClock;

import de.htwberlin.whereismycart.CartApplication;
import de.htwberlin.whereismycart.settings.SettingsManager;
import de.htwberlin.whereismycart.store.Store;

public class TestUtils {

    public static Location createMockLocation(String provider) {
        final Location location = new Location("Prenzlauer Promenade 191");
        location.setLatitude(52.551970);
        location.setLongitude(13.430252);
        location.setAccuracy(1);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        location.setProvider(provider);
        return location;
    }

    public static void resetSettings() {
        final CartApplication application = getApplicationContext();
        final SettingsManager settingsManager = application.getSettingsManager();
        settingsManager.updateStore(Store.LIDL);
        settingsManager.updateStoreAddress("Roelckestraße 172, 13086 Berlin");
        settingsManager.updateRadius(3000);
    }

}
