package br.com.aonsistemas.appvet.db;

import android.provider.BaseColumns;

public class SettingsContract {

    private SettingsContract() {}

    public static class SettingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "Settings";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_CONTACT = "contact";
        public static final String COLUMN_NAME_SERIAL = "serial";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_LOGO = "logo";
    }

}
