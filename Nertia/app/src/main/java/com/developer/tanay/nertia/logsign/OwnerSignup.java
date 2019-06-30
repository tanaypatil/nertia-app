package com.developer.tanay.nertia.logsign;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.tanay.nertia.Nertiapp;
import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.Volley.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OwnerSignup extends AppCompatActivity implements View.OnClickListener {

    EditText fname, uname, phone, email, sname, city, state, pincode, pwd;
    TextView prevsid;
    Button button;
    String oname, ouname, ophone, oemail, osname, ocity, ostate, opincode, opwd;

    private static final String otpurl = Nertiapp.server_url+"/otpgen/";
    private static final String pwd_error = "Your password can't be too similar to your other personal information.\n" +
            "Your password must contain at least 8 characters.\n" +
            "Your password can't be a commonly used password.\n" +
            "Your password can't be entirely numeric.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_signup);
        setVariables();
        button.setOnClickListener(this);
        if (getIntent().hasExtra("sid")){
            prevsid.setText(getIntent().getExtras().getString("sid"));
        }else {
            String z = "Get a Salon ID.";
            prevsid.setText(z);
        }
    }

    private void setVariables(){
        fname = findViewById(R.id.oname);
        prevsid = findViewById(R.id.prevsid);
        //uname = findViewById(R.id.ouname);
        phone = findViewById(R.id.onum);
        //email = findViewById(R.id.omail);
        sname = findViewById(R.id.osalon);
        city = findViewById(R.id.ocity);
        state = findViewById(R.id.ostate);
        pincode = findViewById(R.id.opincode);
        pwd = findViewById(R.id.opwd);
        button = findViewById(R.id.osignup_btn);
    }

    private void setFormVariables(){
        oname = fname.getText().toString().trim();
        //ouname = uname.getText().toString().trim();
        ophone = phone.getText().toString().trim();
        //oemail = email.getText().toString().trim();
        osname = sname.getText().toString().trim();
        ocity = city.getText().toString().trim();
        ostate = state.getText().toString().trim();
        opincode = pincode.getText().toString().trim();
        opwd = pwd.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.osignup_btn:
                String form_response = check_form();
                if (form_response.equals("all_ok")){
                    //sendRequest();
                    send_otp();
                }else {
                    Toast.makeText(OwnerSignup.this, form_response, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void send_otp(){
        StringRequest request = new StringRequest(Request.Method.POST, otpurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(OwnerSignup.this, response, Toast.LENGTH_LONG).show();
                response = response.replaceAll("\"", "");
                if (response.equals("exists")){
                    Toast.makeText(OwnerSignup.this, "User with the Phone Number exists.", Toast.LENGTH_LONG).show();
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
                        Intent intent = new Intent(OwnerSignup.this,OEnterOtp.class);
                        intent.putExtra("fname", oname);
                        intent.putExtra("phone", ophone);
                        intent.putExtra("sname", osname);
                        intent.putExtra("city", ocity);
                        intent.putExtra("state", ostate);
                        intent.putExtra("pincode", opincode);
                        intent.putExtra("pwd", opwd);
                        intent.putExtra("sessionid", map.get("Details"));
                        startActivity(intent);
                    }else {
                        Toast.makeText(OwnerSignup.this, "OTP not sent. Try again.", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OwnerSignup.this, "Network Error.", Toast.LENGTH_SHORT).show();
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

    private String check_form(){
        String ret;
        setFormVariables();
        if (oname.equals("")||ophone.equals("")||osname.equals("")
            ||ocity.equals("")||ostate.equals("")||opincode.equals("")||opwd.equals("")){
            ret = "Enter all Credentials.";
        }else if (oname.length()<4 || oname.length()>100){
            ret = "Length of Full Name should be between 4 and 100.";
        }/*else if (ouname.length()<4 || ouname.length()>10){
            ret = "Length of Username should be between 4 and 10";
        }*/else if (ophone.length()!=10){
            ret = "Enter a valid Phone Number.";
        } /*else if (oemail.length() > 75 || oemail.length() < 7 || !oemail.contains("@") || !oemail.contains(".")) {
            ret = "Enter a valid e-mail Id.";
        }*/else if (osname.length()<4 || osname.length()>40){
            ret = "Length of Salon Name should be between 4 and 40";
        }else if (ocity.length()<3 || ocity.length()>20){
            ret = "Length of City should be between 3 and 20";
        }else if (ostate.length()<3 || ostate.length()>20){
            ret = "Length of State should be between 3 and 20";
        }else if (opincode.length()!=6){
            ret = "Length of Pincode should be 6.";
        }else if (opwd.length()<8){
            ret = pwd_error;
        }else {
            ret = "all_ok";
        }
        return ret;
    }


    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(OwnerSignup.this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/
}
