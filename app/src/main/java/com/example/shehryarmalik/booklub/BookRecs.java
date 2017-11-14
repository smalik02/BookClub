package com.example.shehryarmalik.booklub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


public class BookRecs extends Fragment {


    private static final String TAG = "MyActivity";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_book_recs, container, false);
    }

//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();

//        Realm.init(this);
////        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
////                .name("books.realm")
////                .schemaVersion(0)
////                .build();
////        Realm.setDefaultConfiguration(realmConfig);
//
//        realm = Realm.getDefaultInstance();
        //Toast.makeText(getApplicationContext(), "in BookRecs", Toast.LENGTH_SHORT).show();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
//        tabHost.setup();
//        if (tabHost != null) {
//            TabHost.TabSpec Books = tabHost.newTabSpec("Books");
//            TabHost.TabSpec Recs = tabHost.newTabSpec("Recs");
//            TabHost.TabSpec Settings = tabHost.newTabSpec("Settings");
//
//            Books.setIndicator("Books");
//            Books.setContent(new TabHost.TabContentFactory() {
//
//                public View createTabContent(String tag) {
//                    return new TextView(BookRecs.this);
//                }
//            });
//
//            Recs.setIndicator("Recs");
//            Recs.setContent(new TabHost.TabContentFactory() {
//
//                public View createTabContent(String tag) {
//                    return new TextView(BookRecs.this);
//                }});
//
//            Settings.setIndicator("Settings");
//            Settings.setContent(new TabHost.TabContentFactory() {
//
//                public View createTabContent(String tag) {
//                    return new TextView(BookRecs.this);
////                            .setOnClickListener(new View.OnClickListener(){
////                        public void onClick(View v){
////                            Intent intent = new Intent(this, BookListActivity.class);
////                            startActivity(intent);
////                        }
////                    });
//                }});
//            tabHost.addTab(Books);
//            tabHost.addTab(Recs);
//            tabHost.addTab(Settings);
//        }


}
