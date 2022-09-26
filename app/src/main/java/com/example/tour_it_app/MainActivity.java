package com.example.tour_it_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomView;
    private DrawerLayout mainDrawer;
    private Button btnSearch;

    HomeFragment homeFrag = new HomeFragment();
    SettingsFragment settingsFrag = new SettingsFragment();
    FavouritesFragment favouritesFrag = new FavouritesFragment();


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

        //by default, load the home screen
        bottomView.setSelectedItemId(R.id.bttm_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,homeFrag).commit();

        //Bottom Navigation Bar On Click
        bottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(MenuItem item) {

               switch (item.getItemId()){
                   case R.id.bttm_home:
                       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,homeFrag).commit();
                       return true;
                   case R.id.bttm_settings:
                       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,settingsFrag).commit();
                       return true;
                   case R.id.bttm_favourites:
                       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,favouritesFrag).commit();
                       return true;
               }

               return false;
           }
       });



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


    //------------------------------------ Search Dialogue -----------------------------------------
    public void ClickSearch(View view) {
        ShowSearchDialogue();
    }
    private void ShowSearchDialogue() {
        SearchFragment dialog = new SearchFragment();
        dialog.show(getSupportFragmentManager(), "Search Dialogue");
    }
    //------------------------------------ Search Dialogue -----------------------------------------
}