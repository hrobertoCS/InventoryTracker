package com.example.inventorytracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.widget.Toast;

public class InventoryGrid extends AppCompatActivity {

    private DbHelper db;
    private InventoryAdapter adapter;
    private List<InventoryItemData> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory_grid);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //instantiate db helper
        db = new DbHelper(this);

        //reference UI
        Button editInventory = findViewById(R.id.editInventory);
        RecyclerView recycler = findViewById(R.id.recyclerView);
        Button smsSettings = findViewById(R.id.smsSettings);

        //open sms settings page when user clicks sms settings button
        smsSettings.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(InventoryGrid.this, SmsPermission.class);
            startActivity(intent);
        });




        //sets recycler as a grid with 3 columns
        recycler.setLayoutManager(new GridLayoutManager(this,3));





        //create list
        items = new ArrayList<>();
        adapter = new InventoryAdapter(items);
        recycler.setAdapter(adapter);

        //load inventory data
        loadInventoryItems();

        //open edit inventory page when user clicks edit inventory button
        editInventory.setOnClickListener(v -> {
           android.content.Intent intent = new android.content.Intent(InventoryGrid.this, EditInventory.class);
           startActivity(intent);
        });

        //prompt sms if user has not already been prompted
        String smsPrompted = db.getSetting(InventoryContract.PermissionsPreference.KEY_SMS_PROMPTED);

        if (!"true".equals(smsPrompted) ){
            promptSms();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        loadInventoryItems();
    }

    //load inventory data from db into recycler view
    private void loadInventoryItems() {

        items.clear();

        Cursor cursor = db.readInventoryItems();

        while (cursor.moveToNext()) {
            String itemName = cursor.getString(
                    cursor.getColumnIndexOrThrow(InventoryContract.InventoryItems.COLUMN_ITEM_NAME)
            );

            int itemQuantity = cursor.getInt(
                    cursor.getColumnIndexOrThrow(InventoryContract.InventoryItems.COLUMN_ITEM_QUANTITY)
            );

            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow(InventoryContract.InventoryItems.COLUMN_ITEM_ID)
            );

            items.add(new InventoryItemData(itemName, itemQuantity, itemId));
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    //one time prompt for user to enable sms
    private void promptSms() {
        new androidx.appcompat.app.AlertDialog.Builder(InventoryGrid.this)
                .setTitle("SMS Permission").setMessage("Please enable SMS to receive low inventory alerts")
                .setPositiveButton("Enable", (dialog, which) -> {
                    db.updateSetting(
                            InventoryContract.PermissionsPreference.KEY_SMS_PROMPTED, "true");
                    android.content.Intent intent = new android.content.Intent(InventoryGrid.this, SmsPermission.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    db.updateSetting(
                            InventoryContract.PermissionsPreference.KEY_SMS_PROMPTED, "true");

                    Toast.makeText(InventoryGrid.this, "You can enable sms permission later in SMS settings",
                            Toast.LENGTH_SHORT).show();
                }).show();




    }


    //holds inventory row
    static class InventoryItemData {
        String itemName;
        int itemQuantity;
        String itemId;

        InventoryItemData(String itemName, int itemQuantity, String itemId) {
            this.itemName = itemName;
            this.itemQuantity = itemQuantity;
            this.itemId = itemId;
        }
        }


    class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {
        private final List<InventoryItemData> items;

        InventoryAdapter(List<InventoryItemData> items) {
            this.items = items;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView itemName;
            TextView itemQuantity;
            Button deleteRow;

            ViewHolder(View view) {
                super(view);
                itemName = view.findViewById(R.id.itemName);
                deleteRow = view.findViewById(R.id.deleteRow);
                itemQuantity = view.findViewById(R.id.itemQuantity);
            }


        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            InventoryItemData currentItem = items.get(position);


            holder.itemName.setText(currentItem.itemName);
            holder.itemQuantity.setText("Qty: " + currentItem.itemQuantity);
            holder.deleteRow.setTag(currentItem.itemId);

            holder.itemName.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(InventoryGrid.this, EditQuantity.class);
                intent.putExtra("itemId", currentItem.itemId);
                intent.putExtra("itemName", currentItem.itemName);
                intent.putExtra("itemQuantity", currentItem.itemQuantity);
                startActivity(intent);

            });

            holder.itemQuantity.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(InventoryGrid.this, EditQuantity.class);
                intent.putExtra("itemId", currentItem.itemId);
                intent.putExtra("itemName", currentItem.itemName);
                intent.putExtra("itemQuantity", currentItem.itemQuantity);
                startActivity(intent);

            });




            holder.deleteRow.setOnClickListener( v -> {
                boolean delete = db.deleteItem(currentItem.itemId);
                if (delete) {
                    Toast.makeText(InventoryGrid.this, "Item Deleted",
                            Toast.LENGTH_SHORT).show();
                    loadInventoryItems();
                }
                else {
                    Toast.makeText(InventoryGrid.this, "Could Not Delete Item",
                            Toast.LENGTH_SHORT).show();
                }


            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }




        }



}