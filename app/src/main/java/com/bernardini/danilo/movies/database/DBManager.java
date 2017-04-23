package com.bernardini.danilo.movies.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DBManager {

    private DBHelper dbHelper;

    public DBManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void insert(String type, String list, String id, String title, String path) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBContract.ID, id);
        cv.put(DBContract.TITLE, title);
        cv.put(DBContract.PATH, path);
        try {
            switch (list) {
                case "seen": {
                    if (type.equals("movie"))
                        db.insert(DBContract.SEEN_MOVIES_TABLE_NAME, null, cv);
                    else
                        db.insert(DBContract.SEEN_TV_TABLE_NAME, null, cv);
                    break;
                }
                case "own": {
                    if (type.equals("movie"))
                        db.insert(DBContract.OWN_MOVIES_TABLE_NAME, null, cv);
                    else
                        db.insert(DBContract.WATCHING_TV_TABLE_NAME, null, cv);
                    break;
                }
                case "wish": {
                    if (type.equals("movie"))
                        db.insert(DBContract.WISH_MOVIES_TABLE_NAME, null, cv);
                    else
                        db.insert(DBContract.WISH_TV_TABLE_NAME, null, cv);
                    break;
                }
            }
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public void update(String type, String list, String id, String title, String path) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBContract.ID, id);
        cv.put(DBContract.TITLE, title);
        cv.put(DBContract.PATH, path);
        try {
            switch (list) {
                case "seen": {
                    if (type.equals("movie"))
                        db.updateWithOnConflict(DBContract.SEEN_MOVIES_TABLE_NAME, cv, null, null, SQLiteDatabase.CONFLICT_IGNORE);
                    else
                        db.updateWithOnConflict(DBContract.SEEN_TV_TABLE_NAME, cv, null, null, SQLiteDatabase.CONFLICT_IGNORE);
                    break;
                }
                case "own": {
                    if (type.equals("movie"))
                        db.updateWithOnConflict(DBContract.OWN_MOVIES_TABLE_NAME, cv, null, null, SQLiteDatabase.CONFLICT_IGNORE);
                    else
                        db.updateWithOnConflict(DBContract.WATCHING_TV_TABLE_NAME, cv, null, null, SQLiteDatabase.CONFLICT_IGNORE);
                    break;
                }
                case "wish": {
                    if (type.equals("movie"))
                        db.updateWithOnConflict(DBContract.WISH_MOVIES_TABLE_NAME, cv, null, null, SQLiteDatabase.CONFLICT_IGNORE);
                    else
                        db.updateWithOnConflict(DBContract.WISH_TV_TABLE_NAME, cv, null, null, SQLiteDatabase.CONFLICT_IGNORE);
                    break;
                }
            }
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public boolean delete(String type, String list, String id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            switch (list) {
                case "seen": {
                    if (type.equals("movie")) {
                        if (db.delete(DBContract.SEEN_MOVIES_TABLE_NAME, DBContract.ID + "=?", new String[]{id}) > 0)
                            return true;
                        return false;
                    }
                    else {
                        if (db.delete(DBContract.SEEN_TV_TABLE_NAME, DBContract.ID + "=?", new String[]{id}) > 0)
                            return true;
                        return false;
                    }
                }
                case "own": {
                    if (type.equals("movie")) {
                        if (db.delete(DBContract.OWN_MOVIES_TABLE_NAME, DBContract.ID + "=?", new String[]{id}) > 0)
                            return true;
                        return false;
                    }
                    else {
                        if (db.delete(DBContract.WATCHING_TV_TABLE_NAME, DBContract.ID + "=?", new String[]{id}) > 0)
                            return true;
                        return false;
                    }
                }
                case "wish": {
                    if (type.equals("movie")) {
                        if (db.delete(DBContract.WISH_MOVIES_TABLE_NAME, DBContract.ID + "=?", new String[]{id}) > 0)
                            return true;
                        return false;
                    }
                    else {
                        if (db.delete(DBContract.WISH_TV_TABLE_NAME, DBContract.ID + "=?", new String[]{id}) > 0)
                            return true;
                        return false;
                    }
                }
            }

        }
        catch (SQLiteException sqle){
            return false;
        }
        return false;
    }



    public Cursor query(String type, String list) {
        Cursor cursor = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try{
            switch (list) {
                case "seen": {
                    if (type.equals("movie"))
                        cursor = db.query(DBContract.SEEN_MOVIES_TABLE_NAME, null, null, null, null, null, null);
                    else
                        cursor = db.query(DBContract.SEEN_TV_TABLE_NAME, null, null, null, null, null, null);
                    break;
                }
                case "own": {
                    if (type.equals("movie"))
                        cursor = db.query(DBContract.OWN_MOVIES_TABLE_NAME, null, null, null, null, null, null);
                    else
                        cursor = db.query(DBContract.WATCHING_TV_TABLE_NAME, null, null, null, null, null, null);
                    break;
                }
                case "wish": {
                    if (type.equals("movie"))
                        cursor = db.query(DBContract.WISH_MOVIES_TABLE_NAME, null, null, null, null, null, null, null);
                    else
                        cursor = db.query(DBContract.WISH_TV_TABLE_NAME, null, null, null, null, null, null, null);
                    break;
                }
            }
        }
        catch(SQLiteException sqle){
            sqle.printStackTrace();
        }
        return cursor;
    }

}


