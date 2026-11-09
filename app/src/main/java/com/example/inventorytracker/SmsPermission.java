package com.example.inventorytracker;

import android.os.Bundle;

import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Button;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SmsPermission extends AppCompatActivity {

    private DbHelper db;

    private static final int SMS_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbHelper(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sms_permission);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //reference UI
        Switch switchSms = findViewById(R.id.switchSms);
        TextView smsStatus = findViewById(R.id.smsPermissionStatus);
        Button homeButton = findViewById(R.id.home_button);
        EditText phoneNumberInput = findViewById(R.id.phoneNumberInput);

        String savedPhoneNumber = db.getSetting(InventoryContract.PermissionsPreference.KEY_SMS_PHONE_NUMBER);

        if (savedPhoneNumber != null) {
            phoneNumberInput.setText(savedPhoneNumber);
        }





        //show sms permission status
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED) {
            switchSms.setChecked(true);
            smsStatus.setText("Permission Granted");
            db.updateSetting(InventoryContract.PermissionsPreference.KEY_SMS_PERMISSION, "true");
        }
        else {
            switchSms.setChecked(false);
            smsStatus.setText("Permission Not Granted");
            db.updateSetting(InventoryContract.PermissionsPreference.KEY_SMS_PERMISSION, "false");
        }

        homeButton.setOnClickListener(v -> finish());


        //handle sms permission
        switchSms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                String phoneNumber = phoneNumberInput.getText().toString().trim();

                if (phoneNumber.isEmpty()) {
                    smsStatus.setText("Enter phone number");
                    switchSms.setChecked(false);
                    Toast.makeText(SmsPermission.this, "Please enter a phone number",
                            Toast.LENGTH_SHORT).show();
                    return;

                }
                db.updateSetting(InventoryContract.PermissionsPreference.KEY_SMS_PHONE_NUMBER, phoneNumber);


                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            SMS_PERMISSION_REQUEST_CODE);
                }
                else {
                    smsStatus.setText("Permission Granted");
                    db.updateSetting(InventoryContract.PermissionsPreference.KEY_SMS_PERMISSION, "true");
                }
            }
            else {
                smsStatus.setText("Permission Not Granted");
                Toast.makeText(SmsPermission.this, "SMS Permission disabled",
                        Toast.LENGTH_SHORT).show();
                db.updateSetting(InventoryContract.PermissionsPreference.KEY_SMS_PERMISSION, "false");
            }
        });

    }

    //handle sms permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Switch switchSms = findViewById(R.id.switchSms);
        TextView smsPermissionStatus = findViewById(R.id.smsPermissionStatus);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switchSms.setChecked(true);
                smsPermissionStatus.setText("Permission Granted");
                db.updateSetting(InventoryContract.PermissionsPreference.KEY_SMS_PERMISSION, "true");
            }
            else {
                switchSms.setChecked(false);
                smsPermissionStatus.setText("Permission Not Granted");
                Toast.makeText(SmsPermission.this, "SMS Permission Denied",
                        Toast.LENGTH_SHORT).show();
                db.updateSetting(InventoryContract.PermissionsPreference.KEY_SMS_PERMISSION, "false");
            }
        }
    }
}

