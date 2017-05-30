package com.example.lucija.p2pchatapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "contacts.db";
    public static final String TABLE_CONTACT = "contacts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTACTNAME = "contactName";
    public static final String COLUMN_CONTACTNUMBER = "contactNumber";
    public static final String COLUMN_MYKEY = "myKey";
    public static final String COLUMN_CONTACTKEY = "contactKey";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_CONTACT + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CONTACTNAME + " TEXT, "
                + COLUMN_CONTACTNUMBER + " TEXT, "
                + COLUMN_MYKEY + " TEXT, "
                + COLUMN_CONTACTKEY + " TEXT "
                + ");";

        db.execSQL(query);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT );
        onCreate(db);
    }

    // Add new row to database
    public void addContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACTNAME, contact.get_contactName());
        values.put(COLUMN_CONTACTNUMBER, contact.getContactNumber());
        values.put(COLUMN_MYKEY, contact.getMyKey());
        values.put(COLUMN_CONTACTKEY, contact.getContactKey());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CONTACT, null, values);
        db.close();
    }

    public void deleteContact(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CONTACT + " WHERE " + COLUMN_ID + " = \"" + String.valueOf(id) + "\";" );
    }

    public Contact getContactByNumber(String number){

        String _id;
        String _contactName;
        String _contactNumber;
        String _myKey;
        String _contactKey;
        Contact contactPom;
        ArrayList<Contact> contactArrayList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACT + " WHERE " + COLUMN_CONTACTNUMBER + " = " + number;
        // Cursor point to a location in result
        Cursor cursor = db.rawQuery(query, null);
        // Move to the first row in result
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getString(cursor.getColumnIndex("contactName")) != null) {
                _id = cursor.getString(cursor.getColumnIndex("_id"));
                _contactName = cursor.getString(cursor.getColumnIndex("contactName"));
                _contactNumber = cursor.getString(cursor.getColumnIndex("contactNumber"));
                _contactKey = cursor.getString(cursor.getColumnIndex("contactKey"));
                _myKey = cursor.getString(cursor.getColumnIndex("myKey"));
                contactPom = new Contact(_id, _contactName, _contactNumber, _myKey, _contactKey);
                contactArrayList.add(contactPom);
                Log.i("DATABASE", _contactName);
            }
            cursor.moveToNext();
        }
        db.close();

        if (!contactArrayList.isEmpty()) return contactArrayList.get(0);
        else return null;
    }

    public ArrayList<Contact> getContacts() {

        String _id;
        String _contactName;
        String _contactNumber;
        String _myKey;
        String _contactKey;
        Contact contactPom;
        ArrayList<Contact> contactArrayList = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACT + " WHERE 1";

        // Cursor point to a location in result
        Cursor cursor = db.rawQuery(query, null);
        // Move to the first row in result
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getString(cursor.getColumnIndex("contactName")) != null) {
                _id = cursor.getString(cursor.getColumnIndex("_id"));
                _contactName = cursor.getString(cursor.getColumnIndex("contactName"));
                _contactNumber = cursor.getString(cursor.getColumnIndex("contactNumber"));
                _contactKey = cursor.getString(cursor.getColumnIndex("contactKey"));
                _myKey = cursor.getString(cursor.getColumnIndex("myKey"));
                contactPom = new Contact(_id, _contactName, _contactNumber, _myKey, _contactKey);
                contactArrayList.add(contactPom);
                Log.i("DATABASE", _contactName);
            }
            cursor.moveToNext();
        }
        db.close();

       if(contactArrayList.isEmpty()) {
           return  null;
       } else {
           return contactArrayList;
       }
    }
}
