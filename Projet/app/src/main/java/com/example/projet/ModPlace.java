package com.example.projet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class ModPlace extends AppCompatActivity {
    int year, month, day;
    int hour, minute;
    boolean fromAdd;
    DatePickerDialog.OnDateSetListener onDate;
    TimePickerDialog.OnTimeSetListener onTime;
    private EditText etTitle, etDescription, etDate, etTime, etAddress, etPhone;
    private CheckBox cbVisited;
    private Button btnSave, btnPickDate, btnPickTime;
    private DatabaseHelper dbHelper;
    private long currentPlaceId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mod_place);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        dbHelper.open();

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        cbVisited = findViewById(R.id.cbVisited);
        btnSave = findViewById(R.id.btnSave);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnPickTime.setOnClickListener(v -> showTimePicker());

        btnSave.setOnClickListener(this::savePlace);

        onDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;
                etDate.setText(String.format(Locale.getDefault(), "%02d-%02d-%04d", month + 1, day, year));
            }
        };

        onTime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int selectedMinute) {
                hour = hourOfDay;
                minute = selectedMinute;
                etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        Intent intent = getIntent();
        fromAdd = intent.getBooleanExtra("fromAdd", false);

        if (!fromAdd) {
            Place selectedPlace = intent.getParcelableExtra("SelectedPlace");
            if (selectedPlace != null) {
                currentPlaceId = selectedPlace.getId();
                etTitle.setText(selectedPlace.getTitle());
                etDescription.setText(selectedPlace.getDescription());
                etDate.setText(selectedPlace.getDate());
                etTime.setText(selectedPlace.getTime());
                etAddress.setText(selectedPlace.getAddress());
                etPhone.setText(selectedPlace.getPhone());
                cbVisited.setChecked(selectedPlace.isVisited());
                btnSave.setText("Update Place");
            }
        }
    }

    private void showDatePicker() {
        DatePickerFragment dateFragment = new DatePickerFragment();
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        dateFragment.setArguments(args);
        dateFragment.setCallBack(onDate);
        dateFragment.show(getSupportFragmentManager(), "Date Picker");
    }

    private void showTimePicker() {
        TimePickerFragment timeFragment = new TimePickerFragment();
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        timeFragment.setArguments(args);
        timeFragment.setCallBack(onTime);
        timeFragment.show(getSupportFragmentManager(), "Time Picker");
    }

    public void savePlace(View v) {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        boolean visited = cbVisited.isChecked();

        if (title.isEmpty() || date.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all mandatory fields (Title, Date, Address)", Toast.LENGTH_SHORT).show();
            return;
        }

        Place place = new Place(currentPlaceId, title, description, date, time, address, phone, visited);

        if (fromAdd) {
            dbHelper.add(place);
            Toast.makeText(this, "Place saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.update(place);
            Toast.makeText(this, "Place updated successfully", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
