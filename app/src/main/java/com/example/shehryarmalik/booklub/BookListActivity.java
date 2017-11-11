package com.example.shehryarmalik.booklub;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.shehryarmalik.booklub.models.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import android.view.View.OnClickListener;

/**
 * Created by shehryarmalik on 10/22/17.
 */

public class BookListActivity extends AppCompatActivity {
    private Realm realm;
    private FirebaseAuth mAuth;
    private static final String TAG = "MyActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("books.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        realm = Realm.getDefaultInstance();

        RealmResults<Book> book = realm.where(Book.class).findAll();
        final BookAdapter adapter = new BookAdapter(BookListActivity.this, book);

        ListView listView = (ListView) findViewById(R.id.book_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Book book = (Book) adapterView.getAdapter().getItem(i);

                LinearLayout layout = new LinearLayout(BookListActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText titleBox = new EditText(BookListActivity.this);
                titleBox.setText(book.getName());
                titleBox.setHint("Title");
                layout.addView(titleBox);

                final EditText authorBox = new EditText(BookListActivity.this);
                authorBox.setText(book.getAuthor());
                authorBox.setHint("Author");
                layout.addView(authorBox);

                final EditText genreBox = new EditText(BookListActivity.this);
                genreBox.setText(book.getGenre());
                genreBox.setHint("Genre");
                layout.addView(genreBox);

                final EditText releaseBox = new EditText(BookListActivity.this);
                releaseBox.setText(String.valueOf(book.getRelease_year()));
                releaseBox.setHint("Release Year");
                layout.addView(releaseBox);

                final EditText lengthBox = new EditText(BookListActivity.this);
                lengthBox.setText(String.valueOf(book.getLength()));
                lengthBox.setHint("Length");
                layout.addView(lengthBox);

                final EditText bookEditText = new EditText(BookListActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(BookListActivity.this)
                        .setTitle("Edit Book")
                        .setView(layout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changeTaskName(book.getId(), String.valueOf(titleBox.getText()),
                                        String.valueOf(authorBox.getText()), String.valueOf(genreBox.getText()),
                                        Integer.parseInt(releaseBox.getText().toString()),
                                        Integer.parseInt(lengthBox.getText().toString()));

                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteTask(book.getId());
                            }
                        })
                        .create();
                dialog.show();
//                int button = view.getId();
//                if (button == R.id.sign_out_button) {
//                    Log.v(TAG, "in sign out!!");
//                    mAuth.signOut();
//                }
            }

        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(BookListActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText titleBox = new EditText(BookListActivity.this);
                titleBox.setHint("Title");
                layout.addView(titleBox);

                final EditText authorBox = new EditText(BookListActivity.this);
                authorBox.setHint("Author");
                layout.addView(authorBox);

                final EditText genreBox = new EditText(BookListActivity.this);
                genreBox.setHint("Genre");
                layout.addView(genreBox);

                final EditText releaseBox = new EditText(BookListActivity.this);
                releaseBox.setHint("Release_Year");
                layout.addView(releaseBox);

                final EditText lengthBox = new EditText(BookListActivity.this);
                lengthBox.setHint("Length");
                layout.addView(lengthBox);
                //                    final EditText bookEditText = new EditText(MainActivity.this);
                //                    bookEditText.setMinLines(5);
                AlertDialog dialog = new AlertDialog.Builder(BookListActivity.this)
                        .setTitle("Add Book")
                        .setView(layout)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Book new_book = realm.createObject(Book.class, UUID.randomUUID().toString());
                                        new_book.setName(String.valueOf(titleBox.getText()));
                                        new_book.setAuthor(String.valueOf(authorBox.getText()));
                                        new_book.setGenre(String.valueOf(genreBox.getText()));
                                        new_book.setRelease_year(Integer.parseInt(releaseBox.getText().toString()));
                                        new_book.setLength(Integer.parseInt(lengthBox.getText().toString()));
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

        Button signout = (Button) findViewById(R.id.button2);
//        if (signout != null) {
            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signOut();
                    Intent new_intent = new Intent(BookListActivity.this, MainActivity.class);
//            new_intent.putExtra("main_activity", MainActivity.this);
                    startActivity(new_intent);
                }

            });
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void changeBookRead(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Book book = realm.where(Book.class).equalTo("id", taskId).findFirst();
                book.setRead(!book.isRead());
            }
        });
    }

    private void changeTaskName(final String taskId, final String title, final String author,
                                final String genre, final int release_year, final int length) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Book book = realm.where(Book.class).equalTo("id", taskId).findFirst();
                book.setName(title);
                book.setAuthor(author);
                book.setGenre(genre);
                book.setRelease_year(release_year);
                book.setLength(length);
            }
        });
    }

    private void deleteTask(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Book.class).equalTo("id", taskId)
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }

    private void deleteAllDone() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Book.class).equalTo("done", true)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }
}

//    @Override
//    public void onClick(View v)
//    {
////        Bundle extras = getIntent().getExtras();
////        MainActivity activity = extras.getParcelable("main_activity");
//        int i = v.getId();
//        if (i == R.id.sign_out_button) {
//            mAuth.signOut();
//        }
//    }
//}
