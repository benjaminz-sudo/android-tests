package com.example.tp3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "CELEBS";
    public static final String _ID = "_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String BDATE = "date";
    public static final String GENDER = "gender";
    public static final String DOMAIN = "domain";
    public static final String NATIONALITY = "nationality";

    private static final String DB_NAME = "TopCelebs.DB";
    private static final int DB_VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIRST_NAME + " TEXT NOT NULL, " + 
            LAST_NAME + " TEXT NOT NULL, " + 
            GENDER + " TEXT NOT NULL, " + 
            BDATE + " TEXT NOT NULL, " + 
            NATIONALITY + " TEXT NOT NULL, " + 
            DOMAIN + " TEXT NOT NULL);";

    private SQLiteDatabase database;

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

    public void add(User user) {
        database.insert(TABLE_NAME, null, getUserContentValues(user));
    }

    public int update(User user) {
        return database.update(TABLE_NAME, getUserContentValues(user),
                _ID + " = ?", new String[]{String.valueOf(user.getId())});
    }

    public void delete(long id) {
        database.delete(TABLE_NAME, _ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllCelebs() {
        return database.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public User getCelebById(long id) {
        Cursor cursor = database.query(TABLE_NAME, null, _ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getLong(cursor.getColumnIndexOrThrow(_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(LAST_NAME)),
                    0, // photo index or resource id
                    cursor.getString(cursor.getColumnIndexOrThrow(GENDER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(BDATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(NATIONALITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DOMAIN))
            );
            cursor.close();
            return user;
        }
        return null;
    }

    private ContentValues getUserContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(FIRST_NAME, user.getFirstName());
        values.put(LAST_NAME, user.getLastName());
        values.put(GENDER, user.getGender());
        values.put(BDATE, user.getBirthDate());
        values.put(NATIONALITY, user.getNationality());
        values.put(DOMAIN, user.getDomain());
        return values;
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

}
