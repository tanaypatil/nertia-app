package com.developer.tanay.nertia.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.developer.tanay.nertia.Nertiapp;
import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.Volley.MySingleton;
import com.developer.tanay.nertia.logsign.OEnterOtp;

import java.util.HashMap;
import java.util.Map;

public class OeditOtp extends AppCompatActivity implements View.OnClickListener {

    EditText otp;
    String fname, phone, city, state, pincode, session_id, olduname;
    Button button, chbutton;
    private static final String otpurl = Nertiapp.server_url+"/otpchk/";
    private static final String url = Nertiapp.server_url+"/editowner/";
    private static final String spUsername = "logged_in_username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oedit_otp);
        otp = findViewById(R.id.oedit_otp_txt);
        button = findViewById(R.id.oedit_cont_btn);
        chbutton = findViewById(R.id.sedit_otp_chk_btn);
        button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(this);
        chbutton.setOnClickListener(this);

        fname = getIntent().getExtras().getString("fname");
        phone = getIntent().getExtras().getString("phone");
        city = getIntent().getExtras().getString("city");
        state = getIntent().getExtras().getString("state");
        pincode = getIntent().getExtras().getString("pincode");
        olduname = getIntent().getExtras().getString("olduname");
        session_id = getIntent().getExtras().getString("sessionid");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.oedit_chk_btn:
                chk_otp();
                break;
            case R.id.oedit_cont_btn:
                send_data();
                break;
        }
    }


    private void chk_otp(){
        StringRequest request = new StringRequest(Request.Method.POST, otpurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(OEnterOtp.this, response, Toast.LENGTH_LONG).show();
                response = response.replaceAll("\"", "");
                response = response.substring(1, response.length()-1);
                Log.d("response", response);
                String[] pairs = response.split(",");
                HashMap<String,String> map = new HashMap<>();

                for(String pair : pairs) {
                    String[] entry = pair.split(":");
                    String k = entry[0].replaceAll("\'", "");
                    Log.d("key", k);
                    String p = entry[1].replaceAll("\'", "");
                    Log.d("pair", p);
                    map.put(k.trim(), p.trim());
                }

                if (map.get("Status").equals("Success")){
                    Toast.makeText(OeditOtp.this, "OTP Matched.", Toast.LENGTH_LONG).show();
                    chbutton.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.VISIBLE);
                    otp.setEnabled(false);
                }else {
                    Toast.makeText(OeditOtp.this, "OTP Mismatch.", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OeditOtp.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("code", otp.getText().toString().trim());
                params.put("session_id", getIntent().getExtras().getString("sessionid"));
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void send_data(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String ret = response.replace("\"", "");
                if (ret.equals("success")){
                    Toast.makeText(OeditOtp.this, "Account edited successfully.", Toast.LENGTH_SHORT).show();
                    set_new_uname();
                    Intent intent = new Intent(OeditOtp.this, ODashboard.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(OeditOtp.this, "Invalid Credentials or Duplicate Phone number.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OeditOtp.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                //params.put("uname", ouname);
                params.put("fname", fname);
                params.put("phone", phone);
                //params.put("email", oemail);
                params.put("city", city);
                params.put("state", state);
                params.put("pincode", pincode);
                params.put("olduname", olduname);
                //Log.d("olduname", olduname);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void set_new_uname(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(spUsername, phone);
        editor.apply();
    }

}
