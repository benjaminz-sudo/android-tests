package com.example.projet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private static final String DB_NAME = "places.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_NAME = "places";
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESC = "description";
    public static final String COL_DATE = "date";
    public static final String COL_TIME = "time";
    public static final String COL_ADDRESS = "address";
    public static final String COL_PHONE = "phone";
    public static final String COL_PHOTO = "photo";
    public static final String COL_VISITED = "visited";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TITLE + " TEXT, " +
            COL_DESC + " TEXT, " +
            COL_DATE + " TEXT, " +
            COL_TIME + " TEXT, " +
            COL_ADDRESS + " TEXT, " +
            COL_PHONE + " TEXT, " +
            COL_PHOTO + " TEXT, " +
            COL_VISITED + " INTEGER DEFAULT 0);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void open() {
        database = this.getWritableDatabase();
    }

    public void add(Place place) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, place.getTitle());
        values.put(COL_DESC, place.getDescription());
        values.put(COL_DATE, place.getDate());
        values.put(COL_TIME, place.getTime());
        values.put(COL_ADDRESS, place.getAddress());
        values.put(COL_PHONE, place.getPhone());
        values.put(COL_PHOTO, place.getPhoto());
        values.put(COL_VISITED, place.isVisited() ? 1 : 0);

        database.insert(TABLE_NAME, null, values);
    }

    public Cursor getAllPlaces(){
        String[] projection = {COL_ID, COL_TITLE, COL_DESC, COL_DATE, COL_TIME, COL_ADDRESS, COL_PHONE, COL_PHOTO, COL_VISITED};
        Cursor cursor = database.query(TABLE_NAME, projection, null, null, null, null, null, null);
        return cursor;
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
        
        database.update(TABLE_NAME, contentValues, COL_ID + " = ?", new String[]{String.valueOf(place.getId())});
    }

    public void delete(long id) {
        database.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
