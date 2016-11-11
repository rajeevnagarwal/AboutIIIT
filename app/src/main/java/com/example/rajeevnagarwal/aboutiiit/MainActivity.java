package com.example.rajeevnagarwal.aboutiiit;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView mAboutTextView;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("data",mAboutTextView.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        if(savedInstanceState!=null)
        {
            String data = savedInstanceState.getString("data");
            set(data);
        }
        else {
            ConnectivityManager mng = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = mng.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new AsyncFetch().execute();
            } else {
                Toast.makeText(this, "No network", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void initialize()
    {
        mAboutTextView = (TextView)findViewById(R.id.about);
        mAboutTextView.setMovementMethod(new ScrollingMovementMethod());

    }
    private void set(String text)
    {
        mAboutTextView.setText(text);
    }

    private class AsyncFetch extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides or your JSON file address
                url = new URL("https://www.iiitd.ac.in/about");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we receive data
                conn.setDoOutput(true);
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String result = new String();
                    String line="";

                    while ((line = reader.readLine()) != null) {
                        //System.out.println(line);
                        result = result + "\n"+line;
                    }

                    // Pass data to onPostExecute method
                    return (result);

                } else {
                    return("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            //System.out.println(result.length());
            set(result);

        }


    }

}
