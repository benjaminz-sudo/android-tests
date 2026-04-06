package com.example.tp2_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ArrayList<User> arrayOfUsers = new ArrayList<User>();
        arrayOfUsers.add(new User("FirstName1", "LastName1", R.drawable.user, "Male", "24/06/1987", "Nation1", "Domain1"));
        arrayOfUsers.add(new User("FirstName2", "LastName2", R.drawable.user1, "Female", "05/02/1985", "Nation2", "Domain2"));

        UserAdapter adapter = new UserAdapter(this, R.layout.custom_item_list, arrayOfUsers);
        ListView myListView = (ListView) findViewById(R.id.ListView0);
        myListView.setAdapter(adapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = arrayOfUsers.get(position);
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("firstName", selectedUser.firstName);
                intent.putExtra("lastName", selectedUser.lastName);
                intent.putExtra("photo", selectedUser.photo);
                intent.putExtra("gender", selectedUser.gender);
                intent.putExtra("birthDate", selectedUser.birthDate);
                intent.putExtra("nationality", selectedUser.nationality);
                intent.putExtra("domain", selectedUser.domain);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
