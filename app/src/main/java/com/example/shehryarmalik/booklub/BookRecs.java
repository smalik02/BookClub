package com.example.shehryarmalik.booklub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.Signature;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shehryarmalik.booklub.models.Book;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlPullParserException;


import static com.example.shehryarmalik.booklub.BookListActivity.getRealm;

public class BookRecs extends Fragment{


    private static final String TAG = "MyActivity";
    private Map<String, String> paramsToSign;
    private static boolean fullHTLML = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_book_recs, container,
                false);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Realm realm = getRealm(getActivity());
        RealmResults<Book> book_results = realm.where(Book.class).findAll();
        ArrayList<Book> books = new ArrayList<>(book_results);

        paramsToSign = new HashMap<String, String>();

        paramsToSign.put("AssociateTag", "booklub-20");
        paramsToSign.put("Operation", "ItemSearch");
        paramsToSign.put("SearchIndex", "Books");
        paramsToSign.put("ResponseGroup", "Small");
        paramsToSign.put("Service", "AWSECommerceService");
        paramsToSign.put("Version", "2010-11-01");

        SignedRequestHelper signature = new SignedRequestHelper();

        final ArrayList<String> xmlResponses = new ArrayList<>(books.size());
//        for (int index = 0; index < books.size(); index++) {

//            if (index < 1) {
//                fullHTLML = true;
//            }
            if (books.size() != 0) {
                String book_name = books.get(0).getName();
                //final int index_copy = index;

                paramsToSign.put("Keywords", book_name);
                String strSignature = signature.sign(paramsToSign);

                loadPage(strSignature);

                String service = "AWSECommerceService";

                String region = "us-east-1";
            }
