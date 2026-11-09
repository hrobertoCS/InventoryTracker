package com.example.inventorytracker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;


public class EditInventory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //initialize UI elements
        Button addItem = findViewById(R.id.addItem);
        Button deleteItem = findViewById(R.id.deleteItem);
        TextView back = findViewById(R.id.back);


        //take user to add item activity
        addItem.setOnClickListener(v -> {
            Intent intent = new Intent(EditInventory.this, AddItem.class);
            startActivity(intent);
        });

        //take user to delete item activity
        deleteItem.setOnClickListener(v -> {
            Intent intent = new Intent(EditInventory.this, DeleteItem.class);
            startActivity(intent);
        });

        //take user back to
        back.setOnClickListener(v -> finish());


    }
}