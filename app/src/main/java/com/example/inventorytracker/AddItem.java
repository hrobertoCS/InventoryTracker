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





public class AddItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

            DbHelper db = new DbHelper(this);
            EditText itemName = findViewById(R.id.itemName);
            EditText itemQuantity = findViewById(R.id.itemQuantity);
            EditText itemId = findViewById(R.id.itemID);

            Button addItem = findViewById(R.id.additem_button);
            Button cancel = findViewById(R.id.cancel_button);
            Button uploadImage = findViewById(R.id.upload_image_button);

            uploadImage.setOnClickListener(v1 -> {

                Toast.makeText(AddItem.this, "Not implemented",
                        Toast.LENGTH_SHORT).show();

            });

            cancel.setOnClickListener(v1 -> {
                finish();
            });


            //add item to inventory
            addItem.setOnClickListener(v1 -> {
                String name = itemName.getText().toString().trim();
                String quantity = itemQuantity.getText().toString().trim();
                String id = itemId.getText().toString().trim();

                if (name.isEmpty() || quantity.isEmpty() || id.isEmpty()) {
                    Toast.makeText(AddItem.this, "Please enter all fields",
                            Toast.LENGTH_SHORT).show();
                    return;

                }

                int itemQuantityInt;

                try {
                    itemQuantityInt = Integer.parseInt(quantity);
                }
                catch (NumberFormatException e) {
                    Toast.makeText(AddItem.this, "Please enter a valid number",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (itemQuantityInt < 0) {
                    Toast.makeText(AddItem.this, "Please enter a positive number",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean create = db.addInventoryItem(id, name, itemQuantityInt);

                if (create) {
                    Toast.makeText(AddItem.this, "Item Added",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(AddItem.this, "Item already exists",
                            Toast.LENGTH_SHORT).show();
                }




            });


        }

    }