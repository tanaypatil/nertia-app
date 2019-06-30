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

public class OEnterOtp extends AppCompatActivity implements View.OnClickListener {

    EditText otp;
    Button button, chbutton;
    String fname, phone, sname, city, state, pincode, pwd;
    private static final String otpurl = Nertiapp.server_url+"/otpchk/";
    private static final String url = Nertiapp.server_url+"/osignup/";
    private static final String pwd_error = "Your password can't be too similar to your other personal information.\n" +
            "Your password must contain at least 8 characters.\n" +
            "Your password can't be a commonly used password.\n" +
            "Your password can't be entirely numeric.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oenter_otp);
        button = findViewById(R.id.ocont_btn);
        chbutton = findViewById(R.id.chk_otp_btn);
        otp = findViewById(R.id.otp_text);
        button.setVisibility(View.INVISIBLE);
        chbutton.setOnClickListener(this);
        button.setOnClickListener(this);

        fname = getIntent().getExtras().getString("fname");
        phone = getIntent().getExtras().getString("phone");
        city = getIntent().getExtras().getString("city");
        state = getIntent().getExtras().getString("state");
        pincode = getIntent().getExtras().getString("pincode");
        pwd = getIntent().getExtras().getString("pwd");
        sname = getIntent().getExtras().getString("sname");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chk_otp_btn:
                chk_otp();
                break;
            case R.id.ocont_btn:
                sendRequest();
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
                    Toast.makeText(OEnterOtp.this, "OTP Matched.", Toast.LENGTH_LONG).show();
                    chbutton.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.VISIBLE);
                    otp.setEnabled(false);
                }else {
                    Toast.makeText(OEnterOtp.this, "OTP Mismatch.", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OEnterOtp.this, "Network Error.", Toast.LENGTH_SHORT).show();
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

    private void sendRequest(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch (response) {
                    case "\"error\"":
                        Toast.makeText(OEnterOtp.this, "Error Occurred. Try Again.", Toast.LENGTH_LONG).show();
                        break;
                    case "\"exists\"":
                        Toast.makeText(OEnterOtp.this, "Account with the given credentials already exists", Toast.LENGTH_LONG).show();
                        break;
                    case "\"uexists\"":
                        Toast.makeText(OEnterOtp.this, "Username is already taken. Enter a different username.", Toast.LENGTH_LONG).show();
                        break;
                    case "\"nosalon\"":
                        Toast.makeText(OEnterOtp.this, "Enter Correct Salon Id. Salon with the given Id does not exist", Toast.LENGTH_LONG).show();
                        break;
                    case "\"password_error\"":
                        Toast.makeText(OEnterOtp.this, pwd_error, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(OEnterOtp.this, "Owner/Manager Account Created Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent("android.intent.action.Login");
                        startActivity(intent);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OEnterOtp.this, "Network Error."+error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fname", fname);
                params.put("phone", phone);
                params.put("sname", sname);
                params.put("city", city);
                params.put("state", state);
                params.put("pincode", pincode);
                params.put("password", pwd);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OEnterOtp.this, OwnerSignup.class);
        startActivity(intent);
    }*/
}
