package com.example.projet;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PlaceDisplay extends AppCompatActivity {
    DatabaseHelper myHelper;
    ListView lvPlaces;
    private long tripId;
    private Button btnTrip, btnEditTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_place_display);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tripId = getIntent().getLongExtra("trip_id", -1);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        
        myHelper = new DatabaseHelper(this);
        myHelper.open();

        lvPlaces = findViewById(R.id.lvPlaces);
        lvPlaces.setEmptyView(findViewById(R.id.tvEmpty));
        registerForContextMenu(lvPlaces);

        btnTrip = findViewById(R.id.btnTrip);
        btnTrip.setOnClickListener(v -> finish());

        btnEditTrip = findViewById(R.id.btnEditTrip);
        btnEditTrip.setOnClickListener(v -> {
            Trip trip = myHelper.getTripById(tripId);
            if (trip != null) {
                Intent intent = new Intent(this, ModTrip.class);
                intent.putExtra("SelectedTrip", trip);
                startActivity(intent);
            }
        });

        if (lvPlaces != null) {
            lvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    String idStr = ((TextView)view.findViewById(R.id.tvId)).getText().toString();
                    long placeId = Long.parseLong(idStr);
                    String titleItem= ((TextView)view.findViewById(R.id.tvTitle)).getText().toString();
                    String descriptionItem= ((TextView)view.findViewById(R.id.tvDescription)).getText().toString();
                    String dateItem= ((TextView)view.findViewById(R.id.tvDate)).getText().toString();
                    String timeItem= ((TextView)view.findViewById(R.id.tvTime)).getText().toString();
                    String addressItem= ((TextView)view.findViewById(R.id.tvAddress)).getText().toString();
                    String phoneItem= ((TextView)view.findViewById(R.id.tvPhone)).getText().toString();
                    String photoItem= ((TextView)view.findViewById(R.id.tvPhoto)).getText().toString();
                    
                    // Use the string from resources to check visited status safely
                    String visitedText = ((TextView)view.findViewById(R.id.tvVisited)).getText().toString();
                    boolean visitedItem = visitedText.equals(getString(R.string.visited_status));

                    Place pPlace = new Place(placeId, titleItem, descriptionItem, dateItem, timeItem, addressItem, phoneItem, photoItem, visitedItem);
                    Intent intent = new Intent(getApplicationContext(), ModPlace.class);
                    intent.putExtra("SelectedPlace", pPlace);
                    intent.putExtra("fromAdd", false);
                    intent.putExtra("trip_id", tripId);
                    startActivity(intent);
                }
            });
        }
        chargeData();
    }

    public void addPlace(View view) {
        Intent intent = new Intent(this, ModPlace.class) ;
        intent.putExtra("fromAdd", true);
        intent.putExtra("trip_id", tripId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargeData();
    }

    public void chargeData(){
        final String[] from = new String[]{DatabaseHelper.COL_ID, DatabaseHelper.COL_TITLE, DatabaseHelper.COL_DATE, DatabaseHelper.COL_VISITED, DatabaseHelper.COL_DESC, DatabaseHelper.COL_TIME, DatabaseHelper.COL_ADDRESS, DatabaseHelper.COL_PHONE, DatabaseHelper.COL_PHOTO};
        final int[] to = new int[]{R.id.tvId, R.id.tvTitle, R.id.tvDate, R.id.tvVisited, R.id.tvDescription, R.id.tvTime, R.id.tvAddress, R.id.tvPhone, R.id.tvPhoto};
        Cursor c = myHelper.getPlacesByTrip(tripId);
        
        if (c != null) {
            if (c.getCount() == 0) {
                btnEditTrip.setVisibility(View.VISIBLE);
            } else {
                btnEditTrip.setVisibility(View.GONE);
            }
        }

        SimpleCursorAdapter adapter= new SimpleCursorAdapter(this, R.layout.place_item_view, c, from, to, 0);
        
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.tvVisited) {
                    int visited = cursor.getInt(columnIndex);
                    ((TextView) view).setText(visited == 1 ? getString(R.string.visited_status) : getString(R.string.not_visited_status));
                    return true;
                }
                return false;
            }
        });

        lvPlaces.setAdapter(adapter);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        if (item.getItemId()==R.id.delete){
            myHelper.delete(info.id);
            chargeData();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
