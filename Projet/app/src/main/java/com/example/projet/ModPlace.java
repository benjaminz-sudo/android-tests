package com.example.projet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ModPlace extends AppCompatActivity {
    private EditText etTitle, etDescription, etDate, etTime, etAddress, etPhone;
    private CheckBox cbVisited;
    private ImageView ivPlacePhoto;
    private Button btnSave, btnPickDate, btnPickTime, btnPickPhoto;
    private ImageButton btnShowMap, btnCall;
    private DatabaseHelper dbHelper;
    private long currentPlaceId = -1;
    private long tripId = -1;
    private boolean fromAdd;
    private String photoUriStr = null;
    private Trip currentTrip;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        photoUriStr = selectedImageUri.toString();
                        ivPlacePhoto.setImageURI(selectedImageUri);
                    }
                }
            }
    );

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

        ivPlacePhoto = findViewById(R.id.ivPlacePhoto);
        btnPickPhoto = findViewById(R.id.btnPickPhoto);
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
        btnShowMap = findViewById(R.id.btnShowMap);
        btnCall = findViewById(R.id.btnCall);

        btnPickPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnPickTime.setOnClickListener(v -> showTimePicker());
        btnSave.setOnClickListener(this::savePlace);
        btnShowMap.setOnClickListener(v -> openMap());
        btnCall.setOnClickListener(v -> makeCall());

        Intent intent = getIntent();
        fromAdd = intent.getBooleanExtra("fromAdd", false);
        tripId = intent.getLongExtra("trip_id", -1);
        
        if (tripId != -1) {
            currentTrip = dbHelper.getTripById(tripId);
        }

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
                photoUriStr = selectedPlace.getPhoto();
                if (photoUriStr != null) {
                    try {
                        ivPlacePhoto.setImageURI(Uri.parse(photoUriStr));
                    } catch (Exception e) {
                        // Handle case where URI is no longer accessible
                    }
                }
                btnSave.setText(R.string.update_place);
            }
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> 
            etDate.setText(String.format(Locale.getDefault(), "%02d-%02d-%04d", month + 1, dayOfMonth, year)),
            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> 
            etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
            c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void openMap() {
        String address = etAddress.getText().toString();
        if (!address.isEmpty()) {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    private void makeCall() {
        String phone = etPhone.getText().toString();
        if (!phone.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        }
    }

    public void savePlace(View v) {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dateStr = etDate.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || dateStr.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, R.string.error_mandatory_fields_place, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.isEmpty() && !Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, R.string.error_invalid_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentTrip != null) {
            try {
                Date placeDate = dateFormat.parse(dateStr);
                Date tripStart = dateFormat.parse(currentTrip.getStartDate());
                Date tripEnd = dateFormat.parse(currentTrip.getEndDate());

                if (placeDate != null && tripStart != null && tripEnd != null) {
                    if (placeDate.before(tripStart) || placeDate.after(tripEnd)) {
                        Toast.makeText(this, R.string.error_date_outside_trip, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Place place = new Place(currentPlaceId, title, description,
                dateStr, etTime.getText().toString(),
                address, phone,
                photoUriStr, cbVisited.isChecked());

        if (fromAdd) {
            dbHelper.add(place, tripId);
        } else {
            dbHelper.update(place);
        }
        finish();
    }
}
