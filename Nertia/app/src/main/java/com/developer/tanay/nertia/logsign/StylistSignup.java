package com.developer.tanay.nertia.logsign;

import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class StylistSignup extends AppCompatActivity implements View.OnClickListener {

    EditText fname, uname, phone, email, branchid, city, state, pincode, experience, expertise, pwd;
    Button button;
    String sname, suname, sphone, semail, ssid, scity, sstate, spincode, sexp, sexpt, spwd;
    private static final String otpurl = Nertiapp.server_url+"/otpgen/";
    private static final String pwd_error = "Your password can't be too similar to your other personal information.\n" +
            "Your password must contain at least 8 characters.\n" +
            "Your password can't be a commonly used password.\n" +
            "Your password can't be entirely numeric.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stylist_signup);
        setVariables();
        button.setOnClickListener(this);
    }

    private void setVariables(){
        fname = findViewById(R.id.sfname);
        //uname = findViewById(R.id.sname);
        phone = findViewById(R.id.snum);
        //email = findViewById(R.id.semail);
        branchid = findViewById(R.id.salonid);
        city = findViewById(R.id.scity);
        state = findViewById(R.id.sstate);
        pincode = findViewById(R.id.spincode);
        experience = findViewById(R.id.sexp);
        expertise = findViewById(R.id.sexpt);
        pwd = findViewById(R.id.spwd);
        button = findViewById(R.id.ssignup_btn);
    }

    private void setFormVariables(){
        sname = fname.getText().toString().trim();
        //suname = uname.getText().toString().trim();
        sphone = phone.getText().toString().trim();
        //semail = email.getText().toString().trim();
        ssid = branchid.getText().toString().trim();
        scity = city.getText().toString().trim();
        sstate = state.getText().toString().trim();
        spincode = pincode.getText().toString().trim();
        sexp = experience.getText().toString().trim();
        sexpt = expertise.getText().toString().trim();
        spwd = pwd.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        String form_response = check_form();
        switch (v.getId()){
            case R.id.ssignup_btn:
                if (form_response.equals("all_ok")){
                    //sendRequest();
                    get_otp();
                }else {
                    Toast.makeText(StylistSignup.this, form_response, Toast.LENGTH_LONG).show();
                }
        }
    }


    private void get_otp(){
        StringRequest request = new StringRequest(Request.Method.POST, otpurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.replaceAll("\"", "");
                if (response.equals("exists")){
                    Toast.makeText(StylistSignup.this, "User with the Phone Number exists.", Toast.LENGTH_LONG).show();
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
                        Intent intent = new Intent(StylistSignup.this,SEnterOtp.class);
                        intent.putExtra("fname", sname);
                        intent.putExtra("phone", sphone);
                        intent.putExtra("ssid", ssid);
                        intent.putExtra("city", scity);
                        intent.putExtra("state", sstate);
                        intent.putExtra("pincode", spincode);
                        intent.putExtra("exp", sexp);
                        intent.putExtra("expt", sexpt);
                        intent.putExtra("pwd", spwd);
                        intent.putExtra("sessionid", map.get("Details"));
                        startActivity(intent);
                    }else {
                        Toast.makeText(StylistSignup.this, "OTP not sent. Try again.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StylistSignup.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile", sphone);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private String check_form(){
        String ret;
        setFormVariables();
        if (sname.equals("")||sphone.equals("")||ssid.equals("")
            ||scity.equals("")||sstate.equals("")||spincode.equals("")||sexp.equals("")||sexpt.equals("")
            ||spwd.equals("")){
            ret = "Enter all Credentials.";
        }else if (sname.length()<4 || sname.length()>100){
            ret = "Length of Full Name should be between 4 and 100.";
        }/*else if (suname.length()<4 || suname.length()>10){
            ret = "Length of Username should be between 4 and 10";
        }*/else if (sphone.length()!=10){
            ret = "Enter a valid Phone Number.";
        } /*else if (semail.length() > 75 || semail.length() < 7 || !semail.contains("@") || !semail.contains(".")) {
            ret = "Enter a valid e-mail Id.";
        }*/else if (ssid.length()<9||ssid.length()>20){
            ret = "Length of Salon Id should be between 9 and 20";
        }else if (scity.length()<3 || scity.length()>20){
            ret = "Length of City should be between 3 and 20";
        }else if (sstate.length()<3 || sstate.length()>20){
            ret = "Length of State should be between 3 and 20";
        }else if (spincode.length()!=6){
            ret = "Length of Pincode should be 6.";
        }else if (sexpt.length()>200){
            ret = "Expertise can have maximum 200 characters.";
        }else if (spwd.length()<8){
            ret = pwd_error;
        }else {
            ret = "all_ok";
        }
        return ret;
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(StylistSignup.this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/
}
