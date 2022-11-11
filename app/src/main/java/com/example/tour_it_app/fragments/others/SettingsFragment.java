package com.example.tour_it_app.fragments.others;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.tour_it_app.R;
import com.example.tour_it_app.object_classes.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {

    //Component variables
    private RadioGroup rgSystem;
    private RadioGroup rgPreference;
    private Button btnSystem;
    private Button btnPreference;

    //Type variables
    private static String userID;
    private String system = "Metric";
    private String preference = "Popular";

    //Firebase variables
    private FirebaseDatabase db= FirebaseDatabase.getInstance();
    private DatabaseReference ref = db.getReference("Users");
    private FirebaseAuth mAuth;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        //Finding Id's
        rgPreference = getView().findViewById(R.id.rgPreference);
        rgSystem = getView().findViewById(R.id.rgSystem);
        btnPreference = getView().findViewById(R.id.btnConPref);
        btnSystem = getView().findViewById(R.id.btnConSys);

        //Firebase instances
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        FetchPreference();
        FetchSystem();

        //Listeners
        btnPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePref();
            }
        });

        btnSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSys();
            }
        });

    }

    //------------------------ Retrieving settings based on userID in db ---------------------------
    private void FetchPreference() {

        DatabaseReference newRef = ref.child(userID).child("Settings");
        Query query = newRef.child("Preference");

        //check if current user is logged in
        if (mAuth != null) {

            //Retrieving setting for preference
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        preference = snapshot.getValue().toString();
                        SetPreference(preference);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void FetchSystem() {

        DatabaseReference newRef = ref.child(userID).child("Settings");
        Query query = newRef.child("System");

        //check if current user is logged in
        if (mAuth != null) {
            //Retrieving setting for system
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        system = snapshot.getValue().toString();
                        SetSystem(system);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error", error.getMessage());
                }
            });
        }
    }

    //----------------------------------------------------------------------------------------------

    //-------------------------- Update the settings in the database -------------------------------
    private void UpdatePref() {

        DatabaseReference newRef = ref.child(userID).child("Settings").child("Preference");

        //Retrieving the content from selected radio button
        int id = rgPreference.getCheckedRadioButtonId();
        RadioButton rbPref = (RadioButton) getView().findViewById(id);
        String pref = rbPref.getText().toString();

        //Setting the new value in database
        newRef.setValue(pref);
    }

    private void UpdateSys() {

        DatabaseReference newRef = ref.child(userID).child("Settings").child("System");

        //Retrieving the content from selected radio button
        int id = rgSystem.getCheckedRadioButtonId();
        RadioButton rbSys = (RadioButton) getView().findViewById(id);
        String sys = rbSys.getText().toString();

        //Setting the new value in database
        newRef.setValue(sys);

    }
    //----------------------------------------------------------------------------------------------

    //---------------------------- Setting preference based on userID in db ------------------------
    private void SetPreference(String pref) {

        //Setting selection based on userID in db
        switch (pref) {
            case "Popular":
                rgPreference.check(R.id.rbPopular);
                break;
            case "Modern":
                rgPreference.check(R.id.rbModern);
                break;
            case "Historical":
                rgPreference.check(R.id.rbHistorical);
                break;
        }
    }
    //----------------------------------------------------------------------------------------------

    //------------------------------- Setting system based on userID in db -------------------------
    private void SetSystem(String sys) {

        //Setting selection based on userID in db
        switch (sys) {
            case "Imperial" :
                rgSystem.check(R.id.rbImperial);
                break;
            case "Metric":
                rgSystem.check(R.id.rbMetric);
                break;
        }
    }
    //----------------------------------------------------------------------------------------------



}