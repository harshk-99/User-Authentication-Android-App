package com.example.userauthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Register extends AppCompatActivity {
 // Declaration of global variables.
    EditText name, pass, email;
    String name1, pass1, email1, link, reply, code;
    ImageView eye;
    boolean state = false;

 // Default function call on creation of an activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);                                                              // Setting the content view
        // Instantiate the View elements
        name = findViewById(R.id.user2);
        pass = findViewById(R.id.pass2);
        email = findViewById(R.id.email2);
        eye = findViewById(R.id.toggle_view2);
        name1 = "";
        pass1 = "";
        email1 = "";
    }

 // Method for registration.
    public void register(View view) {
        // Get the inputs from the user.
        name1 = name.getText().toString();
        pass1 = pass.getText().toString();
        email1 = email.getText().toString();

        if(name1.isEmpty() || pass1.isEmpty() || email1.isEmpty()) {
            Toast.makeText(Register.this, "Fields cannot be blank", Toast.LENGTH_SHORT).show();  // Check whether the fields are not blank
        }
        else {
            // Create various messages to display in the app.
            Toast failed_toast = Toast.makeText(Register.this, "Request failed", Toast.LENGTH_SHORT);
            Toast exists_toast = Toast.makeText(Register.this, "User Already Exists", Toast.LENGTH_SHORT);
            Toast created_toast = Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT);
            // Create a worker thread for sending HTTP requests.
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    link = "http://192.168.1.11:8080/users/register";                                   // The private IP address of the machine is used
                    try {
                        URL url = new URL (link);                                                       // new url object is created
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();              // HTTP connection object is created
                        conn.setRequestMethod("POST");                                                  // POST method
                        conn.setRequestProperty("Content-Type", "application/json; utf-8");             // JSON format is specified
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        JSONObject input = new JSONObject();                                            // New JSON object is created
                        // Give data to the json object
                        input.put("username", name1);
                        input.put("password", pass1);
                        input.put("email", email1);
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());             // Output stream object for HTTP connection is created
                        os.writeBytes(input.toString());                                                // JSON object is serialized and sent over the HTTP connection to the listening server
                        os.flush();                                                                     // Flushing the output buffers
                        os.close();                                                                     // Closing the output stream
                        InputStream is = conn.getInputStream();                                         // Input stream object for HTTP connection is created
                        StringBuffer sb = new StringBuffer();                                           // String buffer object is created
                        // Fetch and append the incoming bytes until no more comes over the input stream.
                        try {
                            int chr;
                            while ((chr = is.read()) != -1) {
                                sb.append((char) chr);
                            }
                            reply = sb.toString();
                        } finally {
                            is.close();                                                                 // Closing the input stream
                        }
                        code = String.valueOf(conn.getResponseCode());                                  // Get the HTTP status code
                        conn.disconnect();                                                              // Disconnecting
                        // For unreachable network or other network related failures.
                        if (!code.equals("200")) {
                            failed_toast.show();
                        }
                        else {
                            // Generate an error message when the user data already exists in the database.
                            if (reply.equals("\"USER_ALREADY_EXISTS\"")) {
                                exists_toast.show();
                            }
                            // Successfully registration of user data in the database.
                            else {
                                created_toast.show();
                                Intent user = new Intent(Register.this, User.class);
                                user.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(user);
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

 // For toggling visibility of password.
    public void toggle(View v){
        if(!state){
            pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            pass.setSelection(pass.getText().length());
            eye.setImageResource(R.drawable.eye);
        }
        else{
            pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            pass.setSelection(pass.getText().length());
            eye.setImageResource(R.drawable.eye_off);
        }
        state = !state;
    }
}
