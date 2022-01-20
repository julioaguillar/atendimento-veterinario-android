package br.com.aonsistemas.appvet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AppVeterinaryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "veterinary.db";

    private static final String FOREIGN_KEYS_ON = "PRAGMA foreign_keys = ON;";

    // TABLE CLIENTS
    private static final String SQL_CREATE_CLIENTS =
            "CREATE TABLE " + ClientContract.ClientEntry.TABLE_NAME + " (" +
                    ClientContract.ClientEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY NOT NULL," +
                    ClientContract.ClientEntry.COLUMN_NAME_NAME + " TEXT," +
                    ClientContract.ClientEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                    ClientContract.ClientEntry.COLUMN_NAME_TELEPHONE + " TEXT," +
                    ClientContract.ClientEntry.COLUMN_NAME_EMAIL + " TEXT)";


    private static final String SQL_DELETE_CLIENTS =
            "DROP TABLE IF EXISTS " + ClientContract.ClientEntry.TABLE_NAME;
    // **********************************************************************

    // TABLE ANIMALS
    private static final String SQL_CREATE_ANIMALS =
            "CREATE TABLE " + AnimalContract.AnimalEntry.TABLE_NAME + " (" +
                    AnimalContract.AnimalEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY NOT NULL," +
                    AnimalContract.AnimalEntry.COLUMN_NAME_CLIENT_ID + " INTEGER NOT NULL, " +
                    AnimalContract.AnimalEntry.COLUMN_NAME_NAME + " TEXT, " +
                    AnimalContract.AnimalEntry.COLUMN_NAME_BIRTH + " DATETIME, " +
                    "FOREIGN KEY("+AnimalContract.AnimalEntry.COLUMN_NAME_CLIENT_ID+") REFERENCES "+ ClientContract.ClientEntry.TABLE_NAME+"("+ ClientContract.ClientEntry.COLUMN_NAME_ID+") ON DELETE CASCADE)";

    private static final String SQL_DELETE_ANIMALS =
            "DROP TABLE IF EXISTS " + AnimalContract.AnimalEntry.TABLE_NAME;
    // **********************************************************************

    // TABLE PRODUCTS
    private static final String SQL_CREATE_PRODUCTS =
            "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " (" +
                    ProductContract.ProductEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY NOT NULL," +
                    ProductContract.ProductEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    ProductContract.ProductEntry.COLUMN_NAME_NOTE + " TEXT," +
                    ProductContract.ProductEntry.COLUMN_NAME_UNITY + " TEXT," +
                    ProductContract.ProductEntry.COLUMN_NAME_VALOR + " DOUBLE)";

    private static final String SQL_DELETE_PRODUCTS =
            "DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;
    // **********************************************************************

    // TABLE UNITY
    private static final String SQL_CREATE_UNITY =
            "CREATE TABLE " + UnitContract.UnitEntry.TABLE_NAME + " (" +
                    UnitContract.UnitEntry.COLUMN_NAME_UNITY + " TEXT," +
                    UnitContract.UnitEntry.COLUMN_NAME_DESCRIPTION + " TEXT)";

    private static final String SQL_DELETE_UNITY =
            "DROP TABLE IF EXISTS " + UnitContract.UnitEntry.TABLE_NAME;
    // **********************************************************************

    // TABLE CUSTOMER SERVICE
    private static final String SQL_CREATE_CUSTOMER_SERVICE =
            "CREATE TABLE " + CustomerServiceContract.CustomerServiceEntry.TABLE_NAME + " (" +
                    CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY NOT NULL," +
                    CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID_CLIENT + " INTEGER NOT NULL, " +
                    CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID_ANIMAL + " INTEGER, " +
                    CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_DATA + " DATETIME," +
                    CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_NOTE + " TEXT," +
                    CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_TOTAL + " DOUBLE," +
                    "FOREIGN KEY("+ CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID_CLIENT +") REFERENCES "+ ClientContract.ClientEntry.TABLE_NAME+"("+ ClientContract.ClientEntry.COLUMN_NAME_ID+")," +
                    "FOREIGN KEY("+ CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID_ANIMAL+") REFERENCES "+AnimalContract.AnimalEntry.TABLE_NAME+"("+AnimalContract.AnimalEntry.COLUMN_NAME_ID+"))";

    private static final String SQL_DELETE_CUSTOMER_SERVICE =
            "DROP TABLE IF EXISTS " + CustomerServiceContract.CustomerServiceEntry.TABLE_NAME;
    // **********************************************************************

    // TABLE CUSTOMER SERVICE ITEMS
    private static final String SQL_CREATE_CUSTOMER_SERVICE_ITEMS =
            "CREATE TABLE " + CustomerServiceItemContract.CustomerServiceItemEntry.TABLE_NAME + " (" +
                    CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY NOT NULL," +
                    CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID_CUSTOMER_SERVICE + " INTEGER NOT NULL, " +
                    CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID_PRODUCT + " INTEGER NOT NULL, " +
                    CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_AMOUNT + " DOUBLE," +
                    CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_VALUE + " DOUBLE," +
                    CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_TOTAL_VALUE + " DOUBLE," +
                    "FOREIGN KEY("+ CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID_CUSTOMER_SERVICE +") REFERENCES "+ CustomerServiceContract.CustomerServiceEntry.TABLE_NAME+"("+ CustomerServiceContract.CustomerServiceEntry.COLUMN_NAME_ID+") ON DELETE CASCADE," +
                    "FOREIGN KEY("+ CustomerServiceItemContract.CustomerServiceItemEntry.COLUMN_NAME_ID_PRODUCT +") REFERENCES "+ ProductContract.ProductEntry.TABLE_NAME+"("+ ProductContract.ProductEntry.COLUMN_NAME_ID+"))";

    private static final String SQL_DELETE_CUSTOMER_SERVICE_ITEMS =
            "DROP TABLE IF EXISTS " + CustomerServiceItemContract.CustomerServiceItemEntry.TABLE_NAME;
    // **********************************************************************

    // TABLE SETTINGS
    private static final String SQL_CREATE_SETTINGS =
            "CREATE TABLE " + SettingsContract.SettingsEntry.TABLE_NAME + " (" +
                    SettingsContract.SettingsEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY NOT NULL," +
                    SettingsContract.SettingsEntry.COLUMN_NAME_NAME + " TEXT," +
                    SettingsContract.SettingsEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                    SettingsContract.SettingsEntry.COLUMN_NAME_CONTACT + " TEXT," +
                    SettingsContract.SettingsEntry.COLUMN_NAME_SERIAL + " TEXT," +
                    SettingsContract.SettingsEntry.COLUMN_NAME_STATUS + " TEXT," +
                    SettingsContract.SettingsEntry.COLUMN_NAME_LOGO + " TEXT)";

    private static final String SQL_DELETE_SETTINGS =
            "DROP TABLE IF EXISTS " + SettingsContract.SettingsEntry.TABLE_NAME;
    // **********************************************************************

    // INSERT DATA
    private static final String[] INSERT_DATA = new String[] {
            "INSERT INTO "+SettingsContract.SettingsEntry.TABLE_NAME+" ("+SettingsContract.SettingsEntry.COLUMN_NAME_ID+") VALUES (1);"
    };
    // **********************************************************************

    public AppVeterinaryDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(FOREIGN_KEYS_ON);
        db.execSQL(SQL_CREATE_CLIENTS);
        db.execSQL(SQL_CREATE_ANIMALS);
        db.execSQL(SQL_CREATE_PRODUCTS);
        db.execSQL(SQL_CREATE_UNITY);
        db.execSQL(SQL_CREATE_CUSTOMER_SERVICE);
        db.execSQL(SQL_CREATE_CUSTOMER_SERVICE_ITEMS);
        db.execSQL(SQL_CREATE_SETTINGS);

        for (String sql : INSERT_DATA) {
            db.execSQL(sql);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CUSTOMER_SERVICE_ITEMS);
        db.execSQL(SQL_DELETE_CUSTOMER_SERVICE);
        db.execSQL(SQL_DELETE_UNITY);
        db.execSQL(SQL_DELETE_PRODUCTS);
        db.execSQL(SQL_DELETE_ANIMALS);
        db.execSQL(SQL_DELETE_CLIENTS);
        db.execSQL(SQL_DELETE_SETTINGS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

}
