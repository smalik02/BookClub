package com.shehryarmalik.booklub;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.shehryarmalik.booklub.models.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


/**
 * Created by shehryarmalik on 10/22/17.
 */

public class BookListActivity extends AppCompatActivity{
    private Realm realm;
    private FirebaseAuth mAuth;
    private static final String TAG = "MyActivity";
    private static final String REALM_TAG = "__REALM__";
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

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
        return Realm.getDefaultInstance();
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


    /**
     * Created by shehryarmalik on 11/13/17.
     */

    public static class MyBooks extends Fragment implements RealmChangeListener {
        private Realm realm;
        private FirebaseAuth mAuth;

        private RecyclerView recyclerView;
        private BookAdapter adapter;
        private ArrayList<Book> bookList;
        private static final int ANIMATION_DURATION = 500;

        LinearLayoutManager layoutManager;
        SwipeRefreshLayout_ swipeRefreshLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.activity_main, container,
                    false);
            realm = getRealm(getActivity());

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            RealmResults<Book> book = realm.where(Book.class).findAll();

//            initCollapsingToolbar();

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

//            swipeRefreshLayout = (SwipeRefreshLayout_) rootView.findViewById(R.id.swipeRefreshLayout);
//            swipeRefreshLayout.setColorSchemeResources(R.color.realm_red, R.color.realm_blue);

//          bookList = new RealmResults<book>;

            layoutManager = new LinearLayoutManager(recyclerView.getContext());
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(ANIMATION_DURATION);
            itemAnimator.setRemoveDuration(ANIMATION_DURATION);
            recyclerView.setItemAnimator(itemAnimator);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            adapter = new BookAdapter(getContext(), this, book, new CustomItemClickListener() {
                @Override
                public void onItemClick(final Book book) {
//                    String bookId = bookList.get(position).getId();
                    LinearLayout layout = new LinearLayout(rootView.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText titleBox = new EditText(rootView.getContext());
                    titleBox.setText(book.getName());
                    titleBox.setHint("Title");
                    layout.addView(titleBox);

                    final EditText authorBox = new EditText(rootView.getContext());
                    authorBox.setText(book.getAuthor());
                    authorBox.setHint("Author");
                    layout.addView(authorBox);

                    final EditText genreBox = new EditText(rootView.getContext());
                    genreBox.setText(book.getGenre());
                    genreBox.setHint("Genre");
                    layout.addView(genreBox);

                    final EditText releaseBox = new EditText(rootView.getContext());
                    releaseBox.setText(String.valueOf(book.getRelease_year()));
                    releaseBox.setHint("Release Year");
                    layout.addView(releaseBox);

                    final EditText lengthBox = new EditText(rootView.getContext());
                    lengthBox.setText(String.valueOf(book.getLength()));
                    lengthBox.setHint("Length");
                    layout.addView(lengthBox);

                    final EditText bookEditText = new EditText(rootView.getContext());
                    AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(rootView.getContext(), R.style.AlertDialogCustom))
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

            recyclerView.setAdapter(adapter);
            realm.addChangeListener(this);

            //swipeRefreshLayout.setOnRefreshListener(this);

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

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    checkLoadMore();
                }
            });

            return rootView;
        }



        /**
         * Converting dp to pixel
         */
        private int dpToPx(int dp) {
            Resources r = getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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


        public void changeTaskName(final String taskId, final String title, final String author,
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
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Book book = realm.where(Book.class).equalTo("id", taskId).findFirst();
                    book.setRead(!book.isRead());
                }
            });
        }


        public void deleteTask(final String taskId) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(Book.class).equalTo("id", taskId).findFirst().deleteFromRealm();
                }
            });
        }


        /**
         * RealmChangeListener
         */
        private int itemCount;

        @Override
        public void onChange(Object o) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (itemCount < adapter.getItemCount())
                        adapter.notifyItemRangeInserted(itemCount, adapter.getItemCount() - itemCount);
                    else
                        adapter.notifyItemRangeRemoved(itemCount, itemCount - adapter.getItemCount());

                    itemCount = adapter.getItemCount();
                }
            });

        }

        /**
         * Check whether to load more data or not
         */
        private static final int THRESHOLD = 3;

        private void checkLoadMore() {
//            if (!swipeRefreshLayout.isRefreshing() && adapter.getItemCount() <= layoutManager.findLastVisibleItemPosition() + THRESHOLD) {
//                loadMoreData();
//            }
        }

        private int page;

        private void loadMoreData() {
            swipeRefreshLayout.setRefreshing(true);
//            BooksAPI.getFeed(page++, new BooksAPI.OnResponseListener<List<Book>>() {
//                @Override
//                public void onResponseRetrieved(List<Book> books, Exception e) {
//                    swipeRefreshLayout.setRefreshing(false);
//
//                    realm.beginTransaction();
//                    realm.copyToRealm(books);
//                    realm.commitTransaction();
//                }
//            });
        }

//        @Override
//        public void onRefresh() {
//
//            realm.beginTransaction();
//            realm.delete(Book.class);
//            realm.commitTransaction();
//
//            page = 0;
//            loadMoreData();
//
//        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
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


/**
 * Initializing collapsing toolbar
 * Will show and hide the toolbar title on scroll
 */
//        private void initCollapsingToolbar() {
//            final CollapsingToolbarLayout collapsingToolbar =
//                    (CollapsingToolbarLayout) getView().findViewById(R.id.collapsing_toolbar);
//            collapsingToolbar.setTitle(" ");
//            AppBarLayout appBarLayout = (AppBarLayout) getView().findViewById(R.id.appbar);
//            appBarLayout.setExpanded(true);
//
//            // hiding & showing the title when toolbar expanded & collapsed
//            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//                boolean isShow = false;
//                int scrollRange = -1;
//
//                @Override
//                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                    if (scrollRange == -1) {
//                        scrollRange = appBarLayout.getTotalScrollRange();
//                    }
//                    if (scrollRange + verticalOffset == 0) {
//                        collapsingToolbar.setTitle(getString(R.string.app_name));
//                        isShow = true;
//                    } else if (isShow) {
//                        collapsingToolbar.setTitle(" ");
//                        isShow = false;
//                    }
//                }
//            });
//        }
