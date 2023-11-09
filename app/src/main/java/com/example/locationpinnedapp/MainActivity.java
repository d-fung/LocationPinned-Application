package com.example.locationpinnedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText name_text_input;
    EditText address_text_input;
    Button search_button;
    Button add_button;
    Button update_button;
    Button delete_button;
    TextView latitude_display;
    TextView longitude_display;

    private static boolean POPULATE_DATABASE;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        name_text_input = findViewById(R.id.name_text_input);
        address_text_input = findViewById(R.id.address_text_input);
        add_button = findViewById(R.id.add_button);
        search_button = findViewById(R.id.search_button);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        latitude_display = findViewById(R.id.latitude_display);
        longitude_display = findViewById(R.id.longitude_display);


        // Populate the database with text file if set to true
        POPULATE_DATABASE = true;
        if (POPULATE_DATABASE) {
            try {
                storeLocationDataFromFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Searches the database and sets the latitude and longitude if a match is found
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address_input = address_text_input.getText().toString();
                if (address_input.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter a valid address", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                Cursor cursor = dbHelper.searchLocation(address_input);
                if (cursor.moveToFirst()) {
                    latitude_display.setText(cursor.getString(3));
                    longitude_display.setText(cursor.getString(4));
                } else {

                    Toast.makeText(MainActivity.this, "No matching location found", Toast.LENGTH_SHORT).show();

                }
                cursor.close();

            }
        });

        // Adds a new location to the database
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address_input = address_text_input.getText().toString();
                String name_input = name_text_input.getText().toString();
                if (address_input.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter a valid address", Toast.LENGTH_SHORT).show();
                    return;
                }

                Geocoder geocoder = new Geocoder(MainActivity.this);
                if (!Geocoder.isPresent()) {
                    Toast.makeText(MainActivity.this, "Geocoder not available", Toast.LENGTH_SHORT).show();
                }

                // Use geocoding to get the longitude and latitude from the address
                try {
                    List<Address> addresses = geocoder.getFromLocationName(address_input, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        double latitude = address.getLatitude();
                        double longitude = address.getLongitude();

                        // Display the results
                        latitude_display.setText(String.valueOf(latitude));
                        longitude_display.setText(String.valueOf(longitude));

                        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                        dbHelper.addLocation(name_input, address_input, String.valueOf(latitude), String.valueOf(longitude));
                        Toast.makeText(MainActivity.this, "Location added to database", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "No matching location found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        // Updates a selected location based on address
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address_input = address_text_input.getText().toString();
                if (address_input.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter a valid address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Opens the UpdateLocation page and passes in the selected address to be updated
                Intent intent = new Intent(MainActivity.this, UpdateLocation.class);
                intent.putExtra("address_to_update", address_input);
                startActivity(intent);

            }
        });


        // Deletes the location based on address
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address_input = address_text_input.getText().toString();
                if (address_input.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter a valid address", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                dbHelper.deleteLocation(address_input);
            }
        });
    }

    // This function creates a new database and reads from the locations.txt file
    void storeLocationDataFromFile() throws IOException{
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
        Geocoder geocoder = new Geocoder(this);
        if (!Geocoder.isPresent()){
            Toast.makeText(this, "Geocoder not available", Toast.LENGTH_SHORT).show();
        }

        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("locations.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            // Parse the data and insert it into the database
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String name = parts[0].trim();
                double latitude = Double.parseDouble(parts[1].trim());
                double longitude = Double.parseDouble(parts[2].trim());

                // Gets the address using reverse geocoding by passing in latitude and longitude values
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (!addresses.isEmpty()){
                        Address address = addresses.get(0);
                        String fullAddress = address.getSubThoroughfare() + " " + address.getThoroughfare();
                        Log.d("LocationData", "Name: " + name + ", Latitude: " + latitude + ", Longitude: " + longitude + " Address: " + fullAddress);

                        dbHelper.addLocation(name, fullAddress, String.valueOf(latitude), String.valueOf(longitude));
                    }

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}