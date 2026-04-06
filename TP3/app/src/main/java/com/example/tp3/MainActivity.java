package com.example.tp3;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper myHelper;
    private ListView lvCelebs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. Initialize Database
        myHelper = new DatabaseHelper(this);
        myHelper.open();

        // 3. Setup ListView
        lvCelebs = findViewById(R.id.lvCelebs);
        lvCelebs.setEmptyView(findViewById(R.id.tvEmpty));

        chargeData();

        // 4. Handle Item Clicks (Transfer User via Intent)
        lvCelebs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = myHelper.getCelebById(id);
                Intent intent = new Intent(MainActivity.this, CelebMod.class);
                intent.putExtra("SelectedUser", selectedUser);
                intent.putExtra("fromAdd", false);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void chargeData(){
        final String[] from = new String[]{
                DatabaseHelper.FIRST_NAME,
                DatabaseHelper.LAST_NAME,
                DatabaseHelper.GENDER
        };
        final int[] to = new int[]{
                R.id.tvFirstName,
                R.id.tvLastName,
                R.id.ivPhoto
        };
        
        Cursor c = myHelper.getAllCelebs();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.celeb_item_view, c, from, to, 0);
        
        adapter.setViewBinder((view, cursor, columnIndex) -> {
            if (view.getId() == R.id.ivPhoto) {
                String gender = cursor.getString(columnIndex);
                ImageView iv = (ImageView) view;
                if ("Female".equals(gender)) {
                    iv.setImageResource(R.drawable.user1);
                } else {
                    iv.setImageResource(R.drawable.user);
                }
                return true;
            }
            return false;
        });

        lvCelebs.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.celebs_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_celeb) {
            if (lvCelebs.getCount() >= 10) {
                Toast.makeText(this, "Maximum 10 celebrities reached", Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent intent = new Intent(this, CelebMod.class);
            intent.putExtra("fromAdd", true);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
