package com.developer.tanay.nertia.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.developer.tanay.nertia.Nertiapp;
import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.Volley.MySingleton;
import com.developer.tanay.nertia.oHome.Home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ODashboard extends AppCompatActivity {

    TextView fname, uname, phone, email, city, state, pincode;
    Button button;
    String username;
    private static final String spUsername = "logged_in_username";
    private static final String url = Nertiapp.server_url+"/getowner/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odashboard);
        set_variables();
        get_username();
        get_data();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ODashboard.this, OwnerEdit.class);
                intent.putExtra("uname", uname.getText().toString().trim());
                //intent.putExtra("email", email.getText().toString().trim());
                intent.putExtra("fname", fname.getText().toString().trim());
                intent.putExtra("phone", phone.getText().toString().trim());
                intent.putExtra("city", city.getText().toString().trim());
                intent.putExtra("state", state.getText().toString().trim());
                intent.putExtra("pincode", pincode.getText().toString().trim());
                startActivity(intent);
            }
        });
    }

    private void set_variables(){
        fname = findViewById(R.id.oedit_fname);
        uname = findViewById(R.id.oedit_uname);
        phone = findViewById(R.id.oedit_phone);
        //email = findViewById(R.id.oedit_email);
        city = findViewById(R.id.oedit_city);
        state = findViewById(R.id.oedit_state);
        pincode = findViewById(R.id.oedit_pincode);
        button = findViewById(R.id.oedit_btn);
    }

    private void get_data(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                set_data(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ODashboard.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void set_data(JSONArray response){
        if (response.length()!=0){
            for (int i=0;i<response.length();i++){
                try {
                    JSONObject object = (JSONObject)response.get(i);
                    if (object.getString("phone").equals(username)){
                        fname.setText(object.getString("fullname"));
                        uname.setText(object.getString("username"));
                        phone.setText(object.getString("phone"));
                        //email.setText(object.getString("email"));
                        city.setText(object.getString("city"));
                        state.setText(object.getString("state"));
                        pincode.setText(object.getString("pincode"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void get_username(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(spUsername, "");
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(ODashboard.this, Home.class);
        startActivity(intent);
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        ODashboard.this.finish();
    }
}
