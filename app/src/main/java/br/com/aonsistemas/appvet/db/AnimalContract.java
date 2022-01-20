package br.com.aonsistemas.appvet.db;

import android.provider.BaseColumns;

public class AnimalContract {

    private AnimalContract() {}

    public static class AnimalEntry implements BaseColumns {
        public static final String TABLE_NAME = "animals";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CLIENT_ID = "client_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_BIRTH = "birth";
    }

}
