package com.example.tour_it_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class AccountActivity extends AppCompatActivity {

    private DrawerLayout mainDrawer;
    private TextView txtHeading;
    private NavigationView sideNavView;
    private Button btnSearch;

    HomeFragment homeFrag = new HomeFragment();
    SettingsFragment settingsFrag = new SettingsFragment();
    FavouritesFragment favouritesFrag = new FavouritesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //---------------------------- Main Navigation Drawer layout -------------------------------
        //Drawer layout instance to toggle menu icon to open
        //and back button to close drawer
        mainDrawer = findViewById(R.id.layout2);
        //------------------------------------------------------------------------------------------

        //Finding Id's
        txtHeading = findViewById(R.id.txtPageName);
        sideNavView = findViewById(R.id.mainNavView);
        btnSearch = findViewById(R.id.btnMainSearch);

        //updating the appearance of the header
        SetTopBarAccount();

        //Main Navigation Side Bar menu item on Click
        sideNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.main_home:
                    case R.id.main_fav:
                    case R.id.main_sett:
                    case R.id.main_search:
                        Intent i = new Intent(AccountActivity.this, MainActivity.class);
                        startActivity(i);
                        closeDrawer(mainDrawer);
                        return true;
                    case R.id.main_acc:
                        ////
                        closeDrawer(mainDrawer);
                        return true;
                    case R.id.main_logout:
                        ///
                        return true;
                }
                return false;
            }
        });

    }

    private void SetTopBarAccount() {
        txtHeading.setText("Account");
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
    //----------------------------------- Drawer Management Code -----------------------------------
}