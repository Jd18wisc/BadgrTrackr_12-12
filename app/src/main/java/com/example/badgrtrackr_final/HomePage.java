package com.example.badgrtrackr_final;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.badgrtrackr_final.api.LocationListAPI;
import com.example.badgrtrackr_final.data_types.LocationData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class HomePage extends Fragment {
    ExpandableListView expListView; // expandable list view
    LocationListAdapter expListAdapter; // expandable list adapter (put data into expandable list), using custom LocationListAdapter class
    SearchView searchView; // the search bar object
    LocationListAPI locAPI; // location list API to access location data
    FusedLocationProviderClient client;
    LatLng currLocation;
    Map<String, Double> distances;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client = LocationServices.getFusedLocationProviderClient(getContext());
            client.getLastLocation()
                    .addOnCompleteListener(task -> {
                        currLocation = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    });
        }
        return inflater.inflate(R.layout.home_page, container, false);
    }

    private void loadDistances(List<LocationData> locations){
        for (LocationData loc : locations){
            float[] res = new float[3];
            double distance = res[0];
            Location.distanceBetween(currLocation.latitude, currLocation.longitude,
                    loc.getCoordinates().get("longitude"), loc.getCoordinates().get("latitude"), res);
            distances.put(loc.getName(), distance * 0.000621371);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InputStream is = getResources().openRawResource(R.raw.location_data); // create a new input stream for the location_data csv
        InputStream allLocHisIs = getResources().openRawResource(R.raw.location_history); // create a new input stream for the location_history csv

        locAPI = new LocationListAPI(is, allLocHisIs); // create a new location API with the required data

        expListView = view.findViewById(R.id.expandable_list); // access the expandable view xml object
        expListAdapter = new LocationListAdapter(view.getContext(), locAPI, locAPI.getLocationList()); // set the adapter to a new instance of the location adapter
        expListView.setAdapter(expListAdapter); // set the expandable list with the adapter

        loadDistances(locAPI.getLocationList());

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition = -1;
            @Override
            public void onGroupExpand(int i) {
                if (lastExpandedPosition != -1 && i != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = i;
            }
        });
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                String selected = expListAdapter.getChild(i, i1).toString();
                return true;
            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                String selected = expListAdapter.getChild(i, i1).toString();
                return true;
            }
        });

        // get the search view object in xml
        searchView = view.findViewById(R.id.search_bar);

        // override the search features
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<LocationData> temp = expListAdapter.filterData(query); // search locations for submitted string
                expListAdapter = new LocationListAdapter(view.getContext(), locAPI, temp); // new adapter instance with only locations found by search
                expListView.setAdapter(expListAdapter); // set the view to the new adapter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<LocationData> temp = expListAdapter.filterData(newText); // search locations for submitted string
                expListAdapter = new LocationListAdapter(view.getContext(), locAPI, temp); // new adapter instance with only locations found by search
                expListView.setAdapter(expListAdapter); // set the view to the new adapter
                return false;
            }
        });
    }
}
