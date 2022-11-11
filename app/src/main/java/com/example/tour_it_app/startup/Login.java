package com.example.tour_it_app.startup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tour_it_app.MainActivity;
import com.example.tour_it_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity
{
    //Component variables
    private AppCompatButton btnLogin;
    private AppCompatButton btnRegister;
    private TextView txtForgotPassword;
    private EditText txtEmail;
    private EditText txtPassword;
    private CircularProgressIndicator indicator;

    //Firebase variables
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Finding ID's
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        txtForgotPassword = findViewById(R.id.btn_forgot_password);
        txtEmail = findViewById(R.id.edtLoginEmail);
        txtPassword = findViewById(R.id.edtLoginPass);
        indicator = findViewById(R.id.indicator);

        //New instance
        mAuth = FirebaseAuth.getInstance();

        //Default operations
        btnLogin.setVisibility(View.VISIBLE);
        indicator.setVisibility(View.INVISIBLE);

        //Listeners
        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Check that user has entered their email and password
                if (!txtEmail.getText().toString().isEmpty() && !txtPassword.getText().toString().isEmpty()) {
                    btnLogin.setVisibility(View.INVISIBLE);
                    indicator.setVisibility(View.VISIBLE);
                LoginUser();
                } else {
                    btnLogin.setVisibility(View.VISIBLE);
                    indicator.setVisibility(View.INVISIBLE);
                    Toast.makeText(Login.this, "Please enter both your email and password.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                ResetPassword();
            }
        });

    }

    //-------------------------------Logging user in implementation---------------------------------
    private void LoginUser()
    {
        //Retrieving email and password string values
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        //Check for email and password combination in firebase authentication
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    //Check if their email has been verified
                    if (!currentUser.isEmailVerified())
                    {
                        //If email has not been verified
                        btnLogin.setVisibility(View.VISIBLE);
                        indicator.setVisibility(View.INVISIBLE);
                        Toast.makeText(Login.this, "Unable to login. Please verify your email", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //If email has been valid and password and email combination is authenticated
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }
                else
                {
                    //If email and password combination fails to authenticate
                    btnLogin.setVisibility(View.VISIBLE);
                    indicator.setVisibility(View.INVISIBLE);
                    Toast.makeText(Login.this, "Unable to authenticate email and password combination. " +
                            "Please make sure this account exists.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    //-------------------------------- Forgot Password Dialogue ------------------------------------
    private void ResetPassword()
    {

        //New dialogue instance
        Dialog dialog = new Dialog(this, R.style.DialogStyle);

        //Prevent user from click away
        dialog.setCanceledOnTouchOutside(false);
        //Show register dialogue layout
        dialog.setContentView(R.layout.dialogue_reset_password);

        //Finding Id's
        Button btnChange = dialog.findViewById(R.id.btnChangePass);
        Button btnCancel = dialog.findViewById(R.id.btnCancelReset);
        EditText txtChangeEmail = dialog.findViewById(R.id.edtChangeEmail);

        btnChange.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!txtChangeEmail.getText().toString().isEmpty()) {

                    String email = txtChangeEmail.getText().toString();

                    mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            dialog.dismiss();
                            Toast.makeText(Login.this,"A reset link has been sent to your email.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(Login.this,"Email could not be found. Please make sure this email is registered.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                {
                    Toast.makeText(Login.this, "Please enter your email",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
    //----------------------------------------------------------------------------------------------
}