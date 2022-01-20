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
import br.com.aonsistemas.appvet.model.Animal;
import br.com.aonsistemas.appvet.model.Client;

public class AnimalDAO {

    private SQLiteDatabase db;
    private final AppVeterinaryDbHelper dbHelper;

    public AnimalDAO(Context context) {
        dbHelper = new AppVeterinaryDbHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(Animal animal) {

        ContentValues values = new ContentValues();

        values.put(AnimalContract.AnimalEntry.COLUMN_NAME_CLIENT_ID, animal.getClient_id());
        values.put(AnimalContract.AnimalEntry.COLUMN_NAME_NAME, animal.getName());
        values.put(AnimalContract.AnimalEntry.COLUMN_NAME_BIRTH, animal.getBirth());

        db.insertOrThrow(AnimalContract.AnimalEntry.TABLE_NAME, null, values);

    }

    public void update(Animal animal) {

        ContentValues values = new ContentValues();

        values.put(AnimalContract.AnimalEntry.COLUMN_NAME_CLIENT_ID, animal.getClient_id());
        values.put(AnimalContract.AnimalEntry.COLUMN_NAME_NAME, animal.getName());
        values.put(AnimalContract.AnimalEntry.COLUMN_NAME_BIRTH, animal.getBirth());

        db.update(AnimalContract.AnimalEntry.TABLE_NAME, values, AnimalContract.AnimalEntry.COLUMN_NAME_ID + " = " + animal.getId(), null);

    }

    public void delete(Animal animal) {
        db.delete(AnimalContract.AnimalEntry.TABLE_NAME, AnimalContract.AnimalEntry.COLUMN_NAME_ID+" = "+animal.getId(), null);
    }

    public List<Animal> getAnimals() {

        List<Animal> animals = new ArrayList<>();
        Animal model;

        String sql = "SELECT animals.id, animals.client_id, animals.name, animals.birth, \n" +
                "       clients.id, clients.name, clients.address, clients.telephone, clients.email \n" +
                "FROM animals \n" +
                "LEFT JOIN clients on(clients.id = animals.client_id)";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                model = new Animal();
                model.setId(cursor.getInt(0));
                model.setClient_id(cursor.getInt(1));
                model.setName(cursor.getString(2));
                model.setBirth(cursor.getString(3));

                Client client = new Client();
                client.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ClientContract.ClientEntry.COLUMN_NAME_ID)));
                client.setName(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.ClientEntry.COLUMN_NAME_NAME)));
                client.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.ClientEntry.COLUMN_NAME_ADDRESS)));
                client.setTelephone(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.ClientEntry.COLUMN_NAME_TELEPHONE)));
                client.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.ClientEntry.COLUMN_NAME_EMAIL)));

                model.setClient(client);

                animals.add(model);

                cursor.moveToNext();

            }
        }

        cursor.close();
        return animals;

    }

    public List<Animal> getAnimals(int client_id) {

        List<Animal> animals = new ArrayList<>();
        Animal model;

        String sql = "SELECT animals.id, animals.client_id, animals.name, animals.birth, \n" +
                "       clients.id, clients.name, clients.address, clients.telephone, clients.email \n" +
                "FROM animals \n" +
                "LEFT JOIN clients on(clients.id = animals.client_id) \n" +
                "WHERE client_id = " + client_id;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                model = new Animal();
                model.setId(cursor.getInt(0));
                model.setClient_id(cursor.getInt(1));
                model.setName(cursor.getString(2));
                model.setBirth(cursor.getString(3));

                Client client = new Client();
                client.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ClientContract.ClientEntry.COLUMN_NAME_ID)));
                client.setName(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.ClientEntry.COLUMN_NAME_NAME)));
                client.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.ClientEntry.COLUMN_NAME_ADDRESS)));
                client.setTelephone(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.ClientEntry.COLUMN_NAME_TELEPHONE)));
                client.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.ClientEntry.COLUMN_NAME_EMAIL)));

                model.setClient(client);

                animals.add(model);

                cursor.moveToNext();

            }
        }

        cursor.close();
        return animals;

    }

    public boolean isNotDeletable(Animal animal) {

        boolean result;

        Cursor cursor;
        cursor = db.rawQuery("SELECT count(customer_services.id) FROM customer_services WHERE customer_services.id_animal = "+animal.getId(), null);
        result = ( (cursor.moveToFirst()) && (cursor.getInt(0) > 0) );
        cursor.close();

        return result;

    }

}
