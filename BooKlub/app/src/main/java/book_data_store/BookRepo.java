package book_data_store;

/**
 * Created by shehryarmalik on 10/9/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.HashMap;


public class BookRepo {
    private DBHelper dbHelper;

    public BookRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    public int insert(Book book) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Book.KEY_genre, book.genre);
        values.put(Book.KEY_name, book.name);

        // Inserting Row
        long book_Id = db.insert(Book.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) book_Id;
    }

    public void delete(int book_Id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Book.TABLE, Book.KEY_ID + "= ?", new String[]{String.valueOf(book_Id)});
        db.close(); // Closing database connection
    }

    public void update(Book book) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Book.KEY_genre, book.genre);
        values.put(Book.KEY_name, book.name);

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Book.TABLE, values, Book.KEY_ID + "= ?", new String[]{String.valueOf(book.book_ID)});
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getBookList() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Book.KEY_ID + "," +
                Book.KEY_name + "," +
                Book.KEY_genre +
                " FROM " +
                Book.TABLE;

        //Student student = new Student();
        ArrayList<HashMap<String, String>> bookList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> book = new HashMap<String, String>();
                book.put("id", cursor.getString(cursor.getColumnIndex(Book.KEY_ID)));
                book.put("name", cursor.getString(cursor.getColumnIndex(Book.KEY_name)));
                bookList.add(book);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookList;

    }

    public Book getBookById(int Id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                Book.KEY_ID + "," +
                Book.KEY_name + "," +
                Book.KEY_genre +
                " FROM " + Book.TABLE
                + " WHERE " +
                Book.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount = 0;
        Book book = new Book();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(Id)});

        if (cursor.moveToFirst()) {
            do {
                book.book_ID = cursor.getInt(cursor.getColumnIndex(Book.KEY_ID));
                book.name = cursor.getString(cursor.getColumnIndex(Book.KEY_name));
                book.genre = cursor.getString(cursor.getColumnIndex(Book.KEY_genre));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return book;
    }
}