package com.developer.tanay.nertia.logsign;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class CreateSalon extends AppCompatActivity implements View.OnClickListener {

    EditText sname, bname, baddr, bphone, bcity, bstate, bpincode;
    Button button;
    String salname, brname, addr, phone, city, state, pincode;
    ProgressDialog dialog;
    AlertDialog alertDialog;
    private static final String url = Nertiapp.server_url+"/createSalon/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_salon);
        init_variables();
        button.setOnClickListener(this);
        getSupportActionBar().setElevation(0);
    }

    private void init_variables(){
        sname = findViewById(R.id.form_sname);
        bname = findViewById(R.id.bname);
        bphone = findViewById(R.id.bnum);
        baddr = findViewById(R.id.baddr);
        bcity = findViewById(R.id.bcity);
        bstate = findViewById(R.id.bstate);
        bpincode = findViewById(R.id.bpincode);
        button = findViewById(R.id.bsubmit_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bsubmit_btn:
                dialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
                dialog.setTitle("Creating Salon");
                dialog.setMessage("Please Wait...");
                dialog.show();
                getForm();
                String form_res = chkForm();
                if (form_res.equals("all_ok")){
                    send_request();
                }else {
                    dialog.cancel();
                    Toast.makeText(CreateSalon.this, form_res, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void getForm(){
        salname = sname.getText().toString().trim();
        brname = bname.getText().toString().trim();
        addr = baddr.getText().toString().trim();
        phone = bphone.getText().toString().trim();
        city = bcity.getText().toString().trim();
        state = bstate.getText().toString().trim();
        pincode = bpincode.getText().toString().trim();
    }

    private String chkForm(){
        String ret;
        if (salname.equals("")||brname.equals("")||addr.equals("")||phone.equals("")||city.equals("")||state.equals("")||pincode.equals("")){
            ret = "Enter all the credentials.";
        }else if (salname.length()<4||salname.length()>40){
            ret = "Length of Salon Name should be between 4 and 40";
        }else if (brname.length()<4||brname.length()>40){
            ret = "Length of Branch Name should be between 4 and 40";
        }else if (addr.length()<8||addr.length()>150){
            ret = "Length of Address should be between 8and 150";
        }else if (phone.length()<10||phone.length()>13){
            ret = "Length of Phone Number should be between 10 and 13.";
        }else if (city.length()<3 || city.length()>20){
            ret = "Length of City should be between 3 and 20";
        }else if (state.length()<3 || state.length()>20){
            ret = "Length of State should be between 3 and 20";
        }else if (pincode.length()!=6){
            ret = "Length of Pincode should be 6.";
        }else {
            ret = "all_ok";
        }
        return ret;
    }

    private void send_request(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                dialog.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateSalon.this);
                builder.setTitle("Important Credentials, Take Note");
                builder.setMessage(response);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(CreateSalon.this, OwnerSignup.class);
                        i.putExtra("sid", response);
                        startActivity(i);
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateSalon.this, "Network Error", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("sname", salname);
                params.put("bname", brname);
                params.put("baddr", addr);
                params.put("bphone", phone);
                params.put("bcity", city);
                params.put("bstate", state);
                params.put("bpincode", pincode);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateSalon.this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/
}
