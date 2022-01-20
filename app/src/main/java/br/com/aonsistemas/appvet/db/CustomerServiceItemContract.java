package br.com.aonsistemas.appvet.db;

import android.provider.BaseColumns;

public class CustomerServiceItemContract {

    private CustomerServiceItemContract() {}

    public static class CustomerServiceItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "customer_services_items";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ID_CUSTOMER_SERVICE = "id_customer_service";
        public static final String COLUMN_NAME_ID_PRODUCT = "id_product";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_TOTAL_VALUE = "total_value";
    }

}
