package com.shehryarmalik.booklub;


import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by shehryarmalik on 11/29/17.
 */

public class TabFragment extends Fragment {
    private static final String ARG_KEY_NUMBER = "tab_number";

    public static TabFragment newInstance(int number) {
        Bundle args = new Bundle();
        args.putInt(ARG_KEY_NUMBER, number);

        TabFragment frag = new TabFragment();
        frag.setArguments(args);

        return frag;
    }
}
