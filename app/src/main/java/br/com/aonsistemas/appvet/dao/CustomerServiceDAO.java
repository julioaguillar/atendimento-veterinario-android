package br.com.aonsistemas.appvet.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.aonsistemas.appvet.db.AppVeterinaryDbHelper;
import br.com.aonsistemas.appvet.db.CustomerServiceContract;
import br.com.aonsistemas.appvet.db.CustomerServiceItemContract;
import br.com.aonsistemas.appvet.model.Animal;
import br.com.aonsistemas.appvet.model.Client;
import br.com.aonsistemas.appvet.model.CustomerService;
import br.com.aonsistemas.appvet.model.CustomerServiceItem;
import br.com.aonsistemas.appvet.model.Product;

public class CustomerServiceDAO {

    private SQLiteDatabase db;
    private final AppVeterinaryDbHelper dbHelper;

    public CustomerServiceDAO(Context context) {
        dbHelper = new AppVeterinaryDbHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(CustomerService customerService) {

        int id_customer_service = getNextId();

        ContentValues values = new ContentValues();

        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID, id_customer_service);
        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID_CLIENT, customerService.getId_client());
        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID_ANIMAL, customerService.getId_animal());
        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_DATA, customerService.getData());
        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_NOTE, customerService.getNote());
        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_TOTAL, customerService.getTotal());

        db.insertOrThrow(CustomerServiceContract.CustomerServiceEntry.TABLE_NAME, null, values);

        for (CustomerServiceItem item : customerService.getItems()) {

            values.clear();
            values.put(CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID_CUSTOMER_SERVICE, id_customer_service);
            values.put(CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID_PRODUCT, item.getIdProduct());
            values.put(CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_AMOUNT, item.getAmount());
            values.put(CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_VALUE, item.getValue());
            values.put(CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_TOTAL_VALUE, item.getTotalValue());

            db.insertOrThrow(CustomerServiceItemContract.CustomerServiceItemEntry.TABLE_NAME, null, values);

        }

    }

    public void update(CustomerService customerService) {

        ContentValues values = new ContentValues();

        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID_CLIENT, customerService.getId_client());
        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID_ANIMAL, customerService.getId_animal());
        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_DATA, customerService.getData());
        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_NOTE, customerService.getNote());
        values.put(CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_TOTAL, customerService.getTotal());

        db.update(CustomerServiceContract.CustomerServiceEntry.TABLE_NAME, values, CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID + " = " + customerService.getId(), null);

        db.delete(CustomerServiceItemContract.CustomerServiceItemEntry.TABLE_NAME, CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID_CUSTOMER_SERVICE + " = " + customerService.getId(), null);

        for (CustomerServiceItem item : customerService.getItems()) {

            values.clear();
            values.put(CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID_CUSTOMER_SERVICE, customerService.getId());
            values.put(CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID_PRODUCT, item.getIdProduct());
            values.put(CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_AMOUNT, item.getAmount());
            values.put(CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_VALUE, item.getValue());
            values.put(CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_TOTAL_VALUE, item.getTotalValue());

            db.insertOrThrow(CustomerServiceItemContract.CustomerServiceItemEntry.TABLE_NAME, null, values);

        }

    }

    public void delete(CustomerService customerService) {
        db.delete(CustomerServiceContract.CustomerServiceEntry.TABLE_NAME, CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID+" = "+ customerService.getId(), null);
        db.delete(CustomerServiceItemContract.CustomerServiceItemEntry.TABLE_NAME, CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID_CUSTOMER_SERVICE +" = "+ customerService.getId(), null);
    }

    public List<CustomerService> getCustomerServices() {

        List<CustomerService> customerServices = new ArrayList<>();
        CustomerService model;

        String sql = "SELECT customer_services.id, customer_services.id_client, customer_services.id_animal, customer_services.date, customer_services.note, customer_services.total, \n" +
                "       clients.id, clients.name, clients.address, clients.telephone, clients.email, \n" +
                "       animals.id, animals.client_id, animals.name, animals.birth \n" +
                "FROM customer_services \n" +
                "LEFT JOIN clients ON(clients.id = customer_services.id_client) \n" +
                "LEFT JOIN animals ON(animals.id = customer_services.id_animal) \n" +
                "ORDER BY customer_services.date DESC, clients.name";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                model = new CustomerService();

                model.setId(cursor.getInt(0));
                model.setId_client(cursor.getInt(1));
                model.setId_animal(cursor.getInt(2));
                model.setData(cursor.getString(3));
                model.setNote(cursor.getString(4));
                model.setTotal(cursor.getDouble(5));

                Client client = new Client(
                        cursor.getInt(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10)
                );

                Animal animal = new Animal(
                        cursor.getInt(11),
                        cursor.getInt(12),
                        cursor.getString(13),
                        cursor.getString(14),
                        client
                );

                model.setClient(client);
                model.setAnimal(animal);

                customerServices.add(model);

                cursor.moveToNext();

            }
        }

        cursor.close();
        return customerServices;

    }

    public List<CustomerService> getCustomerServices(String startDate, String endDate, int client_id, int animal_id) {

        List<CustomerService> customerServices = new ArrayList<>();
        CustomerService model;

        String sql = "SELECT customer_services.id, customer_services.id_client, customer_services.id_animal, customer_services.date, customer_services.note, customer_services.total, \n" +
                "       clients.id, clients.name, clients.address, clients.telephone, clients.email, \n" +
                "       animals.id, animals.client_id, animals.name, animals.birth \n" +
                "FROM customer_services \n" +
                "LEFT JOIN clients ON(clients.id = customer_services.id_client) \n" +
                "LEFT JOIN animals ON(animals.id = customer_services.id_animal) \n" +
                "WHERE ( '"+(client_id > 0 ? "NAO" : "SIM")+"' = 'SIM' OR customer_services.id_client = "+client_id+" ) AND \n" +
                "      ( '"+(animal_id > 0 ? "NAO" : "SIM")+"' = 'SIM' OR customer_services.id_animal = "+animal_id+" ) AND \n" +
                "      ( customer_services.date BETWEEN '"+startDate+"' AND '"+endDate+"' ) \n" +
                "ORDER BY clients.id, customer_services.date, animals.id";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                model = new CustomerService();

                model.setId(cursor.getInt(0));
                model.setId_client(cursor.getInt(1));
                model.setId_animal(cursor.getInt(2));
                model.setData(cursor.getString(3));
                model.setNote(cursor.getString(4));
                model.setTotal(cursor.getDouble(5));

                Client client = new Client(
                        cursor.getInt(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10)
                );

                model.setClient(client);

                if ( cursor.getInt(11) > 0 ) {
                    Animal animal = new Animal(
                            cursor.getInt(11),
                            cursor.getInt(12),
                            cursor.getString(13),
                            cursor.getString(14),
                            client
                    );
                    model.setAnimal(animal);
                }

                String sqlItem = "SELECT customer_services_items.id, customer_services_items.id_customer_service, customer_services_items.id_product, customer_services_items.amount, customer_services_items.value, customer_services_items.total_value, \n" +
                        "       products.id, products.description, products.unity, products.value, products.note \n" +
                        "FROM customer_services_items \n" +
                        "LEFT JOIN products ON(products.id = customer_services_items.id_product) \n" +
                        "WHERE customer_services_items.id_customer_service = "+model.getId();

                Cursor cursorItem = db.rawQuery(sqlItem, null);

                if (cursorItem.moveToFirst()) {

                    List<CustomerServiceItem> items = new ArrayList<>();

                    while (!cursorItem.isAfterLast()) {

                        CustomerServiceItem item = new CustomerServiceItem();
                        item.setId(cursorItem.getInt(0));
                        item.setIdCustomerService(cursorItem.getInt(1));
                        item.setIdProduct(cursorItem.getInt(2));
                        item.setAmount(cursorItem.getDouble(3));
                        item.setValue(cursorItem.getDouble(4));
                        item.setTotalValue(cursorItem.getDouble(5));

                        Product product = new Product();
                        product.setId(cursorItem.getInt(6));
                        product.setDescription(cursorItem.getString(7));
                        product.setUnity(cursorItem.getString(8));
                        product.setValor(cursorItem.getDouble(9));
                        product.setNote(cursorItem.getString(10));

                        item.setProduct(product);
                        items.add(item);

                        cursorItem.moveToNext();

                    }

                    model.setItems(items);

                }

                cursorItem.close();

                // *****************************

                customerServices.add(model);
                cursor.moveToNext();

            }
        }

        cursor.close();
        return customerServices;

    }

    public int getNextId() {

        int result;

        Cursor cursor;
        cursor = db.rawQuery("SELECT MAX("+ CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID+") FROM "+ CustomerServiceContract.CustomerServiceEntry.TABLE_NAME, null);

        if (cursor.moveToFirst())
            result = cursor.getInt(0) + 1;
        else
            result = 1;

        cursor.close();

        return result;

    }

}