//        }
    }

    private void loadPage(String urls) {
        new DownloadXmlTask().execute(urls);
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return getResources().getString(R.string.xml_error);
            }
        }
        @Override
        protected void onPostExecute(String result) {
            WebView myWebView = (WebView) getView().findViewById(R.id.webView);
            myWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedSslError(final WebView view, final SslErrorHandler handler, SslError error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
                    AlertDialog alertDialog = builder.create();
                    String message = "Certificate error.";
                    switch (error.getPrimaryError()) {
                        case SslError.SSL_UNTRUSTED:
                            message = "The certificate authority is not trusted.";
                            break;
                        case SslError.SSL_EXPIRED:
                            message = "The certificate has expired.";
                            break;
                        case SslError.SSL_IDMISMATCH:
                            message = "The certificate Hostname mismatch.";
                            break;
                        case SslError.SSL_NOTYETVALID:
                            message = "The certificate is not yet valid.";
                            break;
                    }
                    message += " Do you want to continue anyway?";
                    alertDialog.setTitle("SSL Certificate Error");
                    alertDialog.setMessage(message);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("CHECK", "Button ok pressed");
                            // Ignore SSL certificate errors
                            handler.proceed();
                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("CHECK", "Button cancel pressed");
                            handler.cancel();
                        }
                    });
                    alertDialog.show();
                }
            });
            String urlStr   = "http://example.com/my.jpg";
            String mimeType = "text/html";
            String encoding = null;

            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.getSettings().setDomStorageEnabled(true);
            myWebView.getSettings().setLoadsImagesAutomatically(true);
            myWebView.getSettings().setBuiltInZoomControls(true);
            WebSettings myWebViewSettings = myWebView.getSettings();
            myWebViewSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            String content =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
                            "<html><head>"+
                            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />"+
                            "<head><strong> Book Recommendations </strong> </head> <body>";

            Log.e(TAG, result);

            content += result + "</body></html>";

            myWebView.loadDataWithBaseURL(urlStr, content, mimeType, encoding, urlStr);
            //myWebView.loadData(content, "text/html", null);
            //myWebView.loadUrl("https://images-na.ssl-images-amazon.com/images/I/51YSdHMXnmL._SL75_.jpg");
        }

    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        ResponseParser responseXmlParser = new ResponseParser();
        List<ResponseParser.Entry> entries = null;
        //String url = null;
        //StringBuilder htmlString = new StringBuilder();
        String htmlToOutput;
        try {
            stream = downloadUrl(urlString);
            entries = responseXmlParser.parse(stream, "ItemSearchResponse");
            htmlToOutput = findSimilarities(entries);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return htmlToOutput;
    }

    private String findSimilarities(List<ResponseParser.Entry> entries) {

        paramsToSign = new HashMap<String, String>();

        paramsToSign.put("AssociateTag", "booklub-20");
        paramsToSign.put("Operation", "SimilarityLookup");
        paramsToSign.put("SearchIndex", "Books");
        paramsToSign.put("ResponseGroup", "Small,Images");
        paramsToSign.put("ItemId", entries.get(0).ASIN);
        paramsToSign.put("Service", "AWSECommerceService");
        paramsToSign.put("Version", "2010-11-01");
        paramsToSign.put("Keyword", entries.get(0).title);

        SignedRequestHelper signature = new SignedRequestHelper();
        String strSignature = signature.sign(paramsToSign);

        InputStream stream = null;
        ResponseParser responseXmlParser = new ResponseParser();
        List<ResponseParser.Entry> entries_similarities = null;

        StringBuilder htmlString = new StringBuilder();
        //String imageHTML = null;
        try {
            stream = downloadUrl(strSignature);
            entries_similarities = responseXmlParser.parse(stream, "SimilarityLookupResponse");
            //imageHTML = lookUpImages(entries_similarities);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.e(TAG, String.valueOf(entries_similarities.size()));
        for (ResponseParser.Entry entry : entries_similarities) {
            byte[] imageRaw = null;
            try {
                URL url = new URL(entry.url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                out.flush();

                imageRaw = out.toByteArray();

                urlConnection.disconnect();
                in.close();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String image64 = Base64.encodeToString(imageRaw, Base64.DEFAULT);

            String pageData = "<figure><img src=\"data:image/jpeg;base64," + image64 + "\" width=\"300\" height=\"300\" />";


            htmlString.append(pageData);
            htmlString.append("<figcaption> <strong> Title: </strong>");
            htmlString.append(entry.title);
            htmlString.append("<strong> ASIN: </strong>");
            htmlString.append(entry.ASIN);
            htmlString.append("</figcaption>");
            htmlString.append("</figure>");
            Log.e(TAG, entry.url);
        }

        return htmlString.toString();

    }

    private String lookUpImages(List<ResponseParser.Entry> entries_similarities) {

        paramsToSign = new HashMap<String, String>();

        paramsToSign.put("AssociateTag", "booklub-20");
        paramsToSign.put("Operation", "ItemLookup");
        paramsToSign.put("SearchIndex", "Books");
        paramsToSign.put("ResponseGroup", "Images");
        paramsToSign.put("IdType", "ASIN");
        paramsToSign.put("ItemId", entries_similarities.get(0).ASIN);
        paramsToSign.put("Service", "AWSECommerceService");
        paramsToSign.put("Version", "2010-11-01");

        SignedRequestHelper signature = new SignedRequestHelper();
        String strSignature = signature.sign(paramsToSign);

        InputStream stream = null;
        ResponseParser responseXmlParser = new ResponseParser();
        List<ResponseParser.Entry> images_similar = null;

        StringBuilder htmlString = new StringBuilder();
        try {
            stream = downloadUrl(strSignature);
            images_similar = responseXmlParser.parse(stream, "ItemLookupResponse");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (ResponseParser.Entry entry : images_similar) {
            htmlString.append(" <p>" + entry.url + "</p>");
            htmlString.append(" <p> Title: ");
            htmlString.append(entry.title);
            htmlString.append("</p>" + "<p> ASIN:"+ entry.ASIN + "</p>");
        }

        return htmlString.toString();

    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }



    private void parse_response(ArrayList<String> xmlResponses) {
        ResponseParser searchResponse = new ResponseParser();
        List<ResponseParser.Entry> entries = null;

        for (int index = 0; index < xmlResponses.size(); index++) {
            String response = xmlResponses.get(index);
            try {
                InputStream stream = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8.name()));
                try {
                    entries = searchResponse.parse(stream, "ItemSearchResponse");
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            updateBookInfo(entries);
        }



    }

    private void updateBookInfo(List<ResponseParser.Entry> entries) {

        Realm realm = getRealm(getActivity());
        RealmResults<Book> book_results = realm.where(Book.class).findAll();
        ArrayList<Book> books = new ArrayList<>(book_results);

        for (ResponseParser.Entry entry : entries) {
            Log.e(TAG, entry.ASIN);
            Log.e(TAG, entry.title);
            Log.e(TAG, entry.author);

//            if (entry.title.equals(books.get(index).getName())) {
//                books.get(index).setASIN(entry.ASIN);
//            }
        }
    }




//    byte[] signed_key = new byte[0];
//            try {
//        signed_key = getSignatureKey(SECRET_KEY, nowAsISO, region, service);
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//
//    String s_key = new String(Base64.encode(signed_key, Base64.NO_WRAP));
//            try {
//        s_key = URLEncoder.encode(s_key, "UTF-8");
//    } catch (UnsupportedEncodingException e) {
//        e.printStackTrace();
//    }
//            Log.e(TAG, s_key);


    static byte[] HmacSHA256(String data, byte[] key) throws Exception {
        String algorithm="HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
    }

    static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        return kSigning;
    }


}
