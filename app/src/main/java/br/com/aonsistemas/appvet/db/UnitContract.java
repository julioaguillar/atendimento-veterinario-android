package br.com.aonsistemas.appvet.db;

import android.provider.BaseColumns;

public class UnitContract {

    private UnitContract() {}

    public static class UnitEntry implements BaseColumns {
        public static final String TABLE_NAME = "units";
        public static final String COLUMN_NAME_UNITY = "unity";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

}
