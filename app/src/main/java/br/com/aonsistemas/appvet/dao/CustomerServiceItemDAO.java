package br.com.aonsistemas.appvet.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.aonsistemas.appvet.db.AppVeterinaryDbHelper;
import br.com.aonsistemas.appvet.model.CustomerServiceItem;
import br.com.aonsistemas.appvet.model.Product;

public class CustomerServiceItemDAO {

    private SQLiteDatabase db;
    private final AppVeterinaryDbHelper dbHelper;

    public CustomerServiceItemDAO(Context context) {
        dbHelper = new AppVeterinaryDbHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<CustomerServiceItem> getCustomerServiceItems(int id_customer_service) {

        List<CustomerServiceItem> items = new ArrayList<>();
        CustomerServiceItem model;

        String sql = "SELECT customer_services_items.id, customer_services_items.id_customer_service, customer_services_items.id_product, \n" +
                "       customer_services_items.amount, customer_services_items.value, customer_services_items.total_value, \n" +
                "       products.id, products.description, products.unity, products.value, products.note \n" +
                "FROM customer_services_items \n" +
                "LEFT JOIN products on(products.id = customer_services_items.id_product) \n" +
                "WHERE customer_services_items.id_customer_service = "+id_customer_service;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                model = new CustomerServiceItem();
                model.setId(cursor.getInt(0));
                model.setIdCustomerService(cursor.getInt(1));
                model.setIdProduct(cursor.getInt(2));
                model.setAmount(cursor.getDouble(3));
                model.setValue(cursor.getDouble(4));
                model.setTotalValue(cursor.getDouble(5));

                Product product = new Product();
                product.setId(cursor.getInt(6));
                product.setDescription(cursor.getString(7));
                product.setUnity(cursor.getString(8));
                product.setValor(cursor.getDouble(9));
                product.setNote(cursor.getString(10));

                model.setProduct(product);

                items.add(model);
                cursor.moveToNext();

            }
        }

        cursor.close();
        return items;

    }

}
