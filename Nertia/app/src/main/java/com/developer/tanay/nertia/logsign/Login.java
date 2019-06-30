package com.developer.tanay.nertia.logsign;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class Login extends AppCompatActivity implements View.OnClickListener  {

    String type;
    EditText uname, pwd, phone;
    Button button;
    String un, p;
    Spinner utype;
    String sid, bid;
    ProgressDialog dialog;
    private static final String spUsername = "logged_in_username";
    private static final String spLoggedIn = "log_in_status";
    private static final String spUserType = "log_in_user_type";
    private static final String currentSalonId = "current_salon_id";
    private static final String currentBranchId = "current_branch_id";
    private static final String url = Nertiapp.server_url+"/chklogin/";
    private static final String get_url = Nertiapp.server_url+"/cownerb/";
    private static final String idurl = Nertiapp.server_url+"/getids/";
    private static final String pwd_error = "Your password can't be too similar to your other personal information.\n" +
            "Your password must contain at least 8 characters.\n" +
            "Your password can't be a commonly used password.\n" +
            "Your password can't be entirely numeric.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        utype = findViewById(R.id.user_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        utype.setAdapter(adapter);
        Log.d("spinner text: ", utype.getSelectedItem().toString());

        TextView ssign = findViewById(R.id.ssignlabel);
        ssign.setOnClickListener(this);

        TextView osign = findViewById(R.id.osignlabel);
        osign.setOnClickListener(this);

        TextView create_salon = findViewById(R.id.create_salon_btn);
        create_salon.setOnClickListener(this);

        //uname = findViewById(R.id.uname);
        phone = findViewById(R.id.phonelogin);
        pwd = findViewById(R.id.pwd);
        button = findViewById(R.id.login_btn);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String s = "android.intent.action.";
        switch (v.getId()){
            case R.id.ssignlabel:
                //s.concat("sSignup");
                s+="sSignup";
                Intent intent = new Intent(s);
                startActivity(intent);
                break;
            case  R.id.osignlabel:
                //s.concat("oSignup");
                s+="oSignup";
                Intent intent2 = new Intent(s);
                startActivity(intent2);
                break;
            case R.id.login_btn:
                dialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
                dialog.setTitle("Logging In");
                dialog.setMessage("Please Wait...");
                dialog.show();
                //dialog = ProgressDialog.show(this, "Logging In", "Please wait...", true);
                String form_response = checkCreds();
                type = utype.getSelectedItem().toString();
                Log.d("spinner text: ", utype.getSelectedItem().toString());
                if (form_response.equals("all_ok")){
                    send_request();
                }else {
                    Toast.makeText(Login.this, form_response, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.create_salon_btn:
                Intent intent3 = new Intent(Login.this, CreateSalon.class);
                startActivity(intent3);
        }
    }

    private String checkCreds(){
        //un = uname.getText().toString().trim();
        un = phone.getText().toString().trim();
        p = pwd.getText().toString().trim();
        String ret;
        if (un.equals("")|p.equals("")){
            ret = "Enter all the credentials.";
            dialog.cancel();
        }else if (un.length()<10||un.length()>12){
            //ret = "Length of username should be between 4 and 10";
            ret = "Enter Valid Phone number";
            dialog.cancel();
        }else if (p.length()<8){
            ret = "Password should contain atleast 8 characters.";
            dialog.cancel();
        }else {
            ret = "all_ok";
        }
        return ret;
    }

    private void send_request(){

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch (response){
                    case "\"nomatch\"":
                        Toast.makeText(Login.this, "Username and Password do not match.", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                        break;
                    case "\"noentry\"":
                        Toast.makeText(Login.this, "Wrong Credentials.", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                        break;
                    default:
                        saveToPreferences();
                        dialog.cancel();
                        get_ids();
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Toast.makeText(Login.this, "Network Error.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                //params.put("uname", un);
                params.put("phone", un);
                params.put("pwd", p);
                params.put("type", type);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void saveToPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(spLoggedIn, "true");
        editor.putString(spUsername, un);
        editor.putString(spUserType, type);
        editor.apply();
    }

    private void get_ids(){
        StringRequest request = new StringRequest(Request.Method.POST, idurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.replace("\"", "");
                sid = response.split("_")[0];
                bid = response.split("_")[1];
                save_ids();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("phone", un);
                params.put("type", type);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog_Alert));
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void save_ids(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(currentSalonId, sid);
        editor.putString(currentBranchId, bid);
        Log.d("SALONID", sid);
        Log.d("BRANCHID", bid);
        editor.apply();
        Intent intent = new Intent("android.intent.action.Home");
        intent.putExtra("sid", sid);
        intent.putExtra("bid", bid);
        //dialog.cancel();
        startActivity(intent);
        finish();
        //send_ids(bid, un);
    }

    /*private void send_ids(final String brid, final String unames){
        StringRequest request = new StringRequest(Request.Method.POST, get_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = response.replace("\"", "");
                if (res.equals("success")){
                    Intent intent = new Intent("android.intent.action.Home");
                    intent.putExtra("sid", sid);
                    intent.putExtra("bid", bid);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(Login.this, "Server Error", Toast.LENGTH_LONG).show();
                }
                dialog.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "Network Error", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("bid", brid);
                params.put("un", unames);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }*/

}
