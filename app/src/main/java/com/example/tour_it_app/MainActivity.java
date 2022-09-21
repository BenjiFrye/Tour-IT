package com.example.tour_it_app;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout mainDrawer;
    private Button btnSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---------------------------- Main Navigation Drawer layout -------------------------------
        //Drawer layout instance to toggle menu icon to open
        //and back button to close drawer
        mainDrawer = findViewById(R.id.layout);
        //------------------------------------------------------------------------------------------

        //-------------------------------- Search Drawer layout ------------------------------------
        btnSearch = findViewById(R.id.btnMainSearch);
        //------------------------------------------------------------------------------------------

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
    public void ClickLogo(View view)
    {
        //Closes the drawer
        closeDrawer(mainDrawer);
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


    public void ClickSearch(View view) {
        ShowSearchDialogue();
    }
    private void ShowSearchDialogue() {
        SearchFragment dialog = new SearchFragment();
        dialog.show(getSupportFragmentManager(), "Search Dialogue");
    }
}