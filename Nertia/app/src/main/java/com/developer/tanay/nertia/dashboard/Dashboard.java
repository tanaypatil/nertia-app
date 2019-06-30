package com.developer.tanay.nertia.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class Dashboard extends AppCompatActivity {

    TextView fname, uname, phone, email, city, state, pincode, exp, expt;
    Button button;
    String username;
    private static final String spUsername = "logged_in_username";
    private static final String url = Nertiapp.server_url+"/getstys/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        set_variables();
        //get_username();
        get_data();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, StylistEdit.class);
                //intent.putExtra("uname", uname.getText().toString().trim());
                //intent.putExtra("email", email.getText().toString().trim());
                intent.putExtra("fname", fname.getText().toString().trim());
                intent.putExtra("phone", phone.getText().toString().trim());
                intent.putExtra("city", city.getText().toString().trim());
                intent.putExtra("state", state.getText().toString().trim());
                intent.putExtra("pincode", pincode.getText().toString().trim());
                intent.putExtra("exp", exp.getText().toString().trim());
                intent.putExtra("expt", expt.getText().toString().trim());
                startActivity(intent);
            }
        });
    }

    private void set_variables(){
        fname = findViewById(R.id.fname_dis);
        //uname = findViewById(R.id.uname_dis);
        phone = findViewById(R.id.phone_dis);
        //email = findViewById(R.id.email_dis);
        city = findViewById(R.id.city_dis);
        state = findViewById(R.id.state_dis);
        pincode = findViewById(R.id.pincode_dis);
        exp = findViewById(R.id.exp_dis);
        expt = findViewById(R.id.expt_dis);
        button = findViewById(R.id.edit_btn);
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
                Toast.makeText(Dashboard.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void set_data(JSONArray response){
        if (response.length()!=0){
            Log.d("resp len", response.length()+"");
            for (int i=0;i<response.length();i++){
                try {
                    JSONObject object = (JSONObject)response.get(i);
                    //Log.d("yaha", "ha yaha");
                    String ph = object.getString("phone");
                    Log.d("PHONE", "djbud");
                    phone.setText(object.getString("phone"));
                    if (object.getString("phone").equals(get_username())){
                        fname.setText(object.getString("fullname"));
                        //uname.setText(object.getString("username"));
                        //phone.setText(object.getString("phone"));
                        //email.setText(object.getString("email"));
                        city.setText(object.getString("city"));
                        state.setText(object.getString("state"));
                        pincode.setText(object.getString("pincode"));
                        exp.setText(object.getString("experience"));
                        expt.setText(object.getString("expertise"));
                    }
                }catch (JSONException j){
                    Log.d("Empty", "YES");
                    j.printStackTrace();
                }
            }
        }
    }

    private String get_username(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(spUsername, "");
        Log.d("Username", username);
        phone.setText(username);
        return preferences.getString(spUsername, "");
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(Dashboard.this, Home.class);
        startActivity(intent);
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        Dashboard.this.finish();
    }
}
