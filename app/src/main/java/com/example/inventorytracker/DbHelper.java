package com.example.inventorytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;


public final class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "InventoryTracker.db";

    // SQL Strings that create tables

    //create login table
    private static final String CREATE_USER_LOGIN_TABLE = "CREATE TABLE " + InventoryContract.UserLogins.TABLE_NAME + " (" +
            InventoryContract.UserLogins.COLUMN_USERNAME + " TEXT PRIMARY KEY NOT NULL, " +
            InventoryContract.UserLogins.COLUMN_PASSWORD + " TEXT NOT NULL, " +
            InventoryContract.UserLogins.COLUMN_FIRST_NAME + " TEXT, " +
            InventoryContract.UserLogins.COLUMN_LAST_NAME + " TEXT)";

    //create inventory table
    private static final String CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryContract.InventoryItems.TABLE_NAME + " (" +
            InventoryContract.InventoryItems.COLUMN_ITEM_ID + " TEXT PRIMARY KEY, " +
            InventoryContract.InventoryItems.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
            InventoryContract.InventoryItems.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0)";

    //create permissions table
    private static final String CREATE_PERMISSIONS_TABLE = "CREATE TABLE " + InventoryContract.PermissionsPreference.TABLE_NAME + " (" +
            InventoryContract.PermissionsPreference.COLUMN_SETTINGS_KEY + " TEXT PRIMARY KEY, " +
            InventoryContract.PermissionsPreference.COLUMN_SETTINGS_VALUE + " TEXT)";



    //constructor
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        //create tables
        db.execSQL(CREATE_USER_LOGIN_TABLE);
        db.execSQL(CREATE_INVENTORY_TABLE);
        db.execSQL(CREATE_PERMISSIONS_TABLE);

        //set default settings for sms permission
        ContentValues values = new ContentValues();
        values.put(InventoryContract.PermissionsPreference.COLUMN_SETTINGS_KEY,
                InventoryContract.PermissionsPreference.KEY_SMS_PERMISSION);
        values.put(InventoryContract.PermissionsPreference.COLUMN_SETTINGS_VALUE, "false");
        db.insert(InventoryContract.PermissionsPreference.TABLE_NAME, null, values);

        //set phone number to empty by default
        values = new ContentValues();
        values.put(InventoryContract.PermissionsPreference.COLUMN_SETTINGS_KEY,
                InventoryContract.PermissionsPreference.KEY_SMS_PHONE_NUMBER);
        values.put(InventoryContract.PermissionsPreference.COLUMN_SETTINGS_VALUE, "");
        db.insert(InventoryContract.PermissionsPreference.TABLE_NAME, null, values);


        //set sms prompted to false by default
        values = new ContentValues();
        values.put(InventoryContract.PermissionsPreference.COLUMN_SETTINGS_KEY,
                InventoryContract.PermissionsPreference.KEY_SMS_PROMPTED);
        values.put(InventoryContract.PermissionsPreference.COLUMN_SETTINGS_VALUE, "false");
        db.insert(InventoryContract.PermissionsPreference.TABLE_NAME, null, values);






    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //delete and recreate tables
        db.execSQL("DROP TABLE IF EXISTS " + InventoryContract.UserLogins.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + InventoryContract.InventoryItems.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + InventoryContract.PermissionsPreference.TABLE_NAME);
        onCreate(db);

    }

    //add new user login when user creates account
    public boolean addUserLogin(String username, String password, String firstName, String lastName) {


        SQLiteDatabase db = this.getWritableDatabase();

        //Check for duplicate username
        String selection = InventoryContract.UserLogins.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(InventoryContract.UserLogins.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);

        boolean duplicate = cursor.getCount() > 0;
        cursor.close();

        if (duplicate) {
            return false;
        }


        ContentValues values = new ContentValues();
        values.put(InventoryContract.UserLogins.COLUMN_USERNAME, username);
        values.put(InventoryContract.UserLogins.COLUMN_PASSWORD, password);
        values.put(InventoryContract.UserLogins.COLUMN_FIRST_NAME, firstName);
        values.put(InventoryContract.UserLogins.COLUMN_LAST_NAME, lastName);
        long result = db.insert(InventoryContract.UserLogins.TABLE_NAME, null, values);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    //check username and password when user logs in
    public boolean checkUserLogin(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = InventoryContract.UserLogins.COLUMN_USERNAME + " = ? AND " +
                InventoryContract.UserLogins.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(InventoryContract.UserLogins.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);

        boolean result = cursor.getCount() > 0;
        cursor.close();

        return result;


    }

    //add an inventory item to inventory_items table
    public boolean addInventoryItem(String itemId, String itemName, int itemQuantity) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryItems.COLUMN_ITEM_ID, itemId);
        values.put(InventoryContract.InventoryItems.COLUMN_ITEM_NAME, itemName);
        values.put(InventoryContract.InventoryItems.COLUMN_ITEM_QUANTITY, itemQuantity);
        long result = db.insert(InventoryContract.InventoryItems.TABLE_NAME, null, values);

        if (result == -1) {
            return false;
        }
        else {
            return true;
        }

    }

    //read inventory items from inventory_items table
    public Cursor readInventoryItems() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(InventoryContract.InventoryItems.TABLE_NAME,
                null, null, null, null, null,
                InventoryContract.InventoryItems.COLUMN_ITEM_NAME + " ASC");

        return cursor;

    }

    //update item quantity
    public boolean updateItemQuantity(String itemId, int newQuantity) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryItems.COLUMN_ITEM_QUANTITY, newQuantity);
        String selection = InventoryContract.InventoryItems.COLUMN_ITEM_ID + " = ?";
        String[] selectionArgs = {itemId};


        int result = db.update(InventoryContract.InventoryItems.TABLE_NAME,
                values, selection, selectionArgs);

        return result > 0;


    }

    //get item by item_id
    public Cursor getItem(String itemId) {

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = InventoryContract.InventoryItems.COLUMN_ITEM_ID + " = ?";
        String[] selectionArgs = {itemId};

        Cursor cursor = db.query(InventoryContract.InventoryItems.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);

        return cursor;




    }

    //delete item by item_id
    public boolean deleteItem(String itemId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = InventoryContract.InventoryItems.COLUMN_ITEM_ID + " = ?";
        String[] selectionArgs = {itemId};


        int result = db.delete(InventoryContract.InventoryItems.TABLE_NAME,
                selection, selectionArgs);

        return result > 0;


    }

    //get setting from permissions table
    public String getSetting(String key) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = InventoryContract.PermissionsPreference.COLUMN_SETTINGS_KEY + " = ?";
        String[] selectionArgs = {key};

        Cursor cursor = db.query(InventoryContract.PermissionsPreference.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);

        String settingResult = null;

        if (cursor.moveToFirst()) {
            settingResult = cursor.getString(
                    cursor.getColumnIndexOrThrow(InventoryContract.PermissionsPreference.COLUMN_SETTINGS_VALUE)
            );
        }

        cursor.close();
        return settingResult;


    }

    //update setting in permissions table
    public boolean updateSetting(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.PermissionsPreference.COLUMN_SETTINGS_VALUE, value);
        String selection = InventoryContract.PermissionsPreference.COLUMN_SETTINGS_KEY + " = ?";
        String[] selectionArgs = {key};

        int result = db.update(InventoryContract.PermissionsPreference.TABLE_NAME,
                values, selection, selectionArgs);

        return result > 0;

    }









}
