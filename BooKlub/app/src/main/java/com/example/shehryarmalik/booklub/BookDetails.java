package com.example.shehryarmalik.booklub;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
//

import java.util.ArrayList;

public class BookDetails extends ActionBarActivity implements android.view.View.OnClickListener{

    Button btnSave ,  btnDelete;
    Button btnClose;
    EditText editTextName;
    EditText editTextGenre;
    private int _Book_Id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnClose = (Button) findViewById(R.id.btnClose);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextGenre = (EditText) findViewById(R.id.editTextGenre);

        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnClose.setOnClickListener(this);


        _Book_Id = 0;
        Intent intent = getIntent();
        _Book_Id =intent.getIntExtra("book_Id", 0);
        book_data_store.BookRepo repo = new book_data_store.BookRepo(this);
        book_data_store.Book book = new book_data_store.Book();
        book = repo.getBookById(_Book_Id);

        editTextName.setText(book.name);
        editTextGenre.setText(book.genre);
    }



    public void onClick(View view) {
        if (view == findViewById(R.id.btnSave)){
            book_data_store.BookRepo repo = new book_data_store.BookRepo(this);
            book_data_store.Book book = new book_data_store.Book();

            book.genre = editTextGenre.getText().toString();
            book.name  = editTextName.getText().toString();

            book.book_ID = _Book_Id;

            if (_Book_Id == 0){
                _Book_Id = repo.insert(book);

                Toast.makeText(this,"New Book Insert",Toast.LENGTH_SHORT).show();
            }
            else{
                repo.update(book);
                Toast.makeText(this,"Book Record updated",Toast.LENGTH_SHORT).show();
            }
        }
        else if (view== findViewById(R.id.btnDelete)){
            book_data_store.BookRepo repo = new book_data_store.BookRepo(this);
            repo.delete(_Book_Id);
            Toast.makeText(this, "Book Record Deleted", Toast.LENGTH_SHORT);
            finish();
        }
        else if (view == findViewById(R.id.btnClose)){
            finish();
        }


    }

}