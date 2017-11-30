package com.example.shehryarmalik.booklub;

/**
 * Created by shehryarmalik on 11/29/17.
 */

import android.util.Log;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ResponseParser {
    private static final String ns = null;
    private static final String TAG = "RESPONSE";

    public List<Entry> parse(InputStream in, String startTag) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser, startTag);
        } finally {
            in.close();
        }
    }
    private List<Entry> readFeed(XmlPullParser parser, String start_tag) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();
        parser.require(XmlPullParser.START_TAG, ns, start_tag);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Items")) {
                Log.e(TAG, "Item Found");
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }
    public static class Entry {
        public final String title;
        public final String author;
        public final String ASIN;

        private Entry(String title, String author, String asin) {
            this.title = title;
            this.author = author;
            this.ASIN = asin;
        }
    }
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Items");
        String title = null;
        String author = null;
        String asin = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Item")) {
                Log.e(TAG, "Item Found");
                parser.require(XmlPullParser.START_TAG, ns, "Item");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name2 = parser.getName();
                    if (name2.equals("ItemAttributes")) {
                        parser.require(XmlPullParser.START_TAG, ns, "ItemAttributes");
                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                continue;
                            }
                            String name3 = parser.getName();
                            if (name3.equals("Title")) {
                                title = readTitle(parser);
                            } else if (name3.equals("Author")) {
                                author = readAuthor(parser);
                            } else {
                                skip(parser);
                            }
                        }
                    } else if (name2.equals("ASIN")) {
                        asin = readText(parser);
                    } else {
                        skip(parser);
                    }
                }
            } else {
                skip(parser);
            }

        }
        return new Entry(title, author, asin);
    }
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Title");
        String title = readText(parser);
        Log.e(TAG, title);
        parser.require(XmlPullParser.END_TAG, ns, "Title");
        return title;
    }

    private String readAuthor(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Author");
        String author = readText(parser);
        Log.e(TAG, author);
        parser.require(XmlPullParser.END_TAG, ns, "Author");
        return author;
    }
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        if (tag.equals("link")) {
            link = parser.getAttributeValue(null, "url");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            Log.e(TAG, result);
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
