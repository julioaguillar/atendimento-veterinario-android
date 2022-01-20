package br.com.aonsistemas.appvet.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.aonsistemas.appvet.db.AnimalContract;
import br.com.aonsistemas.appvet.db.AppVeterinaryDbHelper;
import br.com.aonsistemas.appvet.db.ClientContract;
import br.com.aonsistemas.appvet.model.Client;

public class ClientDAO {

    private SQLiteDatabase db;
    private final AppVeterinaryDbHelper dbHelper;

    private final String[] columns = {
            ClientContract.ClientEntry.COLUMN_NAME_ID,
            ClientContract.ClientEntry.COLUMN_NAME_NAME,
            ClientContract.ClientEntry.COLUMN_NAME_ADDRESS,
            ClientContract.ClientEntry.COLUMN_NAME_TELEPHONE,
            ClientContract.ClientEntry.COLUMN_NAME_EMAIL
    };

    public ClientDAO(Context context) {
        dbHelper = new AppVeterinaryDbHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(Client client) {

        ContentValues values = new ContentValues();

        values.put(ClientContract.ClientEntry.COLUMN_NAME_NAME, client.getName());
        values.put(ClientContract.ClientEntry.COLUMN_NAME_ADDRESS, client.getAddress());
        values.put(ClientContract.ClientEntry.COLUMN_NAME_TELEPHONE, client.getTelephone());
        values.put(ClientContract.ClientEntry.COLUMN_NAME_EMAIL, client.getEmail());

        db.insertOrThrow(ClientContract.ClientEntry.TABLE_NAME, null, values);

    }

    public void update(Client client) {

        ContentValues values = new ContentValues();

        values.put(ClientContract.ClientEntry.COLUMN_NAME_NAME, client.getName());
        values.put(ClientContract.ClientEntry.COLUMN_NAME_ADDRESS, client.getAddress());
        values.put(ClientContract.ClientEntry.COLUMN_NAME_TELEPHONE, client.getTelephone());
        values.put(ClientContract.ClientEntry.COLUMN_NAME_EMAIL, client.getEmail());

        db.update(ClientContract.ClientEntry.TABLE_NAME, values, ClientContract.ClientEntry.COLUMN_NAME_ID + " = " + client.getId(), null);

    }

    public void delete(Client client) {
        db.delete(ClientContract.ClientEntry.TABLE_NAME, ClientContract.ClientEntry.COLUMN_NAME_ID+" = "+ client.getId(), null);
        db.delete(AnimalContract.AnimalEntry.TABLE_NAME, AnimalContract.AnimalEntry.COLUMN_NAME_CLIENT_ID+" = "+ client.getId(), null);
    }

    public List<Client> getClients() {

        List<Client> clients = new ArrayList<>();
        Client model;

        Cursor cursor = db.query(
                ClientContract.ClientEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                ClientContract.ClientEntry.COLUMN_NAME_NAME
        );

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                model = new Client();

                model.setId(cursor.getInt(0));
                model.setName(cursor.getString(1));
                model.setAddress(cursor.getString(2));
                model.setTelephone(cursor.getString(3));
                model.setEmail(cursor.getString(4));

                clients.add(model);

                cursor.moveToNext();

            }
        }

        cursor.close();
        return clients;

    }

    public Client getClient(int id) {

        Client model = new Client();
        Cursor cursor = db.query(
                true,
                ClientContract.ClientEntry.TABLE_NAME, columns,
                ClientContract.ClientEntry.COLUMN_NAME_ID+" = "+id,
                null,
                null,
                null,
                null,
                null
        );

        if ( cursor.getCount() > 0 ) {

            cursor.moveToFirst();

            model.setId(cursor.getInt(0));
            model.setName(cursor.getString(1));
            model.setAddress(cursor.getString(2));
            model.setTelephone(cursor.getString(3));
            model.setEmail(cursor.getString(4));

        }

        cursor.close();
        return model;

    }

    public boolean isNotDeletable(Client client) {

        boolean result;

        Cursor cursor;
        cursor = db.rawQuery("SELECT count(customer_services.id) FROM customer_services WHERE customer_services.id_client = "+ client.getId(), null);
        result = ( (cursor.moveToFirst()) && (cursor.getInt(0) > 0) );
        cursor.close();

        return result;

    }

}
