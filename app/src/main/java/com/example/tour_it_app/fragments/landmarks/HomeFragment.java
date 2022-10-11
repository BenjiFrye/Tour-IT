package com.example.tour_it_app.fragments.landmarks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tour_it_app.MainActivity;
import com.example.tour_it_app.R;
import com.example.tour_it_app.startup.Login;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener
{
    private static final int REQUEST_CODE = 44;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 22;

    //FragmentMapsBinding binding;.
    SupportMapFragment mapFragment;
   // FusedLocationProviderClient client;
    public static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;

    //Searching functionality
    public static Marker newMarker = null;

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
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker)
    {
        return false;
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker)
    {
        markerInteraction(marker);
        Toast.makeText(getContext(), "YOU CLICK THE SNIPPET", Toast.LENGTH_LONG).show();
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

                    //TODO: Debug code to set CURRENT LOCATION to Cape Town, FOR USE IN EMULATOR     - Remove for production
                    currentLat = -33.819513;
                    currentLong = 18.490832;
                    //TODO: Debug code to set CURRENT LOCATION to Cape Town, FOR USE IN EMULATOR     - Remove for production

                    mMap.setMyLocationEnabled(true);

                    LatLng currentlatLng = new LatLng(currentLat, currentLong);
                    //MarkerOptions options = new MarkerOptions().position(latLng).title("I am here");
                    //options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatLng, 15));
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                }
            }
        });
    }

    private void direction2()
    {

    }

    private void direction() // LatLng currentLocation, LatLng destination SIGNATURE TO BE ADDED
    {
        Toast.makeText(getContext(), "Direction 1", Toast.LENGTH_LONG).show();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", "-6.913823, 107.608983") // + lat + ", " + lng   Where you want to go AKA the maker through the Find Routes button
                .appendQueryParameter("origin", "-6.916072, 107.641126") //  + currentLat + ", " + currentLng    Current Location
                .appendQueryParameter("mode", "driving") // Will you be walking, driving or jogging
                .appendQueryParameter("key", "AIzaSyApmwTYKuY286rVrGi4qQ-d9DOT_TuxWNs").toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    String status = response.getString("status");
                    if (status.equals("OK"))
                    {
                        JSONArray routes = response.getJSONArray("routes");

                        ArrayList<LatLng> points;
                        PolylineOptions polylineOptions = null;

                        for (int i = 0; i < routes.length(); i++)
                        {
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                            for (int j = 0; j < legs.length(); j++)
                            {
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");

                                for (int k = 0; k < steps.length(); k++)
                                {
                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                    List<LatLng> list = decodePoly(polyline);

                                    for (int l = 0; l < list.size(); l++)
                                    {
                                        LatLng position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                        points.add(position);
                                    }
                                }
                            }
                            polylineOptions.addAll(points);
                            polylineOptions.width(10); // To change the thinkness of the line
                            polylineOptions.color(ContextCompat.getColor(getContext(), R.color.custom2));
                            polylineOptions.geodesic(true);
                        }
                        mMap.addPolyline(polylineOptions);
                        mMap.addMarker( new MarkerOptions().position(new LatLng(-6.9249233, 107.6345122)).title("Your current Location"));
                        mMap.addMarker( new MarkerOptions().position(new LatLng(-6.9218571, 107.6048254)).title("Your destination"));

                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(-6.9249233, 107.6345122))
                                .include(new LatLng(-6.9218571, 107.6048254)).build();
                        Point point = new Point();
                        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 150, 30));
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        , new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        });
        Toast.makeText(getContext(), "Direction 4", Toast.LENGTH_LONG).show();
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }


    private List<LatLng> decodePoly(String encoded)
    {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while(index < len)
        {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while (b > 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat/ 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
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

        final String placeId = poi.placeId;
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        if (!Places.isInitialized())
        {
            Places.initialize(getContext(), getString(R.string.map_key));
        }

        MainActivity.placesClient.fetchPlace(request).addOnSuccessListener((response) ->
        {
            Place place = response.getPlace();

            String locationID, locationName, locationNumber = null, locationLatLong;

            locationID = place.getId();
            locationName = place.getName();
            if (place.getPhoneNumber() == null)
            {
                locationNumber = "No Data Provided";
            }
            else
            {
                locationNumber = place.getPhoneNumber();
            }
            locationLatLong = "Latitude " + poi.latLng.latitude + ", Longitude " + poi.latLng.longitude;

            Log.i("Location Data",
                 "\nPlace ID: " + locationID
                    + "\nPlace Name: " + locationName
                    + "\nPlace Number: " + locationNumber
                    + "\nPlace LatLong: " + locationLatLong);
        }
        ).addOnFailureListener((exception) ->
        {
            if (exception instanceof ApiException)
            {
                final ApiException apiException = (ApiException) exception;
                Log.e("TAG:PlaceNotFound - ", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });
        markerInteraction(poi);
    }

    public void markerInteraction(PointOfInterest poi)
    {

        //----------------------------- Code to display a dialog box -------------------------------
        // Used when a user clicks on a POI on google maps, AKA Burger King -> Give me more info

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

                double latitude = poi.latLng.latitude;
                double longitude = poi.latLng.longitude;

                direction();
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
        //----------------------------- Code to display a dialog box -------------------------------
    }

    public void markerInteraction(Marker poi)
    {
        //----------------------------- Code to display a dialog box -------------------------------
        // Used when a user clicks on a POI on google maps, AKA Burger King -> Give me more info

        Dialog dialog = new Dialog(getContext(), R.style.DialogStyle);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.marker_details);

        TextView markerName = dialog.findViewById(R.id.markerName);
        TextView markerAddress = dialog.findViewById(R.id.markerAddress);
        TextView markerNumber = dialog.findViewById(R.id.markerNumber);
        TextView markerOther = dialog.findViewById(R.id.markerOther);

        markerName.setText(poi.getTitle());
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
        //----------------------------- Code to display a dialog box -------------------------------
    }
}
//References:
//Helped with getting Google Directions API to work - https://www.youtube.com/watch?v=CSbmlp61zLg&t=169s&ab_channel=CodeWorked