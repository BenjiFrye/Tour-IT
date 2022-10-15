package com.example.tour_it_app.fragments.others;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tour_it_app.MainActivity;
import com.example.tour_it_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FavouritesFragment extends Fragment {

    //Component Variables
    private GridLayout layout;
    private TextView txtName;
    private TextView txtLat;
    private TextView txtLong;
    private ImageButton btnImageHeart;

    //Type variables
    private String title;
    private String latitude;
    private String longitude;

    public FavouritesFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        //Finding ID's
        layout = getView().findViewById(R.id.container);

        //Default operations
        RetrieveFavouritesData();

    }

    //--------------------------- Method changes appearance of heart -------------------------------
    private void ChangeHeart()
    {
        //Change appearance of image button based on what the image already is
        if (btnImageHeart.getTag() == "1"){
            RemoveFavourite();
            btnImageHeart.setImageResource(R.drawable.ic_heart_unfilled);
            btnImageHeart.setTag("2");
        } else {
            btnImageHeart.setImageResource(R.drawable.ic_heart_filled);
            btnImageHeart.setTag("1");
        }

    }
    //----------------------------------------------------------------------------------------------

    //-------------------------- This method retrieves favourites data from db ---------------------
    private void RetrieveFavouritesData()
    {
        MainActivity mainAct = new MainActivity();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();

        //Retrieving current user's ID
        String userID = mainAct.UserID;
        //Query retrieves data from the correct reference
        Query query = dataRef.child("Users").child(userID).child("Favourites");

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    for (DataSnapshot item : snapshot.getChildren())
                    {
                        title = item.child("title").getValue().toString();
                        latitude = item.child("latitude").getValue().toString();
                        longitude = item.child("longitude").getValue().toString();
                        AddCard(title, latitude, longitude);
                    }
                }
                else
                {
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    //-------------------------- This method will display populated card views ---------------------
    private void AddCard(String name, String lat, String lon)
    {
        //You need to INFLATE each card when they are created to display on the GridView
        View cardView = getLayoutInflater().inflate(R.layout.card_view_favourites, null);

        txtName = cardView.findViewById(R.id.txtLandName);
        txtLat = cardView.findViewById(R.id.txtLandLong);
        txtLong = cardView.findViewById(R.id.txtLandLat);

        txtName.setText(name);
        txtLat.setText(lat);
        txtLong.setText(lon);

        if (cardView.getParent() != null)
        {
            ((ViewGroup)cardView.getParent()).removeView(cardView);
        }

        layout.addView(cardView);

    }
    //----------------------------------------------------------------------------------------------

    //---------------------------- Method to remove a specific favourited item ---------------------
    private void RemoveFavourite()
    {
        //TODO: REMOVE FAVOURITE IMPLEMENTATION
    }
    //----------------------------------------------------------------------------------------------

}