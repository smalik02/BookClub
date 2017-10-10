package com.example.shehryarmalik.booklub;

import android.app.ListActivity;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import book_data_store.BookRepo;

public class MainActivity extends ListActivity  implements android.view.View.OnClickListener{

    Button btnAdd,btnGetAll;
    TextView book_Id;

    @Override
    public void onClick(View view) {
        if (view== findViewById(R.id.btnAdd)){

            Intent intent = new Intent(this,BookDetails.class);
            intent.putExtra("student_Id",0);
            startActivity(intent);

        }else {

            BookRepo repo = new BookRepo(this);

            ArrayList<HashMap<String, String>> bookList =  repo.getBookList();
            if(bookList.size()!=0) {
                ListView lv = getListView();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                        book_Id = (TextView) view.findViewById(R.id.book_Id);
                        String bookId = book_Id.getText().toString();
                        Intent objIndent = new Intent(getApplicationContext(),BookDetails.class);
                        objIndent.putExtra("book_Id", Integer.parseInt( bookId));
                        startActivity(objIndent);
                    }
                });
                ListAdapter adapter = new SimpleAdapter( MainActivity.this, bookList, R.layout.view_book_detail, new String[] { "id","name"}, new int[] {R.id.book_Id, R.id.book_name});
                setListAdapter(adapter);
            }else{
                Toast.makeText(this,"No book!",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnGetAll = (Button) findViewById(R.id.btnGetAll);
        btnGetAll.setOnClickListener(this);

    }


}