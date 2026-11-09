package com.example.inventorytracker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.widget.TextView;
import android.telephony.SmsManager;



public class EditQuantity extends AppCompatActivity {


    private DbHelper db;
    private String itemId;
    private int currentQuantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_quantity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //instantiate db helper
        db = new DbHelper(this);

        //reference UI
        TextView itemQuantity = findViewById(R.id.itemQuantity);
        TextView itemName = findViewById(R.id.item_name);
        EditText editQuantity = findViewById(R.id.editQuantity);
        Button minusButton = findViewById(R.id.minus_button);
        Button plusButton = findViewById(R.id.plus_button);
        Button homeButton = findViewById(R.id.home_button);

        //read and display data
        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");
        String itemNameText = intent.getStringExtra("itemName");
        currentQuantity = intent.getIntExtra("itemQuantity", 0);

        itemName.setText(itemNameText + " - " + itemId);
        itemQuantity.setText("Quantity: " + currentQuantity);
        editQuantity.setText(String.valueOf(currentQuantity));

        //increase or decrease quantity
        minusButton.setOnClickListener(v -> {
            if (currentQuantity > 0) {
                currentQuantity--;
                itemQuantity.setText("Quantity: " + currentQuantity);
                editQuantity.setText(String.valueOf(currentQuantity));
            }
        });

        plusButton.setOnClickListener(v -> {
            currentQuantity++;
            itemQuantity.setText("Quantity: " + currentQuantity);
            editQuantity.setText(String.valueOf(currentQuantity));
        });


        //update quantity in db
        homeButton.setOnClickListener(v -> {

            String quantityText = editQuantity.getText().toString().trim();

            if (quantityText.isEmpty()) {
                Toast.makeText(EditQuantity.this, "Please enter a quantity",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int newQuantity;

            try {
                newQuantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException e) {
                Toast.makeText(EditQuantity.this, "Please enter a valid number",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (newQuantity < 0) {
                Toast.makeText(EditQuantity.this, "Please enter a positive number",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean update = db.updateItemQuantity(itemId, newQuantity);

            if (update) {
                String smsEnabled  = db.getSetting(InventoryContract.PermissionsPreference.KEY_SMS_PERMISSION);
                String phoneNumber = db.getSetting(InventoryContract.PermissionsPreference.KEY_SMS_PHONE_NUMBER);

                if ("true".equals(smsEnabled) && phoneNumber != null && newQuantity <= 5 && !phoneNumber.isEmpty()) {
                    sendLowInventoryAlert(phoneNumber, itemNameText, newQuantity);
                }

                Toast.makeText(EditQuantity.this, "Quantity updated",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(EditQuantity.this, "Could not update quantity",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //send low inventory alert to user
    private void sendLowInventoryAlert(String phoneNumber, String itemName, int currentQuantity) {
        String message = "Low inventory alert: " + itemName + " is currently at " + currentQuantity + " units.";

        SmsManager smsManager = getSystemService(SmsManager.class);
        if (smsManager != null) {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

        }


    }

}