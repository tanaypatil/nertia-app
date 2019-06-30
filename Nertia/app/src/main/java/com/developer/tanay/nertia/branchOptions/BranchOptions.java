package com.developer.tanay.nertia.branchOptions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.developer.tanay.nertia.Nertiapp;
import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.Volley.MySingleton;
import com.developer.tanay.nertia.logsign.Login;
import com.developer.tanay.nertia.oHome.Home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchOptions extends AppCompatActivity implements BranchAdapter.ClickListener, View.OnClickListener {

    RecyclerView recyclerView;
    BranchAdapter adapter;
    List<BranchItem> data = new ArrayList<>();
    String sid, bid, un;
    ProgressDialog dialog;
    Button adb_btn;
    private static final String currentSalonId = "current_salon_id";
    private static final String currentBranchId = "current_branch_id";
    private static final String spUsername = "logged_in_username";
    private static final String get_url = Nertiapp.server_url+"/cownerb/";
    private static final String url = Nertiapp.server_url+"/getbrs/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_options);
        get_username();
        sid = getIntent().getExtras().getString("sid");
        Log.d("Salonid", sid);
        adb_btn = findViewById(R.id.adb_btn);
        adb_btn.setOnClickListener(this);
        recyclerView = findViewById(R.id.brencho_recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        get_data();
    }

    private void get_data(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());
                setData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BranchOptions.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void setData(JSONArray jsonArray){
        Log.d("response len", jsonArray.length()+"");
        if (jsonArray.length()!=0){
            Log.d("Dimag", "Kharab");
            try {
                Log.d("JyadaDimag", "Kharab");
                for (int i=0;i<jsonArray.length();i++){
                    Log.d("BohotJyadaDimag", "Kharab");
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    BranchItem current = new BranchItem();
                    Log.d("Current salon id", sid);
                    Log.d("SalonId", object.getString("get_salon_id"));
                    if (object.getString("get_salon_id").equals(sid)){
                        current.setBranch_id(object.getString("branch_id"));
                        current.setBranch_name(object.getString("name"));
                        data.add(current);
                    }
                }
                Log.d("DATA SIZE", data.size()+"");
                if (data.size()!=0){
                    adapter = new BranchAdapter(this, data);
                    adapter.setClickListener(this);
                    recyclerView.setAdapter(adapter);
                }
            }catch (JSONException j){
                j.printStackTrace();
            }
        }
    }

    @Override
    public void itemClicked(View view, int position, String bid) {
        dialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        dialog.setTitle("Changing Branch");
        dialog.setMessage("Please Wait...");
        dialog.show();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(currentBranchId, bid);
        editor.putString(currentSalonId, sid);
        Log.d("New Salon ID", sid);
        Log.d("New Branch ID", bid);
        editor.apply();
        save_current_branch_id(bid, un);
        Toast.makeText(BranchOptions.this, "Branch Id changed to "+bid, Toast.LENGTH_LONG).show();
    }

    private void save_current_branch_id(final String brid, final String uname){
        Log.d("USERNAME IN B", uname);
        Log.d("BRID IN B", brid);
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
                    Toast.makeText(BranchOptions.this, "Server Error", Toast.LENGTH_LONG).show();
                }
                dialog.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BranchOptions.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("bid", brid);
                params.put("un", uname);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void get_username(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        un = preferences.getString(spUsername, "");
    }

    /*@Override
    public void onBackPressed() {
        Intent i = new Intent(BranchOptions.this, Home.class);
        i.putExtra(currentSalonId, sid);
        i.putExtra(currentBranchId, bid);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/

    @Override
    public void onClick(View v) {
        Intent i = new Intent(BranchOptions.this, AddBranch.class);
        startActivity(i);
    }
}
