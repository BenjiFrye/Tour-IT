package com.example.tour_it_app;

import static com.example.tour_it_app.fragments.landmarks.HomeFragment.mMap;
import static com.example.tour_it_app.fragments.landmarks.HomeFragment.newMarker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tour_it_app.fragments.others.AccountFragment;
import com.example.tour_it_app.fragments.others.FavouritesFragment;
import com.example.tour_it_app.fragments.landmarks.HomeFragment;
import com.example.tour_it_app.fragments.others.SettingsFragment;
import com.example.tour_it_app.object_classes.Users;
import com.example.tour_it_app.startup.GetStarted;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    //Component Variables
    private NavigationView sideNavView;
    private BottomNavigationView bottomView;
    private DrawerLayout mainDrawer;
    private View searchFrag;
    private RelativeLayout reLayout;
    private TextView txtHeading;
    private TextView txtNavName;
    private TextView txtNavEmail;

    //Searching variables
    public static PlacesClient placesClient;

    //New instances
    HomeFragment homeFrag = new HomeFragment();
    SettingsFragment settingsFrag = new SettingsFragment();
    FavouritesFragment favouritesFrag = new FavouritesFragment();
    AccountFragment accFrag = new AccountFragment();

    //Type variables
    public static String UserID = "No ID"; //holds id of currently logged in user
    public static String currentEmail = "someone.example@gmail.com"; //holds email of currently logged in user
    public static String currentName = "Guest User"; //holds email of currently logged in user

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Run method that retrieves details of currently logged in user
        FetchUserDetails();

        //---------------------------- Main Navigation Drawer layout -------------------------------
        //Drawer layout instance to toggle menu icon to open
        //and back button to close drawer
        mainDrawer = findViewById(R.id.layout);
        //------------------------------------------------------------------------------------------

        //Finding Id's
        bottomView = findViewById(R.id.bottomNavView);
        sideNavView = findViewById(R.id.mainNavView);
        txtHeading = findViewById(R.id.txtPageName);
        txtNavName = findViewById(R.id.txtNav_Name);
        txtNavEmail = findViewById(R.id.txtNav_Email);
        searchFrag = findViewById(R.id.autoComplete_fragment).getRootView();
        reLayout = findViewById(R.id.reLayout);

        //Changing appearance of search fragment
        EditText txt = searchFrag.findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input);
        txt.setTextColor(Color.WHITE);
        txt.setTextSize(22.0f);

        //by default, load the home screen
        SetTopBarMain();
        bottomView.setSelectedItemId(R.id.bttm_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,homeFrag).commit();

        //Implementation for autocomplete fragment
        AutoCompleteMethod(InitialiseAutoComplete());
        //variable used to run view
        final View root = searchFrag.getRootView();

        //Bottom Navigation Bar menu item On Click
        bottomView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(MenuItem item) {

               switch (item.getItemId()){
                   case R.id.bttm_home:
                       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,homeFrag).commit();
                       SetTopBarMain();
                       return true;
                   case R.id.bttm_settings:
                       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,settingsFrag).commit();
                       SetTopBarOther("Settings");
                       return true;
                   case R.id.bttm_favourites:
                       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,favouritesFrag).commit();
                       SetTopBarOther("Favourites");
                       return true;
               }
               return false;
           }
       });

        //Main Navigation Side Bar menu item on Click
        sideNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.main_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, homeFrag).commit();
                        closeDrawer(mainDrawer);
                        SetTopBarMain();
                        bottomView.setSelectedItemId(R.id.bttm_home);
                        return true;
                    case R.id.main_search:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, homeFrag).commit();
                        bottomView.setSelectedItemId(R.id.bttm_home);
                        closeDrawer(mainDrawer);
                        RunSearch(root);
                        return true;
                    case R.id.main_fav:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, favouritesFrag).commit();
                        closeDrawer(mainDrawer);
                        SetTopBarOther("Favourites");
                        bottomView.setSelectedItemId(R.id.bttm_favourites);
                        return true;
                    case R.id.main_acc:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, accFrag).commit();
                        closeDrawer(mainDrawer);
                        SetTopBarOther("Account");
                        bottomView.getMenu().findItem(R.id.bttm_invisible).setChecked(true);
                        return true;
                    case R.id.main_sett:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, settingsFrag).commit();
                        closeDrawer(mainDrawer);
                        SetTopBarOther("Settings");
                        bottomView.setSelectedItemId(R.id.bttm_settings);
                        return true;
                    case R.id.main_logout:
                        LogoutDialogue();
                        return true;
                }
                return false;
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------- AutoComplete Fragment Code -------------------------------
    //Method opens the search fragment
    private void RunSearch(View root) {
        root.post(new Runnable() {
            @Override
            public void run() {
                root.findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input).performClick();
            }
        });
    }

    //Initialise the AutoCompleteSupportFragment
    private AutocompleteSupportFragment InitialiseAutoComplete() {

        AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autoComplete_fragment);

        return autocompleteSupportFragment;
    }

    //Implementation
    public void AutoCompleteMethod(AutocompleteSupportFragment acFrag) {

        if (!Places.isInitialized())
        {
            Places.initialize(this, getString(R.string.map_key));
        }

        placesClient = Places.createClient(getApplication());

        acFrag.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        acFrag.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(@NonNull Place place)
            {
                mMap.clear();
                final LatLng latLng = place.getLatLng();

                Log.i("LOG", "OnPlaceSelected: " + latLng.latitude + "\n" + latLng.latitude);

                if (newMarker != null)
                {
                    newMarker.remove();
                }

                String location = place.getName();
                List<Address> addressList = null;

                if (location != null || !location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(getApplication());
                    try
                    {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }
                    catch (IOException IOexception)
                    {
                        Log.e("ERROR: ", "IOexception: " + IOexception.getMessage());
                    }

                    if (addressList.size() > 0)
                    {
                        Address address = addressList.get(0);
                        LatLng latLng2 = new LatLng(address.getLatitude(), address.getLongitude());
                        double latitude = Math.round(address.getLatitude());
                        double longitude = Math.round(address.getLongitude());
                        newMarker = mMap.addMarker(new MarkerOptions()
                                .position(latLng2)
                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo1_blacktext))  GET THE ICON FIRST
                                .title(location)
                                .snippet("Latitude: " + latitude + " Longitude: " + longitude + "\nClick For more details"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2,13));
                    }
                    else
                    {
                        Toast.makeText(getApplication(), "More than one location found!",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplication(), "Location could not be found.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull Status status)
            {

            }
        });
    }
    //----------------------------------------------------------------------------------------------

    //-------------------------------- Top Bar Heading appearance ----------------------------------
    //Change appearance of top bar when home page is open
    private void SetTopBarMain() {
        txtHeading.setText("");
        reLayout.setVisibility(View.VISIBLE);
    }

    //Change appearance of top bar if any other page is open
    private void SetTopBarOther(String heading) {
        txtHeading.setText(heading);
        reLayout.setVisibility(View.INVISIBLE);
    }
    //----------------------------------------------------------------------------------------------


    //----------------------------------- Drawer Management Code -----------------------------------
    public void ClickMenu(View view)
    {
        //Open the drawer
        openDrawer(mainDrawer);
    }
    public static void openDrawer(DrawerLayout drawerLayout)
    {
        //Open the drawer layout
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

    public static void closeDrawer(DrawerLayout drawerLayout)
    {
        //Close drawer layout
        //Check condition
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
        {
            //When drawer is open
            //Close Drawer
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }
    //----------------------------------------------------------------------------------------------

    //---------------------------- Getting currently logged in user details ------------------------
    public void FetchUserDetails() {

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        //Query based on current user ID
        UserID = fUser.getUid().toString();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Query query = dbRef.child("Users").child(UserID).child("Account").orderByChild("userID").equalTo(UserID);

        //check if current user is logged in
        if (fUser != null) {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //Retrieving user details
                            Users accUser = ds.getValue(Users.class);
                            currentEmail = accUser.getEmail();
                            currentName = accUser.getFirstName() + " " + accUser.getLastName();
                            SetNavDrawerUserDetails();
                            Log.d("Email","accUser");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error", error.getMessage());
                }
            });

        } else {
            //No user is logged in
            Toast.makeText(this,"No user is logged in.",Toast.LENGTH_SHORT).show();
        }

    }
    //----------------------------------------------------------------------------------------------

    //----------------------------- Setting the name nav drawer ------------------------------------
    private void SetNavDrawerUserDetails() {
        txtNavName.setText(currentName);
        txtNavEmail.setText(currentEmail);
    }
    //----------------------------------------------------------------------------------------------


    //------------------------------------ Logout Dialogue -----------------------------------------
    private void LogoutDialogue()
    {
        Dialog dialog = new Dialog(this, R.style.DialogStyle);
        dialog.setCanceledOnTouchOutside(false); //To prevent a user from clicking away
        dialog.setContentView(R.layout.dialogue_logout);

        AppCompatButton btnLogout = dialog.findViewById(R.id.btnYes);
        AppCompatButton btnDontLogout = dialog.findViewById(R.id.btnNo);

        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), GetStarted.class));
                dialog.dismiss();
            }
        });

        btnDontLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    //----------------------------------------------------------------------------------------------


}