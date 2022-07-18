package de.htwberlin.whereismycart.location;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationResult;

public class LocationListenerAdapter extends LocationCallback {

    private final LocationListener listener;

    public LocationListenerAdapter(LocationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onLocationResult(@NonNull LocationResult locationResult) {
        if (locationResult.getLastLocation() != null) {
            listener.onLocationChanged(locationResult.getLastLocation());
        }
    }
}
