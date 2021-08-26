package com.cxj170002.fingerpaint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/* Written by Christy Jacob for CS4301.002, assignment 6 Fingerpainting Program, starting May 3, 2021.
    NetID: cxj170002
    This is the database helper class which helps communicate with the database by writing to the database
    and getting from the database.
 */
public class DatabaseHelper {

    private SQLiteDatabase mDB;
    public DatabaseHelper(Context context)
    {
        mDB = new DatabaseIO(context).getWritableDatabase();
    }

    // method to handle writing to the database
    public void writeDB(String name, String info, boolean isUpdate)
    {
        if(isUpdate) //updating the database if it is an existing painting
        {
            ContentValues cv = new ContentValues();
            cv.put("Information", info);
            mDB.update("Drawings", cv, "where Name='" + name + "'", null);
        }
        else // adding to the database if it is not existing
        {
            ContentValues cv = new ContentValues();
            cv.put("Name", name);
            cv.put("Information", info);
            mDB.insert("Drawings", null, cv);

        }

    }

    //method to handle getting frome database
    public String getFromDB(String name)
    {
        // using cursor to get from database
        Cursor cursor = null;

        // trying to get the information
        try{
            cursor = mDB.query("Drawings", null, "where Name='" + name + "'", null, null, null, null);
            return cursor.getString(cursor.getColumnIndexOrThrow("Information"));
        } catch (Exception e) {
            System.out.println("Error");
        }

        return "Error";
    }

}
