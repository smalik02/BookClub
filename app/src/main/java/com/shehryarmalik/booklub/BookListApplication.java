/**
 * Created by shehryarmalik on 10/22/17.
 */

package com.shehryarmalik.booklub;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class BookListApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("books.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
