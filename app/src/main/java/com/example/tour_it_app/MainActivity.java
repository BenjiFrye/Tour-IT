package com.example.tour_it_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity{

    //Variables
    private NavigationView sideNavView;
    private BottomNavigationView bottomView;
    private DrawerLayout mainDrawer;
    private Button btnSearch;
    private TextView txtHeading;

    //New instances
    HomeFragment homeFrag = new HomeFragment();
    SettingsFragment settingsFrag = new SettingsFragment();
    FavouritesFragment favouritesFrag = new FavouritesFragment();
    AccountFragment accFrag = new AccountFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---------------------------- Main Navigation Drawer layout -------------------------------
        //Drawer layout instance to toggle menu icon to open
        //and back button to close drawer
        mainDrawer = findViewById(R.id.layout);
        //------------------------------------------------------------------------------------------

        //Finding Id's
        btnSearch = findViewById(R.id.btnMainSearch);
        bottomView = findViewById(R.id.bottomNavView);
        sideNavView = findViewById(R.id.mainNavView);
        txtHeading = findViewById(R.id.txtPageName);

        //by default, load the home screen
        SetTopBarMain();
        bottomView.setSelectedItemId(R.id.bttm_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,homeFrag).commit();

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
                        ShowSearchDialogue();
                        closeDrawer(mainDrawer);
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


    //Change appearance of top bar when home page is open
    private void SetTopBarMain() {
        txtHeading.setText("LandmarkName");
        txtHeading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pin,0,0,0);
        btnSearch.setVisibility(View.VISIBLE);
    }

    //Change appearance of top bar if any other page is open
    private void SetTopBarOther(String heading) {
        txtHeading.setText(heading);
        txtHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        btnSearch.setVisibility(View.INVISIBLE);
    }

    //----------------------------------- Drawer Management Code -----------------------------------
    public void ClickMenu(View view)
    {
        //Open the drawer
        openDrawer(mainDrawer);
    }
    public static void openDrawer(DrawerLayout drawerLayout)
    {
        //Open the drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout)
    {
        //Close drawer layout
        //Check condition
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            //When drawer is open
            //Close Drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    //----------------------------------------------------------------------------------------------


    //------------------------------------ Search Dialogue -----------------------------------------
    public void ClickSearch(View view) {
        ShowSearchDialogue();
    }
    private void ShowSearchDialogue() {
        SearchFragment dialog = new SearchFragment();
        dialog.show(getSupportFragmentManager(), "Search Dialogue");
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