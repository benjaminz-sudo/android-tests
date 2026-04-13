package com.example.projet;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ModTrip extends AppCompatActivity {
    private EditText etCity, etStartDate, etEndDate;
    private Button btnSaveTrip, btnPickStartDate, btnPickEndDate;
    private DatabaseHelper dbHelper;
    private long currentTripId = -1;
    private boolean fromAdd = true;

    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mod_trip);

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

        etCity = findViewById(R.id.etCity);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        btnSaveTrip = findViewById(R.id.btnSaveTrip);
        btnPickStartDate = findViewById(R.id.btnPickStartDate);
        btnPickEndDate = findViewById(R.id.btnPickEndDate);

        startDateListener = (view, year, month, dayOfMonth) -> 
            etStartDate.setText(String.format(Locale.getDefault(), "%02d-%02d-%04d", month + 1, dayOfMonth, year));

        endDateListener = (view, year, month, dayOfMonth) -> 
            etEndDate.setText(String.format(Locale.getDefault(), "%02d-%02d-%04d", month + 1, dayOfMonth, year));

        btnPickStartDate.setOnClickListener(v -> showDatePicker(startDateListener));
        btnPickEndDate.setOnClickListener(v -> showDatePicker(endDateListener));

        btnSaveTrip.setOnClickListener(v -> saveTrip());

        Intent intent = getIntent();
        if (intent.hasExtra("SelectedTrip")) {
            Trip trip = intent.getParcelableExtra("SelectedTrip");
            if (trip != null) {
                fromAdd = false;
                currentTripId = trip.getId();
                etCity.setText(trip.getCity());
                etStartDate.setText(trip.getStartDate());
                etEndDate.setText(trip.getEndDate());
                btnSaveTrip.setText(R.string.update_trip);

                checkIfModifiable();
            }
        }
    }

    private void checkIfModifiable() {
        Cursor cursor = dbHelper.getPlacesByTrip(currentTripId);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                etCity.setEnabled(false);
                etStartDate.setEnabled(false);
                etEndDate.setEnabled(false);
                btnPickStartDate.setEnabled(false);
                btnPickEndDate.setEnabled(false);
                btnSaveTrip.setVisibility(View.GONE);
                Toast.makeText(this, R.string.trip_locked, Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
    }

    private void showDatePicker(DatePickerDialog.OnDateSetListener listener) {
        DatePickerFragment dateFragment = new DatePickerFragment();
        Calendar c = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", c.get(Calendar.YEAR));
        args.putInt("month", c.get(Calendar.MONTH));
        args.putInt("day", c.get(Calendar.DAY_OF_MONTH));
        dateFragment.setArguments(args);
        dateFragment.setCallBack(listener);
        dateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void saveTrip() {
        String city = etCity.getText().toString().trim();
        String start = etStartDate.getText().toString().trim();
        String end = etEndDate.getText().toString().trim();

        if (city.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date startDate = dateFormat.parse(start);
            Date endDate = dateFormat.parse(end);
            if (startDate != null && endDate != null && startDate.after(endDate)) {
                Toast.makeText(this, R.string.error_invalid_dates, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        Trip trip = new Trip(currentTripId, city, start, end);
        long tripId;
        if (fromAdd) {
            tripId = dbHelper.addTrip(trip);
            Toast.makeText(this, R.string.trip_added, Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updateTrip(trip);
            tripId = currentTripId;
            Toast.makeText(this, R.string.trip_updated, Toast.LENGTH_SHORT).show();
        }

        scheduleNotification(city, start, tripId);
        finish();
    }

    private void scheduleNotification(String city, String startDateStr, long tripId) {
        try {
            Date startDate = dateFormat.parse(startDateStr);
            if (startDate == null) return;

            // If the trip has already started, no need for a reminder
            if (startDate.getTime() < System.currentTimeMillis()) {
                return;
            }

            SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
            int daysBefore = prefs.getInt(SettingsActivity.NOTIF_PREF, SettingsActivity.NOTIF_2_DAYS);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_YEAR, -daysBefore);
            calendar.set(Calendar.HOUR_OF_DAY, 9); // Notify at 9 AM
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long triggerTime = calendar.getTimeInMillis();

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("city_name", city);
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) tripId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
