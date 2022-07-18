package de.htwberlin.whereismycart.activities;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

import de.htwberlin.whereismycart.CartApplication;
import de.htwberlin.whereismycart.R;
import de.htwberlin.whereismycart.activities.CartListItemAdapter.ItemClickListener;
import de.htwberlin.whereismycart.cart.Cart;
import de.htwberlin.whereismycart.cart.CartManager;
import de.htwberlin.whereismycart.location.Coordinates;
import de.htwberlin.whereismycart.location.LocationListenerAdapter;
import de.htwberlin.whereismycart.location.LocationUtils;
import de.htwberlin.whereismycart.settings.Settings;
import de.htwberlin.whereismycart.settings.SettingsManager;

public class CartListActivity extends AppCompatActivity {

    private CartManager cartManager;

    private SettingsManager settingsManager;

    private FusedLocationProviderClient locationProviderClient;

    private LocationCallback locationCallback;

    private Geocoder geocoder;

    private CartListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cart_list);

        final CartApplication application = (CartApplication)getApplication();

        cartManager = application.getCartManager();
        settingsManager = application.getSettingsManager();

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationListenerAdapter(this::onLocationChanged);

        geocoder = new Geocoder(this);

        findViewById(R.id.cart_list_settings_button).setOnClickListener(this::onSettingsClicked);
        findViewById(R.id.cart_list_refresh_button).setOnClickListener(this::onRefreshClicked);

        adapter = new CartListItemAdapter(this, new ItemClickListener() {
            @Override
            public void onMapClicked(Cart cart, int index) {
                final Coordinates target = cart.getCoordinates();
                final String uri = "geo:0,0?q=" + target.getLatitude() + "," + target.getLongitude();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            }

            @Override
            public void onConfirmClicked(Cart cart, int index) {
                cartManager.markCartAsCollected(cart.getId());
                adapter.remove(index);
            }

            @Override
            public void onDeleteClicked(Cart cart, int index) {
                cartManager.markCartAsNotFound(cart.getId());
                adapter.remove(index);
            }
        });

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.cart_list_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationUtils.subscribeForLocationUpdates(locationProviderClient, locationCallback);
        new FetchCartListModelTask(cartManager, settingsManager, geocoder, adapter).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void onSettingsClicked(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void onRefreshClicked(View view) {
        cartManager.refreshCarts(() ->
                new FetchCartListModelTask(cartManager, settingsManager, geocoder, adapter).execute());
    }

    private void onLocationChanged(@NonNull Location location) {
        adapter.setLocation(new Coordinates(location));
    }

    private static class FetchCartListModelTask extends AsyncTask<Void, Void, List<Cart>> {

        private final CartManager cartManager;

        private final SettingsManager settingsManager;

        private final Geocoder geocoder;

        private final CartListItemAdapter adapter;

        FetchCartListModelTask(CartManager cartManager,
                               SettingsManager settingsManager,
                               Geocoder geocoder,
                               CartListItemAdapter adapter) {
            super();
            this.cartManager = cartManager;
            this.settingsManager = settingsManager;
            this.geocoder = geocoder;
            this.adapter = adapter;
        }

        @Override
        protected List<Cart> doInBackground(Void... voids) {
            final Settings settings = settingsManager.findSettings();
            if (StringUtils.isNotBlank(settings.getStoreAddress())) {
                final Coordinates storeCoordinates = LocationUtils.findCoordinatesByAddress(geocoder, settings.getStoreAddress());
                if (storeCoordinates != null) {
                    return cartManager.findAllCartsInRadius(settings.getStore(), storeCoordinates, settings.getRadius());
                }
            }
            return Collections.emptyList();
        }

        @Override
        protected void onPostExecute(List<Cart> carts) {
            adapter.setCarts(carts);
        }
    }

}