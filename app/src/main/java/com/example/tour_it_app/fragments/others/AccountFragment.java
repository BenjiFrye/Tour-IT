package com.example.tour_it_app.fragments.others;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tour_it_app.MainActivity;
import com.example.tour_it_app.R;
import com.example.tour_it_app.startup.Login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    //Component variables
    private EditText txtEmail;
    private AppCompatButton btnResetPass;
    private Activity activity = getActivity();

    //Firebase variables
    private FirebaseAuth mAuth;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        //Finding ID's
        txtEmail = getView().findViewById(R.id.edtAccEmail);
        btnResetPass = getView().findViewById(R.id.btnConPassword);

        MainActivity mainAct = new MainActivity();
        String currentEmail = mainAct.currentEmail;

        //Listener
        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check that an email has been entered
                if (!txtEmail.getText().toString().isEmpty())
                {
                    String email = txtEmail.getText().toString();

                    //Check that the email matches the email that is used to log in
                    if (currentEmail.equals(email)) {
                        ResetPassword(email);
                    }
                    else {
                        //If email does not match current logged in email
                        Toast.makeText(getActivity(),"Please enter your email that you logged in with",Toast.LENGTH_SHORT).show();
                    }


                } else {
                    //If no email is entered
                    Toast.makeText(getActivity(),"Please enter your email",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //------------------------- Sending reset password link to email -------------------------------
    private void ResetPassword(String email) {

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(getActivity(),"A reset link has been sent to your email", Toast.LENGTH_LONG).show();
                txtEmail.setText("");
                Logout();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getActivity(),"Email could not be found. Please make sure this email is registered", Toast.LENGTH_LONG).show();
                txtEmail.setText("");
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    //---------------------------------- Log user out ----------------------------------------------
    private void Logout() {

        Intent i = new Intent(getActivity(), Login.class);
        startActivity(i);
    }
    //----------------------------------------------------------------------------------------------
}