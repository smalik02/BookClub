package com.example.shehryarmalik.booklub;

/**
 * Created by shehryarmalik on 10/22/17.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.shehryarmalik.booklub.models.Book;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class BookAdapter extends RealmBaseAdapter<Book> implements android.widget.ListAdapter{

    private BookListActivity activity;

    private static class ViewHolder {
        TextView bookName;
        TextView author;
        TextView genre;
        TextView releaseYear;
        TextView length;
        CheckBox isRead;
    }

    BookAdapter(BookListActivity activity, OrderedRealmCollection<Book> data) {
        super(data);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.book_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.bookName = (TextView) convertView.findViewById(R.id.book_name);
            viewHolder.author = (TextView) convertView.findViewById(R.id.author);
            viewHolder.genre = (TextView) convertView.findViewById(R.id.genre);
            viewHolder.releaseYear = (TextView) convertView.findViewById(R.id.release_year);
            viewHolder.length = (TextView) convertView.findViewById(R.id.length);
            viewHolder.isRead = (CheckBox) convertView.findViewById(R.id.is_read);
            viewHolder.isRead.setOnClickListener(listener);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            Book book = adapterData.get(position);
            viewHolder.bookName.setText(book.getName());
            viewHolder.isRead.setChecked(book.isRead());
            viewHolder.author.setText(book.getAuthor());
            viewHolder.genre.setText(book.getGenre());
            viewHolder.releaseYear.setText(""+book.getRelease_year());
            viewHolder.length.setText(""+book.getLength());
            viewHolder.isRead.setTag(position);
        }

        return convertView;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (Integer) view.getTag();
            if (adapterData != null) {
                Book book = adapterData.get(position);
                activity.changeBookRead(book.getId());
            }
        }
    };

}
