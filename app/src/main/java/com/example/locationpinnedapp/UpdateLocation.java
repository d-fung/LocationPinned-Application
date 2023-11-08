package com.example.locationpinnedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class UpdateLocation extends AppCompatActivity {

    String address_to_update;
    EditText address_to_update_text_input;
    EditText new_address_text_input;
    EditText name_text_input;
    Button search_button;
    Button update_button;
    Button cancel_button;
    TextView latitude_display;
    TextView longitude_display;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_location);
        setSupportActionBar(findViewById(R.id.toolbar2));

        // Set references to all views
        address_to_update_text_input = findViewById(R.id.address_to_update_text_input);
        new_address_text_input = findViewById(R.id.new_address_text_input);
        name_text_input = findViewById(R.id.name_text_input2);
        search_button = findViewById(R.id.search_button2);
        update_button = findViewById(R.id.update_button2);
        cancel_button = findViewById(R.id.cancel_button2);

        latitude_display = findViewById(R.id.latitude_display2);
        longitude_display = findViewById(R.id.longitude_display2);

        getAndSetIntentData();

        // User can search if the new address they entered in is valid before updating existing address
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address_input = new_address_text_input.getText().toString();
                if (address_input.equals("")) {
                    Toast.makeText(UpdateLocation.this, "Please enter a valid address", Toast.LENGTH_SHORT).show();
                    return;
                }
                Geocoder geocoder = new Geocoder(UpdateLocation.this);
                if (!Geocoder.isPresent()) {
                    Toast.makeText(UpdateLocation.this, "Geocoder not available", Toast.LENGTH_SHORT).show();
                }

                try {
                    List<Address> addresses = geocoder.getFromLocationName(address_input, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        double latitude = address.getLatitude();
                        double longitude = address.getLongitude();

                        // Display the results
                        latitude_display.setText(String.valueOf(latitude));
                        longitude_display.setText(String.valueOf(longitude));

                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
        }});

        // Updates the database using new values specified in the activity
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address_to_update = address_to_update_text_input.getText().toString();
                String address_input = new_address_text_input.getText().toString();
                String name_input = name_text_input.getText().toString();

                if (address_input.equals("") || address_to_update.equals("")) {
                    Toast.makeText(UpdateLocation.this, "Please enter a valid address", Toast.LENGTH_SHORT).show();
                    return;
                }

                Geocoder geocoder = new Geocoder(UpdateLocation.this);
                if (!Geocoder.isPresent()) {
                    Toast.makeText(UpdateLocation.this, "Geocoder not available", Toast.LENGTH_SHORT).show();
                }

                try {
                    List<Address> addresses = geocoder.getFromLocationName(address_input, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        double latitude = address.getLatitude();
                        double longitude = address.getLongitude();

                        // Display the results
                        latitude_display.setText(String.valueOf(latitude));
                        longitude_display.setText(String.valueOf(longitude));

                    } else {
                        Toast.makeText(UpdateLocation.this, "No matching location found", Toast.LENGTH_SHORT).show();
                        return;
                }
                } catch (IOException e){
                    e.printStackTrace();
                }

                String latitude_input = latitude_display.getText().toString();
                String longitude_input = latitude_display.getText().toString();

                DatabaseHelper dbHelper = new DatabaseHelper(UpdateLocation.this);
                dbHelper.updateData(address_to_update,
                        name_input,
                        address_input,
                        latitude_input,
                        longitude_input);

                Intent intent = new Intent(UpdateLocation.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Go back to main page if cancel button is clicked
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateLocation.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }


    // Sets the address to be updated as referenced from the MainActivity
    void getAndSetIntentData(){
        if (getIntent().hasExtra("address_to_update")){
            address_to_update = getIntent().getStringExtra("address_to_update");
            address_to_update_text_input.setText(address_to_update);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}