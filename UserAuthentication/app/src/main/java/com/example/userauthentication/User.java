package com.example.userauthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class User extends AppCompatActivity {
  // Declaration of global variables.
    String link, code, reply;

 // Default function call on creation of an activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);                                                                  // Setting the content view
    }

 // Method for deleting user data.
    public void delete(View view) {
        // Create various messages to display in the app.
        Toast failed_toast = Toast.makeText(User.this, "Request failed", Toast.LENGTH_SHORT);
        Toast delete_toast = Toast.makeText(User.this, "Account deleted", Toast.LENGTH_SHORT);
        // Create a worker thread for sending HTTP requests.
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                link = "http://192.168.1.11:8080/users/all";                                            // The private IP address of the machine is used
                try {
                    URL url = new URL (link);                                                           // new url object is created
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();                  // HTTP connection object is created
                    conn.setRequestMethod("DELETE");                                                    // DELETE method
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();                                             // Input stream object for HTTP connection is created
                    StringBuffer sb = new StringBuffer();                                               // String buffer object is created
                    // Fetch and append the incoming bytes until no more comes over the input stream.
                    try {
                        int chr;
                        while ((chr = is.read()) != -1) {
                            sb.append((char) chr);
                        }
                        reply = sb.toString();
                    } finally {
                        is.close();                                                                     // Closing the input stream
                    }
                    code = String.valueOf(conn.getResponseCode());                                      // Get the HTTP status code
                    conn.disconnect();                                                                  // Disconnecting
                    // For unreachable network or other network related failures.
                    if (!code.equals("200")) {
                        failed_toast.show();
                    }
                    // Successfully delete user data from the database.
                    else {
                        delete_toast.show();
                        Intent main = new Intent(User.this, MainActivity.class);
                        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
