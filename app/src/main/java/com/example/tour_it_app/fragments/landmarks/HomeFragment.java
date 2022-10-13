package com.example.tour_it_app.fragments.landmarks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.example.tour_it_app.object_classes.Favourites;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener
{
    private static final int REQUEST_CODE = 44;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 22;

    SupportMapFragment mapFragment;
    public static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;
    LatLng currentLatLng;
    LatLng destinationLatLng;

    //Searching functionality
    public static Marker newMarker = null;
    PolylineOptions polylineOptions = null;

    private ImageButton btnOpenInfo;
    private ImageButton btnSmallHeart;
    private ImageButton btnCloseInfo;
    private LinearLayout infoLayout;
    private TextView routeTitle;
    private TextView routeAddress;
    private TextView routeOther;

    //Firebase variables
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference dbUsersRef = db.getReference("Users");

    private Favourites fav;



    public HomeFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {

        //Finding ID's
        btnSmallHeart = getActivity().findViewById(R.id.btnSmallHeart);
        btnCloseInfo = getActivity().findViewById(R.id.btnCloseInfo);
        infoLayout = getActivity().findViewById(R.id.info_layout);
        btnOpenInfo = getActivity().findViewById(R.id.btnOpenInfo);
        routeTitle = getActivity().findViewById(R.id.txtRouteTitle);
        routeAddress = getActivity().findViewById(R.id.txtRouteLat);
        routeOther = getActivity().findViewById(R.id.txtRouteLong);


        //Default operations
        infoLayout.setVisibility(View.INVISIBLE);
        btnOpenInfo.setVisibility(View.INVISIBLE);
        btnSmallHeart.setTag("1");
        fav = new Favourites();

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


        //Listeners
        btnOpenInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoLayout.setVisibility(View.VISIBLE);
            }
        });

        btnSmallHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeHeart();
            }
        });

        btnCloseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    //--------------------------- Method changes appearance of heart -------------------------------
    private void ChangeHeart() {

        //Change appearance of image button based on what the image already is
        if (btnSmallHeart.getTag() == "1"){
            btnSmallHeart.setImageResource(R.drawable.ic_small_heart_unfilled);
            btnSmallHeart.setTag("2");
        } else {
            btnSmallHeart.setImageResource(R.drawable.ic_small_heart_filled);
            btnSmallHeart.setTag("1");
        }
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
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
                    double currentLat = 0, currentLong = 0;

                   // Toast.makeText(getContext(), "I WAS RUN", Toast.LENGTH_LONG).show();
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();

                    //TODO: Debug code to set CURRENT LOCATION to Cape Town, FOR USE IN EMULATOR     - Remove for production
                  //  currentLat = -33.819513;
                  //  currentLong = 18.490832;
                    //TODO: Debug code to set CURRENT LOCATION to Cape Town, FOR USE IN EMULATOR     - Remove for production

                    mMap.setMyLocationEnabled(true);

                    currentLatLng = new LatLng(currentLat, currentLong);
                    //MarkerOptions options = new MarkerOptions().position(latLng).title("I am here");
                    //options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                }
            }
        });
    }

    private void direction2()
    {

    }


    private void direction() // LatLng currentLocation, LatLng destination SIGNATURE TO BE ADDED
    {
        if (newMarker != null)
        {
            newMarker.remove();
            mMap.clear();
        }
       // Toast.makeText(getContext(), "Direction 1", Toast.LENGTH_LONG).show();

        String destinationLatLong = destinationLatLng.latitude + ", " + destinationLatLng.longitude;
        double destinationLat = destinationLatLng.latitude, destinationLong = destinationLatLng.longitude;

        String originLatLong = currentLatLng.latitude + ", " + currentLatLng.longitude;
        double originLat = currentLatLng.latitude, originLong = currentLatLng.longitude;

       // Toast.makeText(getContext(), "Destination: " + destinationLatLong, Toast.LENGTH_LONG).show();
       // Toast.makeText(getContext(), "Origin: " + originLatLong, Toast.LENGTH_LONG).show();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", destinationLatLong) // + lat + ", " + lng   Where you want to go AKA the maker through the Find Routes button
                .appendQueryParameter("origin", originLatLong) //  + currentLat + ", " + currentLng    Current Location
                .appendQueryParameter("mode", "driving") // Will you be walking, driving or jogging
                .appendQueryParameter("key", "AIzaSyBHzZJu7d-ZpaB31W1_BOo590Tzi35XvLk").toString();
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
                            polylineOptions.width(15); // To change the thinkness of the line
                            polylineOptions.color(ContextCompat.getColor(getContext(), R.color.custom1));
                            polylineOptions.geodesic(true);
                        }
                        mMap.addPolyline(polylineOptions);


                        newMarker = mMap.addMarker( new MarkerOptions().position(new LatLng(originLat, originLong))
                                .title("Your current Location"));
                        newMarker = mMap.addMarker( new MarkerOptions().position(new LatLng(destinationLat, destinationLong))
                                .title("Your destination")
                                .snippet("Distance: 300m      Time: 3 Minutes"));
                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(originLat, originLong))           //ORIGIN
                                .include(new LatLng(destinationLat, destinationLong)).build();  //DESTINATION
                        Point point = new Point();
                        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 200, 30));
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

        //Toast.makeText(getContext(), "Direction 4", Toast.LENGTH_LONG).show();
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }


    private List<LatLng> decodePoly(String encoded)
    {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
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
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
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
        //Toast.makeText(getContext(), "YOU CLICKED ON A POI", Toast.LENGTH_LONG).show();

        final String placeId = poi.placeId;
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        if (!Places.isInitialized())
        {
            Places.initialize(requireContext(), "AIzaSyApmwTYKuY286rVrGi4qQ-d9DOT_TuxWNs");
        }

        MainActivity.placesClient.fetchPlace(request).addOnSuccessListener((response) ->
                {
                    Place place = response.getPlace();

                    // ------------------------------ DEBUG ---------------------------------
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
                    // ------------------------------ DEBUG ---------------------------------

                    destinationLatLng = new LatLng(poi.latLng.latitude, poi.latLng.longitude);
                    markerInteraction(poi);
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
    }

    public void markerInteraction(PointOfInterest poi)
    {
        //----------------------------- Code to display a dialog box -------------------------------
        // Used when a user clicks on a POI on google maps, AKA Burger King -> Give me more info

        Dialog dialog = new Dialog(getContext(), R.style.DialogStyle);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.marker_details);

        TextView markerName = dialog.findViewById(R.id.markerName);

        markerName.setText(poi.name);

        Button btn_Route = dialog.findViewById(R.id.btn_route);
        Button btn_add_fav = dialog.findViewById(R.id.btn_add_fav);

        btn_Route.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: ADD CODE TO NAVIGATE THE USER HERE
                //Toast.makeText(getContext(), "YOU CLICKED: Find Route", Toast.LENGTH_LONG).show();
                //Toast.makeText(getContext(), "Destination would be: " + poi.latLng.latitude + " " + poi.latLng.longitude, Toast.LENGTH_LONG).show();

                direction();
                LoadRouteInfo(poi.name, poi.latLng.latitude, poi.latLng.longitude);
                btnOpenInfo.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        btn_add_fav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AddLocationToDB(poi.name, poi.latLng.latitude, poi.latLng.longitude, poi.placeId);
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

        markerName.setText(poi.getTitle());

        Button btn_Route = dialog.findViewById(R.id.btn_route);
        Button btn_add_fav = dialog.findViewById(R.id.btn_add_fav);

        btn_Route.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: ADD CODE TO NAVIGATE THE USER HERE
               // Toast.makeText(getContext(), "YOU CLICKED: Find Route", Toast.LENGTH_LONG).show();
               // destinationLatLng = new LatLng(poi.getPosition().latitude, poi.getPosition().longitude);
                direction();
                LoadRouteInfo(poi.getTitle(), poi.getPosition().latitude, poi.getPosition().longitude);
                btnOpenInfo.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        btn_add_fav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AddLocationToDB(poi.getTitle(), poi.getPosition().latitude, poi.getPosition().longitude, poi.getId());
                dialog.dismiss();
            }
        });
        dialog.show();
        //----------------------------- Code to display a dialog box -------------------------------
    }

    //------------------------------- Method to populate route info card ---------------------------
    private void LoadRouteInfo(String title, double latitude, double longitude) {
         routeTitle.setText(title);
         routeAddress.setText(String.valueOf(latitude));
         routeOther.setText(String.valueOf(longitude));
    }
    //----------------------------------------------------------------------------------------------


    //------------------------------ Method to add location to favourites db -----------------------
    private void AddLocationToDB(String title, double lat, double lon, String id) {

        MainActivity mainAct = new MainActivity();

        //Retrieving current user's ID
        String userID = mainAct.UserID;

        DatabaseReference ref = dbUsersRef.child(userID).child("Favourites");

        fav.setTitle(title);
        fav.setLatitude(lat);
        fav.setLongitude(lon);
        fav.setLocationID(id);

        //TODO: CHECK IF THIS LOCATION DOESN'T ALREADY EXIST IN THE DATABASE BEFORE ADDING
        ref.push().setValue(fav);

        Toast.makeText(getContext(), title + " has successfully been added to your favourites.", Toast.LENGTH_LONG).show();

    }
    //----------------------------------------------------------------------------------------------


}
//References:
//Helped with getting Google Directions API to work - https://www.youtube.com/watch?v=CSbmlp61zLg&t=169s&ab_channel=CodeWorked