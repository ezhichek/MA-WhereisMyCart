package de.htwberlin.whereismycart.location;

import android.annotation.SuppressLint;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Executors;

public class LocationUtils {

    public static Coordinates findCoordinatesByAddress(Geocoder geocoder, String address) {
        try {
            return geocoder.getFromLocationName(address, 1).stream()
                    .findFirst()
                    .map(a -> new Coordinates(a.getLongitude(), a.getLatitude()))
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    public static void subscribeForLocationUpdates(FusedLocationProviderClient client, LocationCallback callback) {

        final LocationRequest request = LocationRequest.create();
        request.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
        request.setFastestInterval(1000);

        client.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                callback.onLocationResult(LocationResult.create(Collections.singletonList(location)));
            }
            client.requestLocationUpdates(request, callback, Looper.getMainLooper());
        });
    }
}
