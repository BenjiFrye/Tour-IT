package com.example.tour_it_app.startup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.tour_it_app.R;

public class GetStarted extends AppCompatActivity {


    // ---------------- * Component declaration * ------------------ //

    private AppCompatButton btnGetStarted;
    private TextView btnLogin1;

    // ------------------------------------------------------------ //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        btnGetStarted = findViewById(R.id.btn_get_started);
        btnLogin1 = findViewById(R.id.btn_login_here_2);

        // --------------------------------- * Listeners * -------------------------------------- //

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GetStarted.this, Register.class);
                startActivity(i);
            }
        });

        btnLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GetStarted.this, Login.class);
                startActivity(i);
            }
        });

        // -------------------------------------------------------------------------------------- //
    }
}