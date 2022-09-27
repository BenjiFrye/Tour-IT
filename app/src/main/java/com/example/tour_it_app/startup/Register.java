package com.example.tour_it_app.startup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.tour_it_app.R;

public class Register extends AppCompatActivity {

    // ---------------- * Component declaration * ------------------ //

    private AppCompatButton btnSignUp;
    private TextView btnLogin3;

    // ------------------------------------------------------------ //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnSignUp = findViewById(R.id.btn_sign_up);
        btnLogin3 = findViewById(R.id.btn_login_here_2);

        // --------------------------------- * Listeners * -------------------------------------- //

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: User Account creation implementation

                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
            }
        });

        btnLogin3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(Register.this, Login.class);
               startActivity(i);
            }
        });


        // -------------------------------------------------------------------------------------- //
    }
}