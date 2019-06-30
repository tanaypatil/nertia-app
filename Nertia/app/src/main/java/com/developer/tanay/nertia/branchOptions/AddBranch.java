package com.developer.tanay.nertia.branchOptions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import com.developer.tanay.nertia.logsign.CreateSalon;

import java.util.HashMap;
import java.util.Map;

public class AddBranch extends AppCompatActivity implements View.OnClickListener {

    EditText name, phone, addr, city, state, pincode;
    String bname, bphone, baddr, bcity, bstate, bpincode;
    String salid;
    Button button;
    String url = Nertiapp.server_url+"/addbranch/";
    ProgressDialog dialog;
    private static final String currentSalonId = "current_salon_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_branch);
        salid = getCurrentSalonId();
        setVariables();
        button.setOnClickListener(this);
    }

    public void setVariables(){
        name = findViewById(R.id.nbname);
        phone = findViewById(R.id.nbphone);
        addr = findViewById(R.id.nbaddr);
        city = findViewById(R.id.nbcity);
        state = findViewById(R.id.nbstate);
        pincode = findViewById(R.id.nbpincode);
        button = findViewById(R.id.nbadd_btn);
    }

    public void setForm(){
        bname = name.getText().toString().trim();
        bphone = phone.getText().toString().trim();
        baddr = addr.getText().toString().trim();
        bcity = city.getText().toString().trim();
        bstate = state.getText().toString().trim();
        bpincode = pincode.getText().toString().trim();
    }

    public String chkCreds(){
        String ret;
        if (bname.equals("")||baddr.equals("")||bphone.equals("")||bcity.equals("")||bstate.equals("")||bpincode.equals("")){
            ret = "Enter all the credentials.";
        }else if (bname.length()<4||bname.length()>40){
            ret = "Length of Branch Name should be between 4 and 40";
        }else if (baddr.length()<8||baddr.length()>150){
            ret = "Length of Address should be between 8and 150";
        }else if (bphone.length()<10||bphone.length()>13){
            ret = "Length of Phone Number should be between 10 and 13.";
        }else if (bcity.length()<3 || bcity.length()>20){
            ret = "Length of City should be between 3 and 20";
        }else if (bstate.length()<3 || bstate.length()>20){
            ret = "Length of State should be between 3 and 20";
        }else if (bpincode.length()!=6){
            ret = "Length of Pincode should be 6.";
        }else {
            ret = "all_ok";
        }
        return ret;
    }

    public String getCurrentSalonId(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(currentSalonId, "");
    }

    @Override
    public void onClick(View v) {
        dialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        dialog.setTitle("Creating Salon");
        dialog.setMessage("Please Wait...");
        dialog.show();
        setForm();
        String form_res = chkCreds();
        if (form_res.equals("all_ok")){
            send_request();
        }else {
            dialog.cancel();
            Toast.makeText(AddBranch.this, form_res, Toast.LENGTH_LONG).show();
        }
    }

    public void send_request(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = response.replaceAll("\"", "");
                if (res.equals("success")){
                    Toast.makeText(AddBranch.this, "Branch Added Successfully.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AddBranch.this, BranchOptions.class);
                    i.putExtra("sid", salid);
                    startActivity(i);
                }else {
                    Toast.makeText(AddBranch.this, "Enter a different Phone Number.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddBranch.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("bname", bname);
                params.put("bphone", bphone);
                params.put("baddr", baddr);
                params.put("bcity", bcity);
                params.put("bstate", bstate);
                params.put("bpincode", bpincode);
                params.put("salid", salid);
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
        Intent intent = new Intent(AddBranch.this, BranchOptions.class);
        intent.putExtra("sid", salid);
        startActivity(intent);
    }*/
}
