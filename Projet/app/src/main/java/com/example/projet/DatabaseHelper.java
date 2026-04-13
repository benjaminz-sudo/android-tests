package com.example.projet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private static final String DB_NAME = "trip_database.db";
    private static final int DB_VERSION = 5;

    // Places Table
    public static final String TABLE_PLACES = "places";
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESC = "description";
    public static final String COL_DATE = "date";
    public static final String COL_TIME = "time";
    public static final String COL_ADDRESS = "address";
    public static final String COL_PHONE = "phone";
    public static final String COL_PHOTO = "photo";
    public static final String COL_VISITED = "visited";
    public static final String COL_TRIP_ID = "trip_id";

    // Trips Table
    public static final String TABLE_TRIPS = "trips";
    public static final String COL_TRIP_CITY = "city";
    public static final String COL_TRIP_START = "start_date";
    public static final String COL_TRIP_END = "end_date";

    private static final String CREATE_TABLE_TRIPS = "CREATE TABLE " + TABLE_TRIPS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TRIP_CITY + " TEXT, " +
            COL_TRIP_START + " TEXT, " +
            COL_TRIP_END + " TEXT);";

    private static final String CREATE_TABLE_PLACES = "CREATE TABLE " + TABLE_PLACES + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TITLE + " TEXT, " +
            COL_DESC + " TEXT, " +
            COL_DATE + " TEXT, " +
            COL_TIME + " TEXT, " +
            COL_ADDRESS + " TEXT, " +
            COL_PHONE + " TEXT, " +
            COL_PHOTO + " TEXT, " +
            COL_VISITED + " INTEGER DEFAULT 0, " +
            COL_TRIP_ID + " INTEGER, " +
            "FOREIGN KEY(" + COL_TRIP_ID + ") REFERENCES " + TABLE_TRIPS + "(" + COL_ID + ") ON DELETE CASCADE);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRIPS);
        db.execSQL(CREATE_TABLE_PLACES);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        onCreate(db);
    }

    public void open() {
        database = this.getWritableDatabase();
    }

    // Place Methods
    public void add(Place place, long tripId) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, place.getTitle());
        values.put(COL_DESC, place.getDescription());
        values.put(COL_DATE, place.getDate());
        values.put(COL_TIME, place.getTime());
        values.put(COL_ADDRESS, place.getAddress());
        values.put(COL_PHONE, place.getPhone());
        values.put(COL_PHOTO, place.getPhoto());
        values.put(COL_VISITED, place.isVisited() ? 1 : 0);
        values.put(COL_TRIP_ID, tripId);

        database.insert(TABLE_PLACES, null, values);
    }

    public Cursor getPlacesByTrip(long tripId) {
        return database.query(TABLE_PLACES, null, COL_TRIP_ID + " = ?", new String[]{String.valueOf(tripId)}, null, null, null);
    }

    public void update(Place place) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, place.getTitle());
        contentValues.put(COL_DESC, place.getDescription());
        contentValues.put(COL_DATE, place.getDate());
        contentValues.put(COL_TIME, place.getTime());
        contentValues.put(COL_ADDRESS, place.getAddress());
        contentValues.put(COL_PHONE, place.getPhone());
        contentValues.put(COL_PHOTO, place.getPhoto());
        contentValues.put(COL_VISITED, place.isVisited() ? 1 : 0);
        
        database.update(TABLE_PLACES, contentValues, COL_ID + " = ?", new String[]{String.valueOf(place.getId())});
    }

    public void delete(long id) {
        database.delete(TABLE_PLACES, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Trip Methods
    public long addTrip(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(COL_TRIP_CITY, trip.getCity());
        values.put(COL_TRIP_START, trip.getStartDate());
        values.put(COL_TRIP_END, trip.getEndDate());
        return database.insert(TABLE_TRIPS, null, values);
    }

    public Cursor getAllTrips() {
        return database.query(TABLE_TRIPS, null, null, null, null, null, null);
    }

    public Trip getTripById(long id) {
        Cursor cursor = database.query(TABLE_TRIPS, null, COL_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String city = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRIP_CITY));
            String start = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRIP_START));
            String end = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRIP_END));
            cursor.close();
            return new Trip(id, city, start, end);
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public void updateTrip(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(COL_TRIP_CITY, trip.getCity());
        values.put(COL_TRIP_START, trip.getStartDate());
        values.put(COL_TRIP_END, trip.getEndDate());
        database.update(TABLE_TRIPS, values, COL_ID + " = ?", new String[]{String.valueOf(trip.getId())});
    }

    public void deleteTrip(long id) {
        database.delete(TABLE_TRIPS, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
