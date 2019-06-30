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

public class OwnerEdit extends AppCompatActivity {

    EditText fname, phone, city, state, pincode;
    Button button;
    String oname, ouname, ophone, oemail, ocity, ostate, opincode, olduname;
    private static final String spUsername = "logged_in_username";
    private static final String otpurl = Nertiapp.server_url+"/otpgen/";
    private static final String url = Nertiapp.server_url+"/editowner/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_edit);
        set_variables();
        set_text();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFormVariables();
                String ret = chkCreds();
                if (ret.equals("all_ok")){
                    if (olduname.equals(ophone)){
                        send_data();
                    }else {
                        send_otp();
                    }
                }else {
                    Toast.makeText(OwnerEdit.this, ret, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String chkCreds(){
        String ret;
        if (oname.equals("")||ophone.equals("")||
                ocity.equals("")||ostate.equals("")||opincode.equals("")){
            ret = "Enter all Credentials.";
        }else if (oname.length()<4 || oname.length()>100){
            ret = "Length of Full Name should be between 4 and 100.";
        }/*else if (ouname.length()<4 || ouname.length()>10){
            ret = "Length of Username should be between 4 and 10";
        }*/else if (ophone.length()!=10){
            ret = "Enter a valid Phone Number.";
        }/* else if (oemail.length() > 75 || oemail.length() < 7 || !oemail.contains("@") || !oemail.contains(".")) {
            ret = "Enter a valid e-mail Id.";
        }*/else if (ocity.length()<3 || ocity.length()>20){
            ret = "Length of City should be between 3 and 20";
        }else if (ostate.length()<3 || ostate.length()>20){
            ret = "Length of State should be between 3 and 20";
        }else if (opincode.length()!=6){
            ret = "Length of Pincode should be 6.";
        }else {
            ret = "all_ok";
        }
        return ret;
    }

    private void setFormVariables(){
        oname = fname.getText().toString().trim();
        //ouname = uname.getText().toString().trim();
        ophone = phone.getText().toString().trim();
        //oemail = email.getText().toString().trim();
        ocity = city.getText().toString().trim();
        ostate = state.getText().toString().trim();
        opincode = pincode.getText().toString().trim();
    }

    private void set_text(){
        fname.setText(getIntent().getExtras().getString("fname"));
        //uname.setText(getIntent().getExtras().getString("uname"));
        phone.setText(getIntent().getExtras().getString("phone"));
        //email.setText(getIntent().getExtras().getString("email"));
        city.setText(getIntent().getExtras().getString("city"));
        state.setText(getIntent().getExtras().getString("state"));
        pincode.setText(getIntent().getExtras().getString("pincode"));
        olduname = getIntent().getExtras().getString("phone");
    }

    private void set_variables(){
        fname = findViewById(R.id.edit_oname);
        //uname = findViewById(R.id.edit_ouname);
        phone = findViewById(R.id.edit_ophone);
        //email = findViewById(R.id.edit_oemail);
        city = findViewById(R.id.edit_ocity);
        state = findViewById(R.id.edit_ostate);
        pincode = findViewById(R.id.edit_opincode);
        button = findViewById(R.id.osave_btn);
    }

    private void send_data(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String ret = response.replace("\"", "");
                if (ret.equals("success")){
                    Toast.makeText(OwnerEdit.this, "Account edited successfully.", Toast.LENGTH_SHORT).show();
                    set_new_uname();
                    Intent intent = new Intent(OwnerEdit.this, ODashboard.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(OwnerEdit.this, "Invalid Credentials or Duplicate Phone number.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OwnerEdit.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                //params.put("uname", ouname);
                params.put("fname", oname);
                params.put("phone", ophone);
                //params.put("email", oemail);
                params.put("city", ocity);
                params.put("state", ostate);
                params.put("pincode", opincode);
                params.put("olduname", olduname);
                Log.d("olduname", olduname);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void set_new_uname(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(spUsername, ophone);
        editor.apply();
    }

    private void send_otp(){
        StringRequest request = new StringRequest(Request.Method.POST, otpurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(OwnerSignup.this, response, Toast.LENGTH_LONG).show();
                response = response.replaceAll("\"", "");
                if (response.equals("exists")){
                    Toast.makeText(OwnerEdit.this, "User with the Phone Number exists.", Toast.LENGTH_LONG).show();
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
                        Intent intent = new Intent(OwnerEdit.this,OEnterOtp.class);
                        intent.putExtra("fname", oname);
                        intent.putExtra("phone", ophone);
                        intent.putExtra("city", ocity);
                        intent.putExtra("state", ostate);
                        intent.putExtra("pincode", opincode);
                        intent.putExtra("olduname", olduname);
                        intent.putExtra("sessionid", map.get("Details"));
                        startActivity(intent);
                    }else {
                        Toast.makeText(OwnerEdit.this, "OTP not sent. Try again.", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OwnerEdit.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile",ophone);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(OwnerEdit.this, ODashboard.class);
        startActivity(intent);
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
