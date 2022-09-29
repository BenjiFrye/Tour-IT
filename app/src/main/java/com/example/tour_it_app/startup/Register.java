package com.example.tour_it_app.startup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tour_it_app.R;
import com.example.tour_it_app.object_classes.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    //Component variables
    private AppCompatButton btnSignUp;
    private TextView btnLogin3;
    private EditText txtName;
    private EditText txtSurname;
    private EditText txtEmail;
    private EditText txtPass;
    private EditText txtConPass;

    //Firebase variables
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference dbUsersRef = db.getReference("Users");
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    //Type variables
    private String userID;
    private static final String TAG = "EmailPassword";
    private Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Finding ID's
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnLogin3 = findViewById(R.id.btn_login_here_2);
        txtName = findViewById(R.id.edtName);
        txtSurname = findViewById(R.id.edtSurname);
        txtEmail = findViewById(R.id.edtEmail);
        txtPass = findViewById(R.id.edtPass);
        txtConPass = findViewById(R.id.edtConPass);

        //New instance
        user = new Users();
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        //Listeners
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check if all fields are not empty and contain a value
                if (!txtName.getText().toString().isEmpty() && !txtSurname.getText().toString().isEmpty() &&
                    !txtEmail.getText().toString().isEmpty() && !txtPass.getText().toString().isEmpty() &&
                    !txtConPass.getText().toString().isEmpty())
                {
                    //Run method to check for input validation
                    CheckUserInput();
                } else
                {
                    //If one or more fields are empty
                    Toast.makeText(Register.this, "Please make sure all fields have been entered",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnLogin3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(Register.this, Login.class);
               startActivity(i);
            }
        });

    }


    /*
     * ---------------------- SUMMARY: Registering Users implementation ----------------------------
     */
    //---------------Checking if all inputs are valid (email, password matching)--------------------
    private void CheckUserInput() {

        String email = txtEmail.getText().toString();
        String pass = txtPass.getText().toString();
        String conPass = txtConPass.getText().toString();

        //Check if passwords are matching and email is valid
        if (pass.equals(conPass))
        {
            //Check if length of password is 6 or more
            if (pass.length() >= 6) {
                if (ValidateEmail(email)) {
                    //If email is valid, run method to register user
                    RegisterUser(email, pass);
                } else
                {
                    //If the email is not valid
                    Toast.makeText(Register.this,"Invalid email! Please try again.",Toast.LENGTH_SHORT).show();
                }
            } else {
                //If length of password is less than 6
                Toast.makeText(Register.this, "Password must contain at least 6 characters.", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            //If passwords do not match
            Toast.makeText(Register.this,  "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
        }

    }
    //----------------------------------------------------------------------------------------------

    //------------------Check for validity of email. Return true if it's valid----------------------
    private boolean ValidateEmail(String email) {

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        } else {
            return false;
        }

    }
    //----------------------------------------------------------------------------------------------

    //--------------------Register the user with authentication in firebase-------------------------
    private void RegisterUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            SendVerificationEmail(email);

                        } else
                        {
                            // If sign in fails, display error message to user
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //----------------------------------------------------------------------------------------------

    //-------------------------Method to send a verification email to user--------------------------
    private void SendVerificationEmail(String email) {

        //Retrieving string values of name and surname
        String name = txtName.getText().toString();
        String surname = txtSurname.getText().toString();
        userID = mAuth.getCurrentUser().getUid().toString();

        fUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    //If email has successfully been sent
                    DisplaySuccessDialogue();

                    //Run method to add the user to the database
                    AddUserToDatabase(name, surname, email, userID);
                    Log.d("onSuccess_TAG", "onSuccess: Email verification link sent");
                }
                else
                {
                    //If task fails to send a verification email
                    Log.e(TAG, "sendEmailVerification", task.getException());
                    Toast.makeText(Register.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    //----------------------Method to display the Success message dialogue--------------------------
    private void DisplaySuccessDialogue() {

        //New dialogue instance
        Dialog dialog = new Dialog(this, R.style.DialogStyle);

        //Prevent user from click away
        dialog.setCanceledOnTouchOutside(false);
        //Show register dialogue layout
        dialog.setContentView(R.layout.dialogue_register);

        Button btnOk = dialog.findViewById(R.id.btnOK);

        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), Login.class));
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    //----------------------------------------------------------------------------------------------

    //--------------------------Method to add the user to the database------------------------------
    private void AddUserToDatabase(String name, String surname, String email, String userID) {

        DatabaseReference ref = dbUsersRef.child(userID).child("Account");

        user.setFirstName(name);
        user.setLastName(surname);
        user.setEmail(email);
        user.setUserID(userID);

        ref.setValue(user);
    }
    //----------------------------------------------------------------------------------------------
}