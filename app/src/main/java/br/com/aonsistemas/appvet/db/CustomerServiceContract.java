package br.com.aonsistemas.appvet.db;

import android.provider.BaseColumns;

public class CustomerServiceContract {

    private CustomerServiceContract() {}

    public static class CustomerServiceEntry implements BaseColumns {
        public static final String TABLE_NAME = "customer_services";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ID_CLIENT = "id_client";
        public static final String COLUMN_NAME_ID_ANIMAL = "id_animal";
        public static final String COLUMN_NAME_DATA = "date";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_TOTAL = "total";
    }

}
