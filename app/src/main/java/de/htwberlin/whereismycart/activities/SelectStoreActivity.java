package de.htwberlin.whereismycart.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import de.htwberlin.whereismycart.CartApplication;
import de.htwberlin.whereismycart.R;
import de.htwberlin.whereismycart.store.Store;
import de.htwberlin.whereismycart.store.StoreManager;

public class SelectStoreActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> requestPermissionLauncher = createLauncher();

    private StoreManager storeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store);

        storeManager = ((CartApplication)getApplication()).getStoreManager();

        findViewById(R.id.select_store_lidl_button).setOnClickListener(view -> onSelectStore(view, Store.LIDL));
        findViewById(R.id.select_store_edeka_button).setOnClickListener(view -> onSelectStore(view, Store.EDEKA));
        findViewById(R.id.select_store_rewe_button).setOnClickListener(view -> onSelectStore(view, Store.REWE));
        findViewById(R.id.select_store_aldi_button).setOnClickListener(view -> onSelectStore(view, Store.ALDI));
    }

    private ActivityResultLauncher<String> createLauncher() {
        return registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                startActivity(new Intent(this, AddCartActivity.class));
            } else {
                Toast.makeText(this, "Location access denied", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onSelectStore(View view, Store store) {
        storeManager.setSelectedStore(store);
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }


}