package com.shehryarmalik.booklub;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.shehryarmalik.booklub.models.Book;

/**
 * Created by shehryarmalik on 12/3/17.
 */

public class BooksAPI {

    public interface OnResponseListener<E> {
        void onResponseRetrieved(E object, Exception e);
    }

    private static final int POST_PER_PAGE = 15;
    private static final int DELAY = 2000;

    public static void getFeed(final int page, final OnResponseListener<List<Book>> listener) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Book> posts = new ArrayList<Book>();
                for (int i = 0; i < POST_PER_PAGE; i++) {
                    Book post = new Book();
                    posts.add(post);
                }

                if (listener != null)
                    listener.onResponseRetrieved(posts, null);
            }
        }, DELAY);
    }

    private static final String ALLOWED_CHARACTERS = "qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}
