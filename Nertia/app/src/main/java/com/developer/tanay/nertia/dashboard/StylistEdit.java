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
import com.developer.tanay.nertia.logsign.OwnerSignup;

import java.util.HashMap;
import java.util.Map;

public class StylistEdit extends AppCompatActivity {

    EditText fname, uname, phone, email, city, state, pincode, exp, expt;
    Button button;
    String sname, suname, sphone, semail, scity, sstate, spincode, sexp, sexpt, olduname;
    private static final String url = Nertiapp.server_url+"/editsty/";
    private static final String otpurl = Nertiapp.server_url+"/otpgen/";
    private static final String spUsername = "logged_in_username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylist_edit);
        set_variables();
        set_text();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFormVariables();
                String s = chkCreds();
                if (s.equals("all_ok")){
                    //Log.d("data", sname+sphone+scity+sstate+spincode+sexp+sexpt+olduname);
                    if (olduname.equals(sphone)){
                        send_data();
                    }else {
                        send_otp();
                    }
                }else {
                    Toast.makeText(StylistEdit.this, s, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setFormVariables(){
        sname = fname.getText().toString().trim();
        //suname = uname.getText().toString().trim();
        sphone = phone.getText().toString().trim();
        //semail = email.getText().toString().trim();
        scity = city.getText().toString().trim();
        sstate = state.getText().toString().trim();
        spincode = pincode.getText().toString().trim();
        sexp = exp.getText().toString().trim();
        sexpt = expt.getText().toString().trim();
    }

    private void set_variables(){
        fname = findViewById(R.id.edit_fname);
        //uname = findViewById(R.id.edit_uname);
        phone = findViewById(R.id.edit_phone);
        //email = findViewById(R.id.edit_email);
        city = findViewById(R.id.edit_city);
        state = findViewById(R.id.edit_state);
        pincode = findViewById(R.id.edit_pincode);
        exp = findViewById(R.id.edit_exp);
        expt = findViewById(R.id.edit_expt);
        button = findViewById(R.id.save_button);
    }

    private void set_text(){
        fname.setText(getIntent().getExtras().getString("fname"));
        //uname.setText(getIntent().getExtras().getString("uname"));
        phone.setText(getIntent().getExtras().getString("phone"));
        //email.setText(getIntent().getExtras().getString("email"));
        city.setText(getIntent().getExtras().getString("city"));
        state.setText(getIntent().getExtras().getString("state"));
        pincode.setText(getIntent().getExtras().getString("pincode"));
        exp.setText(getIntent().getExtras().getString("exp"));
        expt.setText(getIntent().getExtras().getString("expt"));
        olduname = getIntent().getExtras().getString("phone");
    }

    private String chkCreds(){
        String ret;
        //setFormVariables();
        if (sname.equals("")||sphone.equals("")||
                scity.equals("")||sstate.equals("")||spincode.equals("")||sexp.equals("")||sexpt.equals("")){
            ret = "Enter all Credentials.";
        }else if (sname.length()<4 || sname.length()>100){
            ret = "Length of Full Name should be between 4 and 100.";
        }/*else if (suname.length()<4 || suname.length()>10){
            ret = "Length of Username should be between 4 and 10";
        }*/else if (sphone.length()!=10){
            ret = "Enter a valid Phone Number.";
        }/* else if (semail.length() > 75 || semail.length() < 7 || !semail.contains("@") || !semail.contains(".")) {
            ret = "Enter a valid e-mail Id.";
        }*/else if (scity.length()<3 || scity.length()>20){
            ret = "Length of City should be between 3 and 20";
        }else if (sstate.length()<3 || sstate.length()>20){
            ret = "Length of State should be between 3 and 20";
        }else if (spincode.length()!=6){
            ret = "Length of Pincode should be 6.";
        }else if (sexpt.length()>200){
            ret = "Expertise can have maximum 200 characters.";
        }else {
            ret = "all_ok";
        }
        return ret;
    }

    private void send_data(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String ret = response.replace("\"", "");
                if (ret.equals("success")){
                    Toast.makeText(StylistEdit.this, "Account edited successfully.", Toast.LENGTH_SHORT).show();
                    set_new_uname();
                    Intent intent = new Intent(StylistEdit.this, Dashboard.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(StylistEdit.this, "Invalid Credentials or Duplicate Phone Number.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StylistEdit.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                //params.put("uname", suname);
                params.put("fname", sname);
                params.put("phone", sphone);
                //params.put("email", semail);
                params.put("city", scity);
                params.put("state", sstate);
                params.put("pincode", spincode);
                params.put("exp", sexp);
                params.put("expt", sexpt);
                params.put("olduname", olduname);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void set_new_uname(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(spUsername, sphone);
        editor.apply();
    }

    private void send_otp(){
        StringRequest request = new StringRequest(Request.Method.POST, otpurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(OwnerSignup.this, response, Toast.LENGTH_LONG).show();
                response = response.replaceAll("\"", "");
                if (response.equals("exists")){
                    Toast.makeText(StylistEdit.this, "User with the Phone Number exists.", Toast.LENGTH_LONG).show();
                }else {
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
                        Intent intent = new Intent(StylistEdit.this,SeditOtp.class);
                        intent.putExtra("fname", sname);
                        intent.putExtra("phone", sphone);
                        intent.putExtra("city", scity);
                        intent.putExtra("state", sstate);
                        intent.putExtra("pincode", spincode);
                        intent.putExtra("olduname", olduname);
                        intent.putExtra("exp", sexp);
                        intent.putExtra("expt", sexpt);
                        intent.putExtra("sessionid", map.get("Details"));
                        startActivity(intent);
                    }else {
                        Toast.makeText(StylistEdit.this, "OTP not sent. Try again.", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StylistEdit.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile",sphone);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(StylistEdit.this, Dashboard.class);
        startActivity(intent);
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
