package com.example.tour_it_app.fragments.landmarks;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.tour_it_app.MainActivity;
import com.example.tour_it_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class HomeFragment extends Fragment implements OnMapReadyCallback
{
    //FragmentMapsBinding binding;.
    SupportMapFragment mapFragment;
    public static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;

    //Searching functionality
    private SearchView searchView;
    public static Marker newMarker = null;
    private PlacesClient placesClient;
    //private PlacesClient placesClient;

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
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        initializeMap();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        if (!Places.isInitialized())
        {
            Places.initialize(this, getString(R.string.map_key));
        }
        placesClient = Places.createClient(getContext());

        mMap = googleMap;

        googleMap.setOnPoiClickListener(pointOfInterest ->
        {
            String x = "Latitude:" + pointOfInterest.latLng.latitude + " Longitude:" + pointOfInterest.latLng.longitude;
            mDestination = new LatLng(pointOfInterest.latLng.latitude, pointOfInterest.latLng.longitude);

            AlertDialog.Builder Menu = new AlertDialog.Builder(getContext());

            View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.marker_details, (ViewGroup) getChildFragmentManager().findFragmentById(android.R.id.content), false);

            View viewInflated2 = LayoutInflater.from(getActivity()).inflate(R.layout.marker_details, (ViewGroup) getChildFragmentManager().findFragmentById(android.R.id.content), false);
            Menu.setView(viewInflated);
            final AlertDialog alertDialog = Menu.create();
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes();

            title = viewInflated.findViewById(R.id.name);
            address = viewInflated.findViewById(R.id.address);
            number = viewInflated.findViewById(R.id.number);
            other = viewInflated.findViewById(R.id.other);
            btn_fav = viewInflated.findViewById(R.id.btn_add_fav);
            btn_route = viewInflated.findViewById(R.id.btn_route);

            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER, Place.Field.ADDRESS, Place.Field.OPENING_HOURS);

            // Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(pointOfInterest.placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                if (place.getOpeningHours() != null || place.getAddress() != null || place.getPhoneNumber() != null) {
                    title.setText(place.getName());
                    address.setText(place.getAddress());
                    number.setText(place.getPhoneNumber());
                    if (place.getOpeningHours() == null) {

                    } else {
                        other.setText("" + place.getOpeningHours().getWeekdayText());
                    }

                } else {
                    title.setText(place.getName());
                    address.setText(x);
                    other.setText("");
                }

                Log.i(TAG, "Place found: " + place.getName());
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            });
        }


        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener()
        {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse)
            {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getContext(), "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Location>()
                {
                    @Override
                    public void onSuccess(Location location)
                    {
                        currentLat = location.getLatitude();
                        currentLong = location.getLongitude();

                        LatLng latLng = new LatLng(currentLat, currentLong);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                    }
                });
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse)
            {
                Toast.makeText(getContext(), "Permission" + permissionDeniedResponse.getPermissionName() + "" + "was denied!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken)
            {
                permissionToken.continuePermissionRequest();
            }
        }).check();
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
}