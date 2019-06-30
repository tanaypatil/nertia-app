package com.developer.tanay.nertia.oHome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesDetail extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AccAdapter.GetTotalFunc {

    TextView uname, fname, smonthcost, sdtodtotal;
    String un, fn;
    RecyclerView recyclerView;
    Acc2Adapter adapter;
    SwipeRefreshLayout refreshLayout;
    List<AccItem> data = new ArrayList<>();
    private static final String currentBranchId = "current_branch_id";
    private static final String get_url = Nertiapp.server_url+"/sdetail/";
    private static final String mtotal_url = Nertiapp.server_url+"/smonthsales/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales_detail);
        refreshLayout = findViewById(R.id.sdreflayout);
        refreshLayout.setOnRefreshListener(this);
        uname = findViewById(R.id.sitem_up_uname);
        fname = findViewById(R.id.sitem_up_fname);
        smonthcost = findViewById(R.id.sdmnth_total);
        sdtodtotal = findViewById(R.id.sdtod_total);
        un = getIntent().getExtras().getString("uname");
        fn = getIntent().getExtras().getString("fname");
        uname.setText(un);
        fname.setText(fn);
        get_data();
        getMonthTotal();
    }

    private void get_data(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray array=null;
                try {
                    array = new JSONArray(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                set_data(array);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SalesDetail.this, "Network Error.", Toast.LENGTH_SHORT).show();
                if (refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("uname", un);
                params.put("bid", get_current_branch_id());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(stringRequest);
    }

    private void set_data(JSONArray jsonArray){
        if (jsonArray.length()!=0){
            try {
                data = new ArrayList<>();
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject object = (JSONObject)jsonArray.get(i);
                    AccItem current = new AccItem();
                    current.setSername(object.getString("service_name"));
                    Log.d("sname", object.getString("service_name"));
                    current.setScost(object.getString("get_str_cost"));
                    Log.d("scost", object.getString("get_str_cost"));
                    current.setTime(object.getString("get_str_time"));
                    data.add(current);
                }
                Log.d("data size", data.size()+"");
                Log.d("data item", data.get(0).getSername());
                setadapter();
            }catch (JSONException j){
                j.printStackTrace();
            }
        }
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    public void setadapter(){
        Log.d("yaha hu", "ha yahi hu");
        recyclerView = findViewById(R.id.sitem_recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(SalesDetail.this));
        adapter = new Acc2Adapter(this, data);
        adapter.setTotalFunc(this);
        recyclerView.setAdapter(adapter);
    }

    private String get_current_branch_id(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(currentBranchId, "");
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(SalesDetail.this, Home.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/

    private void getMonthTotal(){
        StringRequest request = new StringRequest(Request.Method.POST, mtotal_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String t = response.replaceAll("\"", "");
                String s = "Month Total - Rs."+t;
                smonthcost.setText(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SalesDetail.this, "Can't retrieve month total.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params= new HashMap<>();
                params.put("uname", un);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    @Override
    public void onRefresh() {
        get_data();
        getMonthTotal();
    }

    @Override
    public void getTotal(String total) {
        Log.d("Total", total);
        String t = "Today's Total - Rs."+ total+" ";
        sdtodtotal.setText(t);
    }
}
