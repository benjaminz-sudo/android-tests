package com.example.tp2_1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        ImageView photoView = findViewById(R.id.detailPhoto);
        TextView nameView = findViewById(R.id.detailName);
        RadioGroup genderGroup = findViewById(R.id.detailGenderGroup);
        RadioButton radioMale = findViewById(R.id.radioMale);
        RadioButton radioFemale = findViewById(R.id.radioFemale);
        DatePicker birthDatePicker = findViewById(R.id.detailBirthDatePicker);
        TextView nationalityView = findViewById(R.id.detailNationality);
        Spinner domainSpinner = findViewById(R.id.detailDomainSpinner);

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.domains_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        domainSpinner.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");
            int photo = intent.getIntExtra("photo", R.drawable.user);
            String gender = intent.getStringExtra("gender");
            String birthDate = intent.getStringExtra("birthDate");
            String nationality = intent.getStringExtra("nationality");
            String domain = intent.getStringExtra("domain");

            nameView.setText(firstName + " " + lastName);
            photoView.setImageResource(photo);
            
            if ("Male".equalsIgnoreCase(gender)) {
                radioMale.setChecked(true);
            } else if ("Female".equalsIgnoreCase(gender)) {
                radioFemale.setChecked(true);
            }

            if (birthDate != null && birthDate.contains("/")) {
                String[] parts = birthDate.split("/");
                if (parts.length == 3) {
                    try {
                        int day = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]) - 1; // Calendar months are 0-based
                        int year = Integer.parseInt(parts[2]);
                        birthDatePicker.updateDate(year, month, day);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            nationalityView.setText("Nationality: " + nationality);
            
            if (domain != null) {
                int spinnerPosition = adapter.getPosition(domain);
                domainSpinner.setSelection(spinnerPosition);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
