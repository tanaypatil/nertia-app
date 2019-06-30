package com.developer.tanay.nertia;

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
import com.developer.tanay.nertia.Volley.MySingleton;
import com.developer.tanay.nertia.oHome.Home;

import java.util.HashMap;
import java.util.Map;

public class Query extends AppCompatActivity implements View.OnClickListener {

    EditText qtext;
    Button submit_btn;
    private static final String url = Nertiapp.server_url+"/query/";
    private static final String spUserType = "log_in_user_type";
    private static final String spUsername = "logged_in_username";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        setVariables();
        submit_btn.setOnClickListener(this);
    }

    private void setVariables(){
        qtext = findViewById(R.id.qtext);
        submit_btn = findViewById(R.id.qsubmit_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qsubmit_btn:
                dialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
                dialog.setTitle("Logging In");
                dialog.setMessage("Please Wait...");
                dialog.show();
                sendRequest();
                break;
        }
    }

    private String[] getUserType(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] ret = new String[2];
        ret[0] = preferences.getString(spUserType, "");
        ret[1] = preferences.getString(spUsername, "");
        return ret;
    }

    private void sendRequest(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch (response){
                    case "\"success\"":
                        dialog.cancel();
                        Toast.makeText(Query.this, "Query submitted successfully.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent("android.intent.action.Home");
                        startActivity(intent);
                    default:
                        Toast.makeText(Query.this, "Some error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Toast.makeText(Query.this, "Network Error.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("query", qtext.getText().toString().trim());
                String[] s;
                s = getUserType();
                params.put("user_type", s[0]);
                params.put("username", s[1]);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(Query.this, Home.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Query.this.finish();
    }*/
}
