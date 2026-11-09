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






public class DeleteItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //initialize db helper
        DbHelper db = new DbHelper(this);

        //reference UI
        EditText itemId = findViewById(R.id.itemName3);
        EditText itemName = findViewById(R.id.itemName);
        Button deleteItem = findViewById(R.id.button8);
        Button cancel = findViewById(R.id.button9);


        //delete item from inventory
        deleteItem.setOnClickListener(v -> {
            String name = itemName.getText().toString();
            String id = itemId.getText().toString();

            if(id.isEmpty()) {
                Toast.makeText(DeleteItem.this, "Please enter all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean delete = db.deleteItem(id);


            if (delete) {
                Toast.makeText(DeleteItem.this, "Item deleted",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(DeleteItem.this, "Item not found",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //cancel delete item
        cancel.setOnClickListener(v -> {
            finish();
        });

        }


}