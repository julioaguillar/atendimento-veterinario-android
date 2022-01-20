package br.com.aonsistemas.appvet.db;

import android.provider.BaseColumns;

public final class ClientContract {

    private ClientContract() {}

    public static class ClientEntry implements BaseColumns {
        public static final String TABLE_NAME = "clients";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_TELEPHONE = "telephone";
        public static final String COLUMN_NAME_EMAIL = "email";
    }

}
