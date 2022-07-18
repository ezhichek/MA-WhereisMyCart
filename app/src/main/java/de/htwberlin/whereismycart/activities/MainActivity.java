package de.htwberlin.whereismycart.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.appcompat.app.AppCompatActivity;

import de.htwberlin.whereismycart.R;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> requestPermissionLauncher = createLauncher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_user_button).setOnClickListener(this::onSelectUserMode);
        findViewById(R.id.main_employee_button).setOnClickListener(this::onSelectEmployeeMode);
    }

    private ActivityResultLauncher<String> createLauncher() {
        return registerForActivityResult(new RequestPermission(), isGranted -> {
            if (isGranted) {
                startActivity(new Intent(this, CartListActivity.class));
            } else {
                Toast.makeText(this, "Location access denied", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onSelectUserMode(View view) {
        Intent intent = new Intent(this, SelectStoreActivity.class);
        startActivity(intent);
    }

    private void onSelectEmployeeMode(View view) {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

}