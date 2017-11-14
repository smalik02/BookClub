package com.example.shehryarmalik.booklub;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class BookListActivity extends AppCompatActivity{
    private Realm realm;
    private FirebaseAuth mAuth;
    private static final String TAG = "MyActivity";
    private static final String REALM_TAG = "__REALM__";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Books"));
        tabLayout.addTab(tabLayout.newTab().setText("Recs"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

//    @Override
//    public void onTabChanged(String tabId) {
//        Toast.makeText(getApplicationContext(), tabId, Toast.LENGTH_SHORT).show();
//        if (tabId.equals("Recs")) {
//            setContentView(R.layout.activity_book_recs);
//        }
//    }

    public static Realm getRealm(Context context) {
        // noinspection ResourceType
        return (Realm)context.getSystemService(REALM_TAG);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if(REALM_TAG.equals(name)) {
            return realm;
        }
        return super.getSystemService(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
           deleteAllDone();
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteAllDone() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.close();
                try {
                    Realm.deleteRealm(realm.getConfiguration());
                } catch (Exception ex){
                    throw ex;
                }
            }
        });
    }

//    @Override
//    public void onTabChanged(String tabId) {
//        Toast.makeText(getApplicationContext(), tabId, Toast.LENGTH_SHORT).show();
//    }

    /**
     * Created by shehryarmalik on 11/13/17.
     */

    public static class MyBooks extends Fragment {
        private Realm realm;
        private FirebaseAuth mAuth;
        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_main, container,
                    false);
            realm = getRealm(getActivity());

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            RealmResults<Book> book = realm.where(Book.class).findAll();
            final BookAdapter adapter = new BookAdapter(MyBooks.this, book);

            ListView listView = (ListView) rootView.findViewById(R.id.book_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final Book book = (Book) adapterView.getAdapter().getItem(i);

                    LinearLayout layout = new LinearLayout(getActivity().getApplicationContext());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText titleBox = new EditText(getActivity().getApplicationContext());
                    titleBox.setText(book.getName());
                    titleBox.setHint("Title");
                    layout.addView(titleBox);

                    final EditText authorBox = new EditText(getActivity().getApplicationContext());
                    authorBox.setText(book.getAuthor());
                    authorBox.setHint("Author");
                    layout.addView(authorBox);

                    final EditText genreBox = new EditText(getActivity().getApplicationContext());
                    genreBox.setText(book.getGenre());
                    genreBox.setHint("Genre");
                    layout.addView(genreBox);

                    final EditText releaseBox = new EditText(getActivity().getApplicationContext());
                    releaseBox.setText(String.valueOf(book.getRelease_year()));
                    releaseBox.setHint("Release Year");
                    layout.addView(releaseBox);

                    final EditText lengthBox = new EditText(getActivity().getApplicationContext());
                    lengthBox.setText(String.valueOf(book.getLength()));
                    lengthBox.setHint("Length");
                    layout.addView(lengthBox);

                    final EditText bookEditText = new EditText(getActivity().getApplicationContext());
                    AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity() ,R.style.AlertDialogCustom) )
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
                }

            });
            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout layout = new LinearLayout(getActivity().getApplicationContext());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText titleBox = new EditText(getActivity().getApplicationContext());
                    titleBox.setHint("Title");
                    layout.addView(titleBox);

                    final EditText authorBox = new EditText(getActivity().getApplicationContext());
                    authorBox.setHint("Author");
                    layout.addView(authorBox);

                    final EditText genreBox = new EditText(getActivity().getApplicationContext());
                    genreBox.setHint("Genre");
                    layout.addView(genreBox);

                    final EditText releaseBox = new EditText(getActivity().getApplicationContext());
                    releaseBox.setHint("Release_Year");
                    layout.addView(releaseBox);

                    final EditText lengthBox = new EditText(getActivity().getApplicationContext());
                    lengthBox.setHint("Length");
                    layout.addView(lengthBox);
                    //                    final EditText bookEditText = new EditText(MainActivity.this);
                    //                    bookEditText.setMinLines(5);
                    AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
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

            Button signout = (Button) rootView.findViewById(R.id.button2);
            //          if (signout != null) {
            signout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signOut();
                    Intent new_intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    //                  new_intent.putExtra("main_activity", MainActivity.this);
                    startActivity(new_intent);
                }

            });
            return rootView;
        }

    //    public void onViewCreated(View v, Bundle savedInstanceState) {
    //        super.onViewCreated(v, savedInstanceState);
    //
    //
    //    }

        @Override
        public void onStart()
        {
            super.onStart();
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


        public void changeBookRead(final String taskId) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Book book = realm.where(Book.class).equalTo("id", taskId).findFirst();
                    book.setRead(!book.isRead());
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


    }
}





//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
//        tabHost.setup();
//        tabHost.setOnTabChangedListener(this);
////        if (tabHost != null) {
//            TabHost.TabSpec Books = tabHost.newTabSpec("Books");
//            TabHost.TabSpec Recs = tabHost.newTabSpec("Recs");
//            TabHost.TabSpec Settings = tabHost.newTabSpec("Settings");
//
//            Books.setIndicator("Books");
////            Intent intent = new Intent(this, BookListActivity.class);
////            Books.setContent(intent);
//            Books.setContent(new TabHost.TabContentFactory() {
//
//                                 public View createTabContent(String tag) {
//                                     return new TextView(BookListActivity.this);
//                                 }});
//
//            Recs.setIndicator("Recs");
////            intent = new Intent(this, BookRecs.class);
////            Recs.setContent(intent);
//            Recs.setContent(new TabHost.TabContentFactory() {
//
//                public View createTabContent(String tag) {
//                    return new TextView(BookListActivity.this);
//                }});
//
//            Settings.setIndicator("Settings");
////            intent = new Intent(this, BookListActivity.class);
////            Settings.setContent(intent);
//            Settings.setContent(new TabHost.TabContentFactory() {
//
//                public View createTabContent(String tag) {
//                    return new TextView(BookListActivity.this);
//                }});
//
//
//            tabHost.addTab(Books);
//            tabHost.addTab(Recs);
//            tabHost.addTab(Settings);
//            tabHost.setCurrentTab(0);
//        }
//        tabHost.setOnTabChangeaListener( new TabHost.OnTabChangeListener() {
//            @Override
//            public void onTabChanged(String tabId) {
//                // display the name of the tab whenever a tab is changed
//                Toast.makeText(getApplicationContext(), tabId, Toast.LENGTH_SHORT).show();
//            }
//        });
//
