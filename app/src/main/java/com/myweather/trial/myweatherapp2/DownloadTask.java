package com.myweather.trial.myweatherapp2;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Void, String> {
    private final int WALLDORF_ID = 2814668;
    // Only needed to make toast. There are probably better options.
    private MainActivity mainActivity;

    public DownloadTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... urls) {
        String response = "";
        URL url;
        HttpURLConnection urlConn;
        try {
            url = new URL(urls[0]);
            try {
                urlConn = (HttpURLConnection) url.openConnection();
                InputStream input = urlConn.getInputStream();
                InputStreamReader inputReader = new InputStreamReader(input);
                int data = inputReader.read();
                while (data != -1) {
                    char current = (char) data;
                    response += current;
                    data = inputReader.read();
                }
            } catch (FileNotFoundException e) {
                // if response is not valid
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result == null) {
            Toast.makeText(mainActivity, "Your city was not found", Toast.LENGTH_LONG).show();
            mainActivity.clearTemperatureAndWeather();
            return;
        }

        try {
            JSONObject jsonResponse = new JSONObject(result);

            JSONObject mainWeatherData = jsonResponse.getJSONObject("main");
            String weather = jsonResponse.getJSONArray("weather").getJSONObject(0)
                    .getString("description");
            double temperature = Double.parseDouble(mainWeatherData.getString("temp"));
            String humidity = mainWeatherData.getString("humidity") + "%";

            MainActivity.cityEditText.setText(jsonResponse.getString("name"));
            MainActivity.weatherTextView.setText(weather);
            MainActivity.temperatureTextView.setText("Temperature: " + String.valueOf(temperature) + "\u00b0");
            MainActivity.humidityTextView.setText("Humidity: " + humidity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
