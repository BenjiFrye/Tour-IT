package com.example.tour_it_app.fragments.others;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tour_it_app.R;
import com.google.android.material.badge.BadgeUtils;

public class FavouritesFragment extends Fragment {

    //Component Variables
    private GridLayout layout;
    private View cardView;
    private TextView txtName;
    private TextView txtArea;
    private TextView txtAddress;
    private ImageButton btnImageHeart;

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

        //Listeners
        btnImageHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnImageHeart.isPressed()) {
                    ChangeHeart();
                }
            }
        });

        //Call method to add card view
        AddCard("Robben Island", "Cape Town, WC", "Table Bay");

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

    }
    //----------------------------------------------------------------------------------------------

    //-------------------------- This method will display populated card views ---------------------
    private void AddCard(String name, String area, String address) {

        txtName = cardView.findViewById(R.id.txtLandName);
        txtArea = cardView.findViewById(R.id.txtLandArea);
        txtAddress = cardView.findViewById(R.id.txtLandAddress);

        txtName.setText(name);
        txtArea.setText(area);
        txtAddress.setText(address);

        btnImageHeart.setTag("1");

        layout.addView(cardView);
    }
    //----------------------------------------------------------------------------------------------
}