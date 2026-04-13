package com.example.projet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TripDisplay extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView lvTrips;
    private SimpleCursorAdapter adapter;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Notifications are disabled. You won't receive trip reminders.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_display);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        dbHelper = new DatabaseHelper(this);
        dbHelper.open();

        lvTrips = findViewById(R.id.lvTrips);
        lvTrips.setEmptyView(findViewById(R.id.tvEmptyTrips));

        findViewById(R.id.btnAddTrip).setOnClickListener(v -> {
            Intent intent = new Intent(this, ModTrip.class);
            startActivity(intent);
        });

        lvTrips.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, PlaceDisplay.class);
            intent.putExtra("trip_id", id);
            startActivity(intent);
        });

        registerForContextMenu(lvTrips);
        chargeData();

        // Start Music Service if enabled
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(SettingsActivity.MUSIC_ENABLED, true)) {
            startService(new Intent(this, MusicService.class));
        }

        requestNotificationPermission();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargeData();
    }

    private void chargeData() {
        Cursor cursor = dbHelper.getAllTrips();
        String[] from = new String[]{DatabaseHelper.COL_ID, DatabaseHelper.COL_TRIP_CITY, DatabaseHelper.COL_TRIP_START};
        int[] to = new int[]{R.id.tvTripId, R.id.tvTripCity, R.id.tvTripDates}; 
        
        adapter = new SimpleCursorAdapter(this, R.layout.trip_item_view, cursor, from, to, 0);
        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            if (view.getId() == R.id.tvTripDates) {
                String start = cursor1.getString(cursor1.getColumnIndexOrThrow(DatabaseHelper.COL_TRIP_START));
                String end = cursor1.getString(cursor1.getColumnIndexOrThrow(DatabaseHelper.COL_TRIP_END));
                ((TextView) view).setText(start + " " + getString(R.string.to) + " " + end);
                return true;
            }
            return false;
        });
        lvTrips.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.delete) {
            dbHelper.deleteTrip(info.id);
            chargeData();
            Toast.makeText(this, R.string.trip_deleted, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
