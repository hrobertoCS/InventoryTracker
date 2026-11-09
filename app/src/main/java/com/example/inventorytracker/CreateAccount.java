package com.example.inventorytracker;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //instantiates db helper
        DbHelper db = new DbHelper(this);


        //reference UI
        EditText username = findViewById(R.id.newUsername);
        EditText password = findViewById(R.id.newPassword);
        EditText firstName = findViewById(R.id.firstName);
        EditText lastName = findViewById(R.id.lastName);

        Button createAccount = findViewById(R.id.confirmCreateAccount);

        TextView back = findViewById(R.id.back);

        //close screen
        back.setOnClickListener(v -> {
            finish();
        });


        //create account when user clicks create account button
        createAccount.setOnClickListener(v -> {

            //get user input from create account page
            String usernameText = username.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String firstNameText = firstName.getText().toString().trim();
            String lastNameText = lastName.getText().toString().trim();

            //input validation
            if (usernameText.isEmpty() || passwordText.isEmpty() || firstNameText.isEmpty() || lastNameText.isEmpty()) {
                Toast.makeText(CreateAccount.this, "Missing input, please fill out all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //add login info to db
            boolean create = db.addUserLogin(usernameText, passwordText, firstNameText, lastNameText);

            if (create) {
                Toast.makeText(CreateAccount.this, "Account Successfully Created",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(CreateAccount.this, "Username already exists",
                        Toast.LENGTH_SHORT).show();
            }



        });








    }
}