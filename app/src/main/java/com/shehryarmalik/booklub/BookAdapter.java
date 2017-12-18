package com.shehryarmalik.booklub;

/**
 * Created by shehryarmalik on 10/22/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.shehryarmalik.booklub.models.Book;

import io.realm.RealmResults;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{

    private BookListActivity activity;
    private BookListActivity.MyBooks activity2;

    private Context mContext;
    private RealmResults<Book> bookList;

    private CustomItemClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView bookName;
        public TextView author;
        public TextView genre;
        public TextView releaseYear;
        public TextView length;
        public CheckBox isRead;


        public ViewHolder(View view) {
            super(view);
            bookName = (TextView) view.findViewById(R.id.book_name);
            author = (TextView) view.findViewById(R.id.author);
            genre = (TextView) view.findViewById(R.id.genre);
            releaseYear = (TextView) view.findViewById(R.id.release_year);
            length = (TextView) view.findViewById(R.id.length);
            isRead = (CheckBox) view.findViewById(R.id.is_read);

        }

        public void bind(final Book book, final CustomItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                    listener.onItemClick(book);
                }
            });
        }

    }

    BookAdapter(Context context, BookListActivity.MyBooks activity, RealmResults<Book> data, CustomItemClickListener listener) {
        this.mContext = context;
        this.bookList = data;
        this.activity2 = activity;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list, parent, false);
        final ViewHolder mViewHolder = new ViewHolder(itemView);
        return mViewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Book book = bookList.get(position);
        String book_name = "Title: " + book.getName();
        String author_name = "Author: " + book.getAuthor();
        String genre = "Genre: " + book.getGenre();
        String year = "Year: " + String.valueOf(book.getRelease_year());
        String length = "Pages: " + String.valueOf(book.getLength());
        holder.bookName.setText(book_name);
        holder.author.setText(author_name);
        holder.genre.setText(genre);
        holder.releaseYear.setText(year);
        holder.length.setText(length);
        holder.bind(bookList.get(position), listener);

//        // loading album cover using Glide library
//        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

//        holder.overflow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showPopupMenu(holder.overflow);
//            }
//        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.book_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }


    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem bookItem) {
            switch (bookItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

}




//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final ViewHolder viewHolder;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(parent.getContext())
//                                .inflate(R.layout.book_list, parent, false);
//            viewHolder = new ViewHolder(convertView);
//            viewHolder.bookName = (TextView) convertView.findViewById(R.id.book_name);
//            viewHolder.author = (TextView) convertView.findViewById(R.id.author);
//            viewHolder.genre = (TextView) convertView.findViewById(R.id.genre);
//            viewHolder.releaseYear = (TextView) convertView.findViewById(R.id.release_year);
//            viewHolder.length = (TextView) convertView.findViewById(R.id.length);
//            viewHolder.isRead = (CheckBox) convertView.findViewById(R.id.is_read);
//            viewHolder.isRead.setOnClickListener(listener);
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        if (adapterData != null) {
//            Book book = adapterData.get(position);
//            viewHolder.bookName.setText(book.getName());
//            viewHolder.isRead.setChecked(book.isRead());
//            viewHolder.author.setText(book.getAuthor());
//            viewHolder.genre.setText(book.getGenre());
//            viewHolder.releaseYear.setText(""+book.getRelease_year());
//            viewHolder.length.setText(""+book.getLength());
//            viewHolder.isRead.setTag(position);
//        }
//
//        return convertView;
//    }

//    private View.OnClickListener listener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            int position = (Integer) view.getTag();
//            if (adapterData != null) {
//                Book book = adapterData.get(position);
//                activity2.changeBookRead(book.getId());
//            }
//        }
//    };