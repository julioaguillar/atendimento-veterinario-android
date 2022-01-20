package br.com.aonsistemas.appvet.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.aonsistemas.appvet.db.AppVeterinaryDbHelper;
import br.com.aonsistemas.appvet.db.SettingsContract;
import br.com.aonsistemas.appvet.model.Settings;

public class SettingsDAO {

    private SQLiteDatabase db;
    private final AppVeterinaryDbHelper dbHelper;

    private final String[] columns = {
            SettingsContract.SettingsEntry.COLUMN_NAME_ID,
            SettingsContract.SettingsEntry.COLUMN_NAME_NAME,
            SettingsContract.SettingsEntry.COLUMN_NAME_ADDRESS,
            SettingsContract.SettingsEntry.COLUMN_NAME_CONTACT,
            SettingsContract.SettingsEntry.COLUMN_NAME_SERIAL,
            SettingsContract.SettingsEntry.COLUMN_NAME_STATUS,
            SettingsContract.SettingsEntry.COLUMN_NAME_LOGO
    };

    public SettingsDAO(Context context) {
        dbHelper = new AppVeterinaryDbHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void update(Settings settings) {

        ContentValues values = new ContentValues();
        values.put(SettingsContract.SettingsEntry.COLUMN_NAME_NAME, settings.getNome());
        values.put(SettingsContract.SettingsEntry.COLUMN_NAME_ADDRESS, settings.getAddress());
        values.put(SettingsContract.SettingsEntry.COLUMN_NAME_CONTACT, settings.getContact());
        values.put(SettingsContract.SettingsEntry.COLUMN_NAME_SERIAL, settings.getSerial());
        values.put(SettingsContract.SettingsEntry.COLUMN_NAME_LOGO, settings.getLogo());

        db.update(SettingsContract.SettingsEntry.TABLE_NAME, values, null, null);

    }

    public Settings getConfiguration() {

        Settings model = new Settings();

        Cursor cursor = db.query(
                true,
                SettingsContract.SettingsEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                "1"
        );

        if ( cursor.getCount() > 0 ) {
            cursor.moveToFirst();
            model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SettingsContract.SettingsEntry.COLUMN_NAME_ID)));
            model.setNome(cursor.getString(cursor.getColumnIndexOrThrow(SettingsContract.SettingsEntry.COLUMN_NAME_NAME)));
            model.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(SettingsContract.SettingsEntry.COLUMN_NAME_ADDRESS)));
            model.setContact(cursor.getString(cursor.getColumnIndexOrThrow(SettingsContract.SettingsEntry.COLUMN_NAME_CONTACT)));
            model.setSerial(cursor.getString(cursor.getColumnIndexOrThrow(SettingsContract.SettingsEntry.COLUMN_NAME_SERIAL)));
            model.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(SettingsContract.SettingsEntry.COLUMN_NAME_STATUS)));
            model.setLogo(cursor.getString(cursor.getColumnIndexOrThrow(SettingsContract.SettingsEntry.COLUMN_NAME_LOGO)));
        }

        cursor.close();
        return model;

    }

}
