package de.htwberlin.whereismycart.settings;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import android.content.SharedPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.htwberlin.whereismycart.store.Store;

public class SettingsManagerTest {

    SettingsManager settingsManager;

    SharedPreferences preferences;

    @Before
    public void setup() {
        preferences = mock(SharedPreferences.class);
        settingsManager = new SettingsManagerImpl(preferences);
    }

    @Test
    public void findSettingsWorks() {

        // given

        given(preferences.getString("store", "LIDL")).willReturn("REWE");
        given(preferences.getString("store_address", "")).willReturn("Berlin, 13189, Ostseestrasse 23");
        given(preferences.getInt("radius", 1000)).willReturn(200);

        // when

        Settings settings = settingsManager.findSettings();

        // then

        Assert.assertEquals(Store.REWE, settings.getStore());
        Assert.assertEquals("Berlin, 13189, Ostseestrasse 23", settings.getStoreAddress());
        Assert.assertEquals(200, settings.getRadius());
    }
}
