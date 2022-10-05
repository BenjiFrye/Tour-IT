package com.example.tour_it_app.fragments.landmarks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tour_it_app.MainActivity;
import com.example.tour_it_app.R;
import com.example.tour_it_app.startup.Login;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener
{
    private static final int REQUEST_CODE = 44;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 22;

    //FragmentMapsBinding binding;.
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    public static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;
    boolean permissionGrated;

    //Searching functionality
    private SearchView searchView;
    public static Marker newMarker = null;
    private PlacesClient placesClient;
    TextView distance, duration, type_measure, title, address, other, number;
    //Searching functionality

    //Destination code
    LatLng mDestination;
    //Destination code

    public HomeFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        //Checks if user has given need permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {

        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        initializeMap();

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        Toast.makeText(getContext(), "I WAS CALLED", Toast.LENGTH_LONG).show();
        getCurrentLocation();


        mMap = googleMap;
        googleMap.setOnPoiClickListener(this);


    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation()
    {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location)
            {
                if (location != null)
                {
                    Toast.makeText(getContext(), "I WAS RUN", Toast.LENGTH_LONG).show();
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();

                    mMap.setMyLocationEnabled(true);

                    LatLng latLng = new LatLng(currentLat, currentLong);
                    //MarkerOptions options = new MarkerOptions().position(latLng).title("I am here");
                    //options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 44)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        return view; // Return view
    }

    private void initializeMap()
    {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(16);
        locationRequest.setFastestInterval(3000);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public void onPoiClick(@NonNull PointOfInterest poi)
    {
        Toast.makeText(getContext(), "YOU CLICKED ON A POI", Toast.LENGTH_LONG).show();
        Toast.makeText(getContext(), "\nClicked: " + poi.name
                                        + "\nPlace ID:" + poi.placeId
                                        + "\nLatitude:" + poi.latLng.latitude + " Longitude:" + poi.latLng.longitude, Toast.LENGTH_SHORT).show();

        Dialog dialog = new Dialog(getContext(), R.style.DialogStyle);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.marker_details);

        TextView markerName = dialog.findViewById(R.id.markerName);
        TextView markerAddress = dialog.findViewById(R.id.markerAddress);
        TextView markerNumber = dialog.findViewById(R.id.markerNumber);
        TextView markerOther = dialog.findViewById(R.id.markerOther);

        markerName.setText(poi.name.toString());
        markerAddress.setText("PLACEHOLDER: 21 Barry Road");
        markerNumber.setText("PLACEHOLDER: 081 485 3711");
        markerOther.setText("PLACEHOLDER: It's a ugly house");

        Button btn_Route = dialog.findViewById(R.id.btn_route);
        Button btn_add_fav = dialog.findViewById(R.id.btn_add_fav);

        btn_Route.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: ADD CODE TO NAVIGATE THE USER HERE

                Toast.makeText(getContext(), "YOU CLICKED: Find Route", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        btn_add_fav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: ADD CODE TO ADD THIS LOCATION TO THEIR FAVOURITES

                Toast.makeText(getContext(), "YOU CLICKED: Add to Favourites", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}