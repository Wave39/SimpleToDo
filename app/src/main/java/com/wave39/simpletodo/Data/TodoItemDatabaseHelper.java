package com.wave39.simpletodo.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wave39.simpletodo.Model.ListItem;

import java.util.ArrayList;

/**
 * TodoItemDatabaseHelper
 * Created by bp on 2/7/17.
 */

public class TodoItemDatabaseHelper extends SQLiteOpenHelper {
    private static TodoItemDatabaseHelper sInstance;

    private static final String TAG = "TodoItemDatabaseHelper";

    private static final String DATABASE_NAME = "todoDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_LISTITEMS = "listitems";

    private static final String COLUMN_LISTITEM_ID = "listitemid";
    private static final String COLUMN_LISTITEM_STRING = "listitemstring";
    private static final String COLUMN_SORT_ORDER = "sortorder";

    private TodoItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized TodoItemDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoItemDatabaseHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_LISTITEMS +
                "(" +
                COLUMN_LISTITEM_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_LISTITEM_STRING + " TEXT, " +
                COLUMN_SORT_ORDER + " INTEGER" +
                ")";

        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTITEMS);
            onCreate(db);
        }
    }

    public long addOrUpdateListItem(ListItem listItem) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_LISTITEM_STRING, listItem.listItemString);
            values.put(COLUMN_SORT_ORDER, listItem.sortOrder);

            int rows = db.update(TABLE_LISTITEMS, values, COLUMN_LISTITEM_ID + "= ?",
                    new String[]{Integer.toString(listItem.listItemID)});
            if (rows == 1) {
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        COLUMN_LISTITEM_ID, TABLE_LISTITEMS, COLUMN_LISTITEM_ID);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{Integer.toString(listItem.listItemID)});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                userId = db.insertOrThrow(TABLE_LISTITEMS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update item");
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    public ArrayList<ListItem> getAllListItems() {
        ArrayList<ListItem> listItems = new ArrayList<>();

        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_LISTITEMS);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    ListItem newListItem = new ListItem();
                    newListItem.listItemID = cursor.getInt(cursor.getColumnIndex(COLUMN_LISTITEM_ID));
                    newListItem.listItemString = cursor.getString(cursor.getColumnIndex(COLUMN_LISTITEM_STRING));
                    newListItem.sortOrder = cursor.getInt(cursor.getColumnIndex(COLUMN_SORT_ORDER));
                    listItems.add(newListItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get items from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return listItems;
    }

    public void deleteAllListItems() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_LISTITEMS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all items");
        } finally {
            db.endTransaction();
        }
    }

}
