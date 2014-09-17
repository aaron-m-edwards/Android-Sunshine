package com.aedwards.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aaron on 24/08/2014.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

    private static final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    public static final int DAYS = 14;

    private ArrayAdapter<String> forecastAdaptor;

    public FetchWeatherTask(ArrayAdapter<String> forecastAdapter){
        this.forecastAdaptor = forecastAdapter;
    }

    @Override
    protected String[] doInBackground(String... strings) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String [] weatherData = new String[0];

        String location = strings[0];
        String unit = strings[1];

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri url = new Uri.Builder()
                    .scheme("http")
                    .authority("api.openweathermap.org")
                    .appendPath("data")
                    .appendPath("2.5")
                    .appendPath("forecast")
                    .appendPath("daily")
                    .appendQueryParameter("q", location)
                    .appendQueryParameter("mode", "json")
                    .appendQueryParameter("units", unit)
                    .appendQueryParameter("cnt", String.valueOf(DAYS))
                    .build();
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) new URL(url.toString()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return weatherData;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return weatherData;
            }

            String jsonString = buffer.toString();
            weatherData = WeatherDataParser.getWeatherDataFromJson(jsonString, DAYS);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON parse Error ", e);
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG, "IO Error ", e);
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
            return weatherData;
        }
    }

    @Override
    protected void onPostExecute(String[] weatherData) {
        forecastAdaptor.clear();
        forecastAdaptor.addAll(weatherData);
    }
}
