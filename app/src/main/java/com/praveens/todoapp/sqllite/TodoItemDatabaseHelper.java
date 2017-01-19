package com.praveens.todoapp.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.praveens.todoapp.model.Todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.id;
import static android.content.ContentValues.TAG;

/**
 * Created by praveens on 1/17/17.
 */

public class TodoItemDatabaseHelper extends SQLiteOpenHelper {

    private static TodoItemDatabaseHelper sInstance;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // Database Info
    private static final String DATABASE_NAME = "todoDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TODO = "todo";

    // TODO Table Columns
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TEXT = "text";
    private static final String KEY_TODO_CREATED_ON = "createdOn";
    private static final String KEY_TODO_MODIFIED_ON = "modifiedOn";

    public static synchronized TodoItemDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoItemDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private TodoItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY, " +
                KEY_TODO_TEXT + " TEXT, " +
                KEY_TODO_CREATED_ON + " DATE, " +
                KEY_TODO_MODIFIED_ON + " DATE" +
                ")";

        sqLiteDatabase.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
            onCreate(sqLiteDatabase);
        }
    }

    // CREATE
    public long addTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        long id = 0;
        try {
            ContentValues values = new ContentValues();
            Date now = new Date();
            values.put(KEY_TODO_TEXT, todo.text);
            values.put(KEY_TODO_CREATED_ON, dateFormat.format(now));
            values.put(KEY_TODO_MODIFIED_ON, dateFormat.format(now));

            id = db.insertOrThrow(TABLE_TODO, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add to todo database:" + e);
        } finally {
            db.endTransaction();
        }
        return id;
    }

    // GET by id
    public Todo getTodo(long id) {
        String TODO_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE id=%s",
                        TABLE_TODO, id
                );

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODO_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) do {
                Todo todo = new Todo();
                todo.id = cursor.getLong(cursor.getColumnIndex(KEY_TODO_ID));
                todo.text = cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT));

                Date creationDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_TODO_CREATED_ON)));
                todo.createdOn = creationDate;

                Date modifiedDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_TODO_MODIFIED_ON)));
                todo.modifiedOn = modifiedDate;

                return todo;
            } while (cursor.moveToNext());
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get todo items from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    // GETALL
    public List<Todo> getAllTodos() {
        List<Todo> todoItems = new ArrayList<Todo>();

        String TODOS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_TODO
                );

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODOS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) do {
                Todo todo = new Todo();
                todo.id = cursor.getLong(cursor.getColumnIndex(KEY_TODO_ID));
                todo.text = cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT));

                Date creationDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_TODO_CREATED_ON)));
                todo.createdOn = creationDate;

                Date modifiedDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_TODO_MODIFIED_ON)));
                todo.modifiedOn = modifiedDate;

                todoItems.add(todo);
            } while (cursor.moveToNext());
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get todo items from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todoItems;
    }

    // UPDATE
    public int updateToDo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        Date now = new Date();

        values.put(KEY_TODO_TEXT, todo.text);
        values.put(KEY_TODO_MODIFIED_ON, dateFormat.format(now));

        return db.update(TABLE_TODO, values, KEY_TODO_ID + " = ?",
                new String[]{String.valueOf(todo.id)});
    }

    // DELETE
    public void deleteTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TODO, KEY_TODO_ID + " = ?", new String[]{String.valueOf(todo.id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete todo:" + todo);
        } finally {
            db.endTransaction();
        }
    }

}
