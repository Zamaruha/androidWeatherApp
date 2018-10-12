
package com.myweather.trial.myweatherapp2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String MY_API_ID = "4a823942205d29efa870dd4f0e282e01";
    protected static EditText cityEditText;
    protected static TextView temperatureTextView;
    protected static TextView weatherTextView;
    protected static TextView humidityTextView;
    protected static Button getWeather;
    private boolean cityIsSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        temperatureTextView = findViewById(R.id.temperatureTxt);
        weatherTextView = findViewById(R.id.weatherTxt);
        humidityTextView = findViewById(R.id.humidityTxt);
        getWeather = findViewById(R.id.getWeatherBtn);
        cityIsSet = false;

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable inputCity) {
                if (inputCity != null && !inputCity.equals("") && !inputCity.equals("Type in city")) {
                    cityIsSet = true;
                    return;
                } else {
                    Toast.makeText(MainActivity.this, "Please enter your city", Toast.LENGTH_LONG)
                            .show();
                    cityIsSet = false;
                    return;
                }
            }
        });


        getWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(MainActivity.this);
                if (!cityIsSet) {
                    Toast.makeText(MainActivity.this, "Please enter your city", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                String city = cityEditText.getText().toString();
                String request = "https://api.openweathermap.org/data/2.5/weather?q="
                        + city + "&units=metric" + "&appid=" + MY_API_ID;
                DownloadTask dTask = new DownloadTask(MainActivity.this);
                dTask.execute(request);
            }
        });


        // if last location exists
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)

        {
            Log.w("location access", "no");
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            String request = "https://api.openweathermap.org/data/2.5/weather?lat" + latitude +
                    "&lon=" + longitude + "&appid=" + MY_API_ID;

            DownloadTask dTask = new DownloadTask(MainActivity.this);
            dTask.execute(request);
        } else {
            Toast.makeText(this, "No information about your last location",
                    Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Please type in your city",
                    Toast.LENGTH_LONG).show();
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void clearTemperatureAndWeather() {
        weatherTextView.setText("");
        temperatureTextView.setText("");
        humidityTextView.setText("");
    }
}


