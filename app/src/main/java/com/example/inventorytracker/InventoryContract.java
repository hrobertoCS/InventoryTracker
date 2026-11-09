package com.example.inventorytracker;

public final class InventoryContract {

    private InventoryContract() {}

    public static class UserLogins {
        public static final String TABLE_NAME = "user_logins";

        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";

        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";

    }

    public static final class InventoryItems {
        public static final String TABLE_NAME = "inventory_items";

        public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_ITEM_NAME = "item_name";

        public static final String COLUMN_ITEM_QUANTITY = "item_quantity";

    }

    public static final class PermissionsPreference {
        public static final String TABLE_NAME = "permissions_preference";

        public static final String COLUMN_SETTINGS_KEY = "settings_key";
        public static final String COLUMN_SETTINGS_VALUE = "settings_value";

        public static final String KEY_SMS_PERMISSION = "sms_permission";
        public static final String KEY_SMS_PHONE_NUMBER = "phone_number";

        public static final String KEY_SMS_PROMPTED = "sms_prompted";






    }



}
