package com.example.tour_it_app.fragments.others;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tour_it_app.MainActivity;
import com.example.tour_it_app.R;
import com.example.tour_it_app.object_classes.Favourites;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FavouritesFragment extends Fragment {

    //Component Variables
    private GridLayout layout;
    private View cardView;
    private TextView txtName;
    private TextView txtLat;
    private TextView txtLong;
    private ImageButton btnImageHeart;

    //Type variables
    private String userID;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //Finding ID's
        layout = getView().findViewById(R.id.container);
        cardView = getLayoutInflater().inflate(R.layout.card_view_favourites, null);
        btnImageHeart = cardView.findViewById(R.id.btnHeart);

        //Default operations
        RetrieveFavouritesData();

        //Listeners
        btnImageHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnImageHeart.isPressed()) {
                    ChangeHeart();
                }
            }
        });


    }

    //--------------------------- Method changes appearance of heart -------------------------------
    private void ChangeHeart() {

        //Change appearance of image button based on what the image already is
        if (btnImageHeart.getTag() == "1"){
            btnImageHeart.setImageResource(R.drawable.ic_heart_unfilled);
            btnImageHeart.setTag("2");
        } else {
            btnImageHeart.setImageResource(R.drawable.ic_heart_filled);
            btnImageHeart.setTag("1");
        }

        //TODO: Remove card view implementation
    }
    //----------------------------------------------------------------------------------------------

    //-------------------------- This method retrieves favourites data from db ---------------------
    private void RetrieveFavouritesData() {

        MainActivity mainAct = new MainActivity();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();

        //Retrieving current user's ID
        String userID = mainAct.UserID;
        //Query retrieves data from the correct reference
        Query query = dataRef.child("Users").child(userID).child("Favourites");


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    for (DataSnapshot item : snapshot.getChildren()) {

                        String title = item.child("title").getValue().toString();
                        String latitude = item.child("latitude").getValue().toString();
                        String longitude = item.child("longitude").getValue().toString();

                        //Display the card with the retrieved favourite
                        AddCard(title, latitude, longitude);

                        Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Snapshot does not exist",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    //----------------------------------------------------------------------------------------------

    //-------------------------- This method will display populated card views ---------------------
    private void AddCard(String name, String lat, String lon) {

        txtName = cardView.findViewById(R.id.txtLandName);
        txtLat = cardView.findViewById(R.id.txtLandLong);
        txtLong = cardView.findViewById(R.id.txtLandLat);

        txtName.setText(name);
        txtLat.setText(lat);
        txtLong.setText(lon);

        btnImageHeart.setTag("1");

        if (cardView.getParent() != null)  {
            ((ViewGroup)cardView.getParent()).removeView(cardView);
        }

        layout.addView(cardView);
    }
    //----------------------------------------------------------------------------------------------
}