package com.example.inventorytracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //instantiate db helper
        DbHelper db = new DbHelper(this);

        //reference UI
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        Button createAccount = findViewById(R.id.create_account);
        ProgressBar loading = findViewById(R.id.loading);

        //move to create account page
        createAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
            startActivity(intent);
        });

        //log user in when login is clicked
        login.setOnClickListener(v -> {

            loading.setVisibility(View.VISIBLE);

            //get user input from login page
            String usernameText = username.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            //input validation
            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Missing input, enter a username and password",
                        Toast.LENGTH_SHORT).show();

                loading.setVisibility(View.GONE);
                return;
            }

            //check if user exists in db
            boolean check = db.checkUserLogin(usernameText, passwordText);

            loading.setVisibility(View.GONE);


            if (check) {
                Toast.makeText(LoginActivity.this, "Login Successful",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, InventoryGrid.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(LoginActivity.this, "Invalid username or password",
                        Toast.LENGTH_SHORT).show();
            }





        });



    }




}
