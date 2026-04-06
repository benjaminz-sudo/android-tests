package com.example.tp3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CelebMod extends AppCompatActivity {

    private EditText etFirstName, etLastName, etDate, etNationality;
    private Spinner spDomain;
    private RadioGroup rgGender;
    private TextView tvId;
    private Button btnSave;
    private DatabaseHelper myHelper;
    private boolean fromAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celeb_details);

        // Initialize Views
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etDate = findViewById(R.id.etDate);
        etNationality = findViewById(R.id.etNationality);
        spDomain = findViewById(R.id.spDomain);
        rgGender = findViewById(R.id.rgGender);
        tvId = findViewById(R.id.tvId);

        // Setup Domain Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDomain.setAdapter(adapter);
        myHelper = new DatabaseHelper(this);
        myHelper.open();
        Intent intent = getIntent();
        fromAdd= intent.getBooleanExtra("fromAdd",false);
        if(!fromAdd){
            Bundle b= intent.getExtras();
            User selectedUser= b.getParcelable("SelectedUser");
            tvId.setText(String.valueOf(selectedUser.getId()));
            etFirstName.setText(selectedUser.getFirstName());
            etLastName.setText(selectedUser.getLastName());
            etDate.setText(selectedUser.getBirthDate());
            etNationality.setText(selectedUser.getNationality());
            spDomain.setSelection(adapter.getPosition(selectedUser.getDomain()));
        }
    }
    public void onCancelClick(View v){
        finish();
    }

    public void saveCeleb(View view) {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String nationality = etNationality.getText().toString().trim();
        String domain = spDomain.getSelectedItem().toString();
        String gender = rgGender.getCheckedRadioButtonId() == R.id.rbFemale ? "Female" : "Male";
        int photo;
        if (gender.equals("Female")) photo=R.drawable.user1;
        else photo=R.drawable.user;
        User user;
        if(fromAdd) {
                user = new User(firstName, lastName, photo, gender, date, nationality, domain);
                myHelper.add(user);
                Intent main = new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
            }
        else {
                Long id = Long.parseLong(tvId.getText().toString());
                user = new User(id, firstName, lastName, photo, gender, date, nationality, domain);
                int n = myHelper.update(user);
                Intent main = new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
            }
    }
}