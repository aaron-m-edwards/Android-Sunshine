package com.aedwards.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by aaron on 24/08/2014.
 */
public class ForecastFragment extends Fragment{

    private SharedPreferences preferences;

    private ArrayAdapter<String> weekAdaptor;

    public ForecastFragment(){

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragmentforecast, menu);
    }

    @Override
    public void onStart() {
        updateWeather();
        super.onStart();
    }

    private void updateWeather() {
        new FetchWeatherTask(weekAdaptor).execute(getLocation(), getUnits());
    }

    private String getLocation() {
        return preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_default_location));
    }
    private String getUnits() {
        return preferences.getString(getString(R.string.pref_unit_key), getString(R.string.pref_default_unit));
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_sunshine, container, false);

        weekAdaptor = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());

        ListView textViewForecast = (ListView)rootView.findViewById(R.id.listView_forecast);
        textViewForecast.setAdapter(weekAdaptor);

        textViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, adapterView.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_refresh:
                updateWeather();
        }

        return super.onOptionsItemSelected(item);
    }


}
