package com.example.tour_it_app.fragments.landmarks;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.tour_it_app.MainActivity.UserID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
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
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

    //Map variables
    private SupportMapFragment mapFragment;
    public static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng currentLatLng;
    private LatLng destinationLatLng;
    public static Marker newMarker = null;
    private PolylineOptions polylineOptions = null;

    //Component variables
    private ImageButton btnOpenInfo;
    private ImageButton btnSmallHeart;
    private ImageButton btnCloseInfo;
    private LinearLayout infoLayout;
    private TextView routeTitle;
    private TextView routeAddress;
    private TextView routeOther;
    private TextView routeEstTime;
    private TextView routeEstDis;

    //Type variables
    public String systemPreference;
    private static String distanceToLocation = "Not Set";
    private static String durationToLocation = "Not Set";

    //Firebase variables
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference dbUsersRef = db.getReference("Users");

    private Favourites fav;

    public void setDistanceToLocation(String distanceToLocation) {
        this.distanceToLocation = distanceToLocation;
    }

    public void setDurationToLocation(String durationToLocation) {
        this.durationToLocation = durationToLocation;
    }

    public HomeFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    //----------------------------- Main View Created method ---------------------------------------
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        //Finding ID's
       // btnSmallHeart = getActivity().findViewById(R.id.btnSmallHeart);
        infoLayout = getActivity().findViewById(R.id.info_layout);
        btnOpenInfo = getActivity().findViewById(R.id.btnOpenInfo);
        routeTitle = getActivity().findViewById(R.id.txtRouteTitle);
        routeAddress = getActivity().findViewById(R.id.txtRouteLat);
        routeOther = getActivity().findViewById(R.id.txtRouteLong);
        routeEstTime = getActivity().findViewById(R.id.txtRouteEstTime);;
        routeEstDis = getActivity().findViewById(R.id.txtRouteDis);
        btnCloseInfo = getActivity().findViewById(R.id.btnCloseInfo);

        //Default operations
       // btnSmallHeart.setTag("1");
        infoLayout.setVisibility(View.INVISIBLE);
        btnOpenInfo.setVisibility(View.INVISIBLE);
        fav = new Favourites();

        //Checks if user has given need permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
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

        /*
        btnSmallHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeHeart();
            }
        });
         */

        btnCloseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
    //----------------------------------------------------------------------------------------------


    //--------------------------- Retrieve User Settings (preference & system) ---------------------
    public void GetUserPreference()
    {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        //Query based on current user ID
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Query query = dbRef.child("Users").child(UserID).child("Settings").child("System");

        //check if current user is logged in
        if (fUser != null)
        {
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        if (snapshot.getValue().toString().trim().equals("Metric") )
                        {
                            systemPreference = "metric";
                        }
                        else
                        {
                            systemPreference= "imperial";
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error", error.getMessage());
                }
            });
        } else
        {
            Toast.makeText(getContext(),"No preference selected.",Toast.LENGTH_SHORT).show();
        }
    }
    //----------------------------------------------------------------------------------------------

    private void getGPS()
    {

    }


    //--------------------------- Method changes appearance of heart -------------------------------
    private void ChangeHeart() {

        /*
        //Change appearance of image button based on what the image already is
        if (btnSmallHeart.getTag() == "1"){
            btnSmallHeart.setImageResource(R.drawable.ic_small_heart_filled);
            btnSmallHeart.setTag("2");
        } else {
            btnSmallHeart.setImageResource(R.drawable.ic_small_heart_unfilled);
            btnSmallHeart.setTag("1");
        } */
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        getCurrentLocation();
        GetUserPreference();

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
        destinationLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        markerInteraction(marker);
    }

    //-----------------------------00oo0o0o-- MAP INTEGRATION --00oo0o0o----------------------------
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

                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();

                    mMap.setMyLocationEnabled(true);

                    currentLatLng = new LatLng(currentLat, currentLong);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                }
            }
        });
    }

    //----------------------------------- Retrieve direction info ----------------------------------
    private void direction(String title, double latitude, double longitude)
    {
        GetUserPreference();

        if (newMarker != null)
        {
            newMarker.remove();
            mMap.clear();
        }

        String destinationLatLong = destinationLatLng.latitude + ", " + destinationLatLng.longitude;
        double destinationLat = destinationLatLng.latitude, destinationLong = destinationLatLng.longitude;

        String originLatLong = currentLatLng.latitude + ", " + currentLatLng.longitude;
        double originLat = currentLatLng.latitude, originLong = currentLatLng.longitude;

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", destinationLatLong) // + lat + ", " + lng   Where you want to go AKA the maker through the Find Routes button
                .appendQueryParameter("origin", originLatLong) //  + currentLat + ", " + currentLng    Current Location
                .appendQueryParameter("mode", "driving") // Will you be walking, driving or jogging
                .appendQueryParameter("units", systemPreference)
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

                        JSONArray distanceAndDuration = null;
                        for (int i = 0; i < routes.length(); i++)
                        {
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                            JSONObject legsObjects = legs.getJSONObject(0);

                            JSONObject distance = legsObjects.getJSONObject("distance");
                            setDistanceToLocation(distance.getString("text"));

                            JSONObject time = legsObjects.getJSONObject("duration");
                            setDurationToLocation(time.getString("text"));

                            //Method to load the route info into the popup dialogue
                            LoadRouteInfo(title, latitude, longitude, durationToLocation, distanceToLocation);

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
                            polylineOptions.width(15); // To change the thickness of the line
                            polylineOptions.color(ContextCompat.getColor(getContext(), R.color.custom1));
                            polylineOptions.geodesic(true);
                        }
                        mMap.addPolyline(polylineOptions);

                        newMarker = mMap.addMarker( new MarkerOptions().position(new LatLng(originLat, originLong))
                                .title("Your current Location"));
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

        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }
    //----------------------------------------------------------------------------------------------

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
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101)
        {
            if (resultCode == RESULT_OK)
            {
                getCurrentLocation();
            }
            if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(getContext(), "Please enable your location services.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeMap()
    {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(16);
        locationRequest.setFastestInterval(3000);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices.getSettingsClient(getContext())
                .checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>()
        {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task)
            {
                try
                {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    getCurrentLocation();
                }
                catch (ApiException e)
                {
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED)
                    {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try
                        {
                            resolvableApiException.startResolutionForResult(getActivity(), 101);
                        }
                        catch (IntentSender.SendIntentException sendIntentException)
                        {
                            sendIntentException.printStackTrace();
                        }
                        if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE)
                        {
                            Toast.makeText(getContext(), "Setting not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    //-----------------------------00oo0o0o-- MAP INTEGRATION --00oo0o0o----------------------------


    //--------------------------------------- POI OnClick ------------------------------------------
    @Override
    public void onPoiClick(@NonNull PointOfInterest poi)
    {
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

                    ////--0o0o0oo DEBUG 0o0o0oo--///
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

                    destinationLatLng = new LatLng(poi.latLng.latitude, poi.latLng.longitude);
                    markerInteraction(poi, place);
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
    //----------------------------------------------------------------------------------------------


    //-------------------------------- Dialogue box: POI onClick -----------------------------------
    public void markerInteraction(PointOfInterest poi, Place place)
    {
        Dialog dialog = new Dialog(getContext(), R.style.DialogStyle);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.marker_details);

        //Finding ID's
        TextView markerName = dialog.findViewById(R.id.markerName);
        TextView txtNumber = dialog.findViewById(R.id.txtNum);
        ImageButton btn_Route = dialog.findViewById(R.id.btn_route);
        ImageButton btn_add_fav = dialog.findViewById(R.id.btn_add_fav);

        //Setting texts
        markerName.setText(poi.name);

        if (place.getPhoneNumber() != null && !place.getPhoneNumber().isEmpty()) {
            txtNumber.setText("Contact: " + place.getPhoneNumber());
        } else {
            txtNumber.setText("No contact information available.");
        }


        btn_Route.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getCurrentLocation();
                direction(poi.name, poi.latLng.latitude, poi.latLng.longitude);
                dialog.dismiss();
            }
        });
        btn_add_fav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CheckDuplication(poi.name, poi.latLng.latitude, poi.latLng.longitude, poi.placeId);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    //----------------------------------------------------------------------------------------------


    //-------------------------------- Dialogue box: Marker onClick --------------------------------
    public void markerInteraction(Marker marker)
    {
        Dialog dialog = new Dialog(getContext(), R.style.DialogStyle);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.marker_details);

        //Finding ID's
        TextView markerName = dialog.findViewById(R.id.markerName);
        TextView txtNumber = dialog.findViewById(R.id.txtNum);
        ImageButton btn_Route = dialog.findViewById(R.id.btn_route);
        ImageButton btn_add_fav = dialog.findViewById(R.id.btn_add_fav);

        //Setting texts
        markerName.setText(marker.getTitle());
        txtNumber.setText("No contact information available.");

        btn_Route.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getCurrentLocation();
                direction(marker.getTitle(), marker.getPosition().latitude, marker.getPosition().longitude);
                dialog.dismiss();
            }
        });
        btn_add_fav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CheckDuplication(marker.getTitle(), marker.getPosition().latitude, marker.getPosition().longitude, marker.getId());
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    //----------------------------------------------------------------------------------------------


    //------------------------------- Method to populate route info card ---------------------------
    private void LoadRouteInfo(String title, double latitude, double longitude, String estTime, String estDis) {

         routeTitle.setText(title);
         routeAddress.setText(String.valueOf(latitude));
         routeOther.setText(String.valueOf(longitude));
         routeEstTime.setText(estTime);
         routeEstDis.setText(estDis);

        btnOpenInfo.setVisibility(View.VISIBLE);
        infoLayout.setVisibility(View.VISIBLE);
    }
    //----------------------------------------------------------------------------------------------


    //------------------------------ Method to add location to favourites db -----------------------
    private void AddLocationToDB(String title, double lat, double lon, String id) {

        //Retrieving current user's ID
        String userID = UserID;

        DatabaseReference ref = dbUsersRef.child(userID).child("Favourites");

        fav.setTitle(title);
        fav.setLatitude(lat);
        fav.setLongitude(lon);
        fav.setLocationID(id);

        ref.push().setValue(fav);

        Toast.makeText(getContext(), title + " has successfully been added to your favourites.", Toast.LENGTH_LONG).show();

    }
    //----------------------------------------------------------------------------------------------


    //-------------------------- Check if POI already exists in the db -----------------------------
    private void CheckDuplication(String title, double lat, double lon, String id) {

        //Retrieving current user's ID
        String userID = UserID;

        Query query = dbUsersRef.child(userID).child("Favourites").orderByChild("locationID").equalTo(id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Toast.makeText(getContext(), "This location has already been added to your favourites.",Toast.LENGTH_LONG).show();
                }
                else {  AddLocationToDB(title, lat, lon, id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------
}
//References:
//Helped with getting Google Directions API to work - https://www.youtube.com/watch?v=CSbmlp61zLg&t=169s&ab_channel=CodeWorked