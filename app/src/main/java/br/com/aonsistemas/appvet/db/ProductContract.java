package br.com.aonsistemas.appvet.db;

import android.provider.BaseColumns;

public class ProductContract {

    private ProductContract() {}

    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_UNITY = "unity";
        public static final String COLUMN_NAME_VALOR = "value";
    }

}
