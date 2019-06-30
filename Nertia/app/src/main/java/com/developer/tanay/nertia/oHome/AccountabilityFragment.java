package com.developer.tanay.nertia.oHome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

/**
 * Created by Tanay on 21-Jan-18.
 */

public class AccountabilityFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AccAdapter.GetTotalFunc {

    TextView date, ttotal, smonthcost;
    EditText sname, scost;
    ImageButton button;
    Button sdrepbtn;
    RecyclerView recyclerView;
    AccAdapter adapter;
    List<AccItem> data = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    private static final String currentSalonId = "current_salon_id";
    private static final String currentBranchId = "current_branch_id";

    String sername, sercost, uname, utype;
    float total = 0;
    private static final String spUsername = "logged_in_username";
    private static final String spUserType = "log_in_user_type";
    private static final String get_url = Nertiapp.server_url+"/sservice/";
    private static final String mtotal_url = Nertiapp.server_url+"/smonthsales/";
    private static final String post_url = Nertiapp.server_url+"/qseradd/";
    private static final String date_url =Nertiapp.server_url+"/getdate/";

    public static AccountabilityFragment newInstance(){
        AccountabilityFragment fragment = new AccountabilityFragment();
        return fragment;
    }

    public AccountabilityFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accountability, container, false);
        date = view.findViewById(R.id.sacc_date);
        ttotal = view.findViewById(R.id.acitem_total);
        sname = view.findViewById(R.id.servicename);
        scost = view.findViewById(R.id.scost);
        sdrepbtn = view.findViewById(R.id.sdrep_btn);
        sdrepbtn.setOnClickListener(this);
        smonthcost = view.findViewById(R.id.smonthcost);
        button = view.findViewById(R.id.send_ser);
        recyclerView = view.findViewById(R.id.sales_recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout = view.findViewById(R.id.acswiperef);
        swipeRefreshLayout.setOnRefreshListener(this);
        uname = get_username();
        utype = get_usertype();
        button.setOnClickListener(this);
        Log.d("CREATE VIEW", "INSIDE");
        getDate();
        get_data();
        getMonthTotal();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void get_data(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, get_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                set_data(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getContext()).addToQueue(request);
    }

    private void set_data(JSONArray jsonArray){
        if (jsonArray.length()==0){
            Log.d("SET DATA", "EMPTY LIST");
            data = new ArrayList<>();
            adapter = new AccAdapter(getContext(), data);
            recyclerView.setAdapter(adapter);
            Toast.makeText(getActivity(), "No Services done today.", Toast.LENGTH_LONG).show();
        }else {
            set_list(jsonArray);
        }
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void set_list(JSONArray jsonArray){
        try {
            JSONObject object;
            AccItem current;
            data = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++){
                object = (JSONObject)jsonArray.get(i);
                current = new AccItem();
                if (object.getString("get_phone").equals(uname)){
                    current.setSername(object.getString("service_name"));
                    current.setScost(object.getString("get_str_cost"));
                    current.setTime(object.getString("get_str_time"));
                    data.add(current);
                }
            }
            if (data.size()!=0){
                adapter = new AccAdapter(getContext(), data);
                adapter.setTotalFunc(this);
                recyclerView.setAdapter(adapter);
            }
        }catch (JSONException j){
            j.printStackTrace();
        }
    }

    private String get_username(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(spUsername, "");
    }

    private String get_usertype(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(spUserType, "");
    }


    private void getDate(){
        StringRequest request = new StringRequest(Request.Method.GET, date_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String sdate = response.replace("\"", "");
                String[] datel = sdate.split("-");
                String fdate = datel[datel.length-1];
                fdate+=("-"+datel[1]);
                fdate+=("-"+datel[0]);
                String d = "Date - "+fdate;
                date.setText(d);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error retrieving date from server.", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(getContext()).addToQueue(request);
    }

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
                Toast.makeText(getActivity(), "Can't retrieve month total.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params= new HashMap<>();
                params.put("uname", get_username());
                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToQueue(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_ser:
                sername = sname.getText().toString().trim();
                sercost = scost.getText().toString().trim();
                if (sername.equals("")||sercost.equals("")){
                    Toast.makeText(getActivity(), "Fill all the values.", Toast.LENGTH_LONG).show();
                }else {
                    Log.d("SERVICE", sername);
                    Log.d("COST", sercost);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, post_url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("\"success\"")){
                                Toast.makeText(getActivity(), "Service added successfully.", Toast.LENGTH_SHORT).show();
                                sname.setText("");
                                scost.setText("");
                                get_data();
                                getMonthTotal();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String > params = new HashMap<>();
                            params.put("sername", sname.getText().toString().trim());
                            params.put("sercost", scost.getText().toString().trim());
                            params.put("suname", uname);
                            params.put("sutype", utype);
                            params.put("sbranchid", get_current_branch_id());
                            return params;
                        }
                    };
                    MySingleton.getInstance(getActivity()).addToQueue(stringRequest);
                }
                break;
            case R.id.sdrep_btn:
                Intent intent = new Intent("android.intent.action.SPdfDatePick");
                startActivity(intent);
                break;

        }
    }

    private String get_current_branch_id(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getString(currentBranchId, "");
    }

    @Override
    public void onRefresh() {
        get_data();
        getMonthTotal();
    }

    @Override
    public void getTotal(String total) {
        String stotal = "Today's Total - Rs."+total+"";
        ttotal.setText(stotal);
    }
}
