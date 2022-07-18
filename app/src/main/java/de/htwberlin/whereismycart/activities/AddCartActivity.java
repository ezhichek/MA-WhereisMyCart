package de.htwberlin.whereismycart.activities;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.htwberlin.whereismycart.CartApplication;
import de.htwberlin.whereismycart.R;
import de.htwberlin.whereismycart.cart.CartManager;
import de.htwberlin.whereismycart.databinding.ActivityAddCartBinding;
import de.htwberlin.whereismycart.location.Coordinates;
import de.htwberlin.whereismycart.location.LocationListenerAdapter;
import de.htwberlin.whereismycart.location.LocationUtils;
import de.htwberlin.whereismycart.store.Store;
import de.htwberlin.whereismycart.store.StoreManager;

public class AddCartActivity extends FragmentActivity {

    private StoreManager storeManager;

    private CartManager cartManager;

    private FusedLocationProviderClient locationProviderClient;

    private LocationCallback locationCallback;

    private Location location;

    private GoogleMap googleMap;

    private Marker locationMarker;

    private OnLocationChangedListener onLocationChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityAddCartBinding binding = ActivityAddCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);

        final CartApplication application = (CartApplication)getApplication();

        storeManager = application.getStoreManager();
        cartManager = application.getCartManager();

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationListenerAdapter(this::onLocationChanged);

        findViewById(R.id.add_cart_add_cart_button).setOnClickListener(this::onAddCart);
        findViewById(R.id.add_cart_cancel_button).setOnClickListener(this::onCancelAddCart);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationUtils.subscribeForLocationUpdates(locationProviderClient, locationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void onLocationChanged(@NonNull Location location) {

        this.location = location;

        if (onLocationChangedListener != null) {
            onLocationChangedListener.onLocationChanged(location);
        }

        if (locationMarker != null) {
            locationMarker.remove();
        }

        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        locationMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(storeManager.getSelectedStore().getDisplayName() + " cart")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    @SuppressLint("MissingPermission")
    private void onMapReady(GoogleMap map) {

        googleMap = map;

        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        googleMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(@NonNull OnLocationChangedListener onLocationChangedListener) {
                AddCartActivity.this.onLocationChangedListener = onLocationChangedListener;
            }

            @Override
            public void deactivate() {
                AddCartActivity.this.onLocationChangedListener = null;
            }
        });

        googleMap.setMyLocationEnabled(true);
    }

    private void onAddCart(View view) {
        if (location != null) {
            final Store store = storeManager.getSelectedStore();
            cartManager.addNewCart(store, new Coordinates(location));
            finish();
        } else {
            Toast.makeText(this, "Unable to determine current location", Toast.LENGTH_LONG).show();
        }
    }

    private void onCancelAddCart(View view) {
        finish();
    }


}