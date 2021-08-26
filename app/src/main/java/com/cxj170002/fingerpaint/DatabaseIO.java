package com.cxj170002.fingerpaint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/* Written by Christy Jacob for CS4301.002, assignment 6 Fingerpainting Program, starting May 3, 2021.
    NetID: cxj170002
    This class creates a database to be used to store drawings.
 */

public class DatabaseIO extends SQLiteOpenHelper {

    public static final String DB_NAME = "DrawData";
    public static final int DB_VERSION = 1;

    public DatabaseIO(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table " + "Drawings" + "(" + " _id integer primary key autoincrement" + ", " +
                "Name" + " varchar(25) NOT NULL, " +
                "Information" + " varchar(10000) NOT NULL" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
