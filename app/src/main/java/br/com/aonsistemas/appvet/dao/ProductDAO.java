package br.com.aonsistemas.appvet.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.aonsistemas.appvet.db.AppVeterinaryDbHelper;
import br.com.aonsistemas.appvet.db.ProductContract;
import br.com.aonsistemas.appvet.model.Product;

public class ProductDAO {

    private SQLiteDatabase db;
    private final AppVeterinaryDbHelper dbHelper;

    String[] columns = {
            ProductContract.ProductEntry.COLUMN_NAME_ID,
            ProductContract.ProductEntry.COLUMN_NAME_DESCRIPTION,
            ProductContract.ProductEntry.COLUMN_NAME_NOTE,
            ProductContract.ProductEntry.COLUMN_NAME_UNITY,
            ProductContract.ProductEntry.COLUMN_NAME_VALOR
    };

    public ProductDAO(Context context) {
        dbHelper = new AppVeterinaryDbHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(Product product) {

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_NAME_DESCRIPTION, product.getDescription());
        values.put(ProductContract.ProductEntry.COLUMN_NAME_NOTE, product.getNote());
        values.put(ProductContract.ProductEntry.COLUMN_NAME_UNITY, product.getUnity());
        values.put(ProductContract.ProductEntry.COLUMN_NAME_VALOR, product.getValor());

        db.insertOrThrow(ProductContract.ProductEntry.TABLE_NAME, null, values);

    }

    public void update(Product product) {

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_NAME_DESCRIPTION, product.getDescription());
        values.put(ProductContract.ProductEntry.COLUMN_NAME_NOTE, product.getNote());
        values.put(ProductContract.ProductEntry.COLUMN_NAME_UNITY, product.getUnity());
        values.put(ProductContract.ProductEntry.COLUMN_NAME_VALOR, product.getValor());

        db.update(ProductContract.ProductEntry.TABLE_NAME, values, ProductContract.ProductEntry.COLUMN_NAME_ID + " = " + product.getId(), null);

    }

    public void delete(Product product) {
        db.delete(ProductContract.ProductEntry.TABLE_NAME, ProductContract.ProductEntry.COLUMN_NAME_ID+" = "+ product.getId(), null);
    }

    public List<Product> getProducts() {

        List<Product> products = new ArrayList<>();
        Product model;

        Cursor cursor = db.query(
                ProductContract.ProductEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                ProductContract.ProductEntry.COLUMN_NAME_DESCRIPTION
        );

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                model = new Product();

                model.setId(cursor.getInt(0));
                model.setDescription(cursor.getString(1));
                model.setNote(cursor.getString(2));
                model.setUnity(cursor.getString(3));
                model.setValor(cursor.getDouble(4));

                products.add(model);

                cursor.moveToNext();

            }
        }

        cursor.close();
        return products;

    }

    public boolean isNotDeletable(Product product) {

        boolean result;

        Cursor cursor;
        cursor = db.rawQuery("SELECT count(customer_services_items.id) FROM customer_services_items WHERE customer_services_items.id_product = "+ product.getId(), null);
        result = ( (cursor.moveToFirst()) && (cursor.getInt(0) > 0) );
        cursor.close();

        return result;

    }
    
}
