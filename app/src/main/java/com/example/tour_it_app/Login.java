package com.example.tour_it_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    // ---------------- * Component declaration * ------------------ //

    private AppCompatButton btnLogin2;
    private AppCompatButton btnRegister;
    private TextView txtForgotPassword;

    // ------------------------------------------------------------ //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin2 = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        txtForgotPassword = findViewById(R.id.btn_forgot_password);

        // --------------------------------- * Listeners * -------------------------------------- //

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Login.this, "Forgot password dialogue",Toast.LENGTH_SHORT).show();
            }
        });
        // -------------------------------------------------------------------------------------- //
    }
}