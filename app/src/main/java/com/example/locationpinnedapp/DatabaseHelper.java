package com.example.locationpinnedapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DB_NAME = "LocationsDatabase.db";
    private static final Integer DB_VERSION = 1;
    private static final String TABLE_NAME = "Locations";
    private static final String LOCATION_ID = "id";
    private static final String LOCATION_NAME = "Name";
    private static final String LOCATION_ADDRESS = "Address";
    private static final String LOCATION_LATITUDE = "Latitude";
    private static final String LOCATION_LONGITUDE = "LONGITUDE";


    public static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LOCATION_NAME + " TEXT, " +
                    LOCATION_ADDRESS + " TEXT, " +
                    LOCATION_LATITUDE + " TEXT, " +
                    LOCATION_LONGITUDE + " TEXT);";

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // returns a cursor with the matching address
    public Cursor searchLocation(String address){
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues cv = new ContentValues();
        //cv.put(LOCATION_ADDRESS, address_query);
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + LOCATION_ADDRESS + " LIKE ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + address + "%"});

        return cursor;
    }

    // adds new location to database
    public void addLocation(String name, String address, String latitude, String longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LOCATION_NAME, name);
        cv.put(LOCATION_ADDRESS, address);
        cv.put(LOCATION_LATITUDE, latitude);
        cv.put(LOCATION_LONGITUDE, longitude);

        long result = db.insert(TABLE_NAME, null, cv);

        if (result == -1){
            Toast.makeText(context, "Unable to save location", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully saved location", Toast.LENGTH_SHORT).show();
        }
    }

    // updates the entry in the database that matches the address_old input
    public void updateData(String address_old, String name, String address, String latitude, String longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LOCATION_NAME, name);
        cv.put(LOCATION_ADDRESS, address);
        cv.put(LOCATION_LATITUDE, latitude);
        cv.put(LOCATION_LONGITUDE, longitude);

        long result = db.update(TABLE_NAME, cv, "address=?", new String[] {address_old});
        if (result == -1){
            Toast.makeText(context, "Failed to update location", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully updated location", Toast.LENGTH_SHORT).show();
        }
    }

    // queries the database and deletes the entry with matching address
    public void deleteLocation(String address){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "address=?", new String[] {address});
        if (result>0){
            Toast.makeText(context, "Successfully deleted location", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to delete location", Toast.LENGTH_SHORT).show();
        }
    }

}
