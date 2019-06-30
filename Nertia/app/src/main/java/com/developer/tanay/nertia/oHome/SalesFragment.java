package com.developer.tanay.nertia.oHome;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.disklrucache.DiskLruCache;
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
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends Fragment implements SalesAdapter.GetTotalFunc, SalesAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    String sid, bid;
    TextView ttsales, lastMcost, thisMcost;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    SalesAdapter adapter;
    EditText sname, scost;
    Button pdfbtn;
    ImageButton button;
    float total;
    String date = "", sername, sercost, uname, utype;
    List<SalesItem> data = new ArrayList<>();
    private static final String spUsername = "logged_in_username";
    private static final String spUserType = "log_in_user_type";
    private static final String url = Nertiapp.server_url+"/getstysales/";
    private static final String month_url = Nertiapp.server_url+"/monthsales/";
    private static final String date_url = Nertiapp.server_url+"/getdate/";
    private static final String post_url = Nertiapp.server_url+"/qseradd/";
    private static final String currentSalonId = "current_salon_id";
    private static final String currentBranchId = "current_branch_id";

    public static SalesFragment newInstance(){
        SalesFragment fragment = new SalesFragment();
        return fragment;
    }

    public SalesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales, container, false);
        ttsales = view.findViewById(R.id.ttsales);
        //date = view.findViewById(R.id.ositem_date);
        sname = view.findViewById(R.id.sservice_entry);
        scost = view.findViewById(R.id.sservice_cost);
        button = view.findViewById(R.id.osersend_btn);
        lastMcost = view.findViewById(R.id.lastMcost);
        thisMcost = view.findViewById(R.id.thisMcost);
        pdfbtn = view.findViewById(R.id.get_pdf_btn);
        uname = get_username();
        utype = get_usertype();
        refreshLayout = view.findViewById(R.id.salesswipe);
        refreshLayout.setOnRefreshListener(this);
        recyclerView = view.findViewById(R.id.salessty_recview);
        set_ids();
        getDate();
        getMonthSales();
        getStylists();

        pdfbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.PdfDatePick");
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sname.getText().toString().trim().equals("")||scost.getText().toString().trim().equals("")){
                    Toast.makeText(getActivity(), "Fill all the values.", Toast.LENGTH_LONG).show();
                }else {
                    send_service();
                }
            }
        });

        return view;
    }

    private void set_ids(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sid = preferences.getString(currentSalonId, "");
        bid = preferences.getString(currentBranchId, "");
    }

    private void getStylists(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    setNameList(jsonArray);
                } catch (JSONException e) {
                    Log.d("Conversion Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("bid", bid);
                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToQueue(request);
    }

    private void setNameList(JSONArray response){
        if (response.length()!=0){
            data = new ArrayList<>();
            try {
                for (int i=0;i<response.length();i++){
                    JSONObject object = (JSONObject)response.get(i);
                    SalesItem current  = new SalesItem();
                    current.setFname(object.getString("fname"));
                    current.setUname(object.getString("uname"));
                    current.setCost(object.getString("cost"));
                    Log.d("cost", object.getString("cost"));
                    Log.d("fname", object.getString("fname"));
                    data.add(current);
                }
            }catch (JSONException j){
                j.printStackTrace();
            }
            if (data.size()!=0){
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new SalesAdapter(getActivity(), data);
                adapter.setTotalFunc(this);
                adapter.setClickListener(this);
                recyclerView.setAdapter(adapter);
                Log.d("adapter attached", "yes");
                if (refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                }
            }
        }else {
            Toast.makeText(getActivity(), "No Services done today.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void getTotal(String total) {
        String[] datel = date.split("-");
        String fdate = datel[datel.length-1];
        fdate+=("-"+datel[1]);
        fdate+=("-"+datel[0]);
        String text = " Total Sale on "+fdate+" - Rs."+total;
        ttsales.setText(text);
    }

    private void getDate(){
        StringRequest request = new StringRequest(Request.Method.GET, date_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                date = response.replace("\"", "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error retrieving date from server.", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(getContext()).addToQueue(request);
    }

    private String get_username(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(spUsername, "");
    }

    private String get_usertype(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(spUserType, "");
    }

    @Override
    public void itemClicked(View view, int position, String un, String fn) {
        //Toast.makeText(getActivity(), "In Item Clicked", Toast.LENGTH_LONG).show();
        //Log.d("Item","Clicked");
        Intent intent = new Intent("android.intent.action.SalesDetail");
        intent.putExtra("uname", un);
        intent.putExtra("fname", fn);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getStylists();
        getMonthSales();
    }

    private void send_service(){
        sername = sname.getText().toString().trim();
        sercost = scost.getText().toString().trim();
        Log.d("SERVICE", sername);
        Log.d("COST", sercost);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, post_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("\"success\"")){
                    Toast.makeText(getActivity(), "Service added successfully.", Toast.LENGTH_SHORT).show();
                    sname.setText("");
                    scost.setText("");
                    getStylists();
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

    private String get_current_branch_id(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getString(currentBranchId, "");
    }

    private void getMonthSales()
    {
        StringRequest request = new StringRequest(Request.Method.POST, month_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String t = "Last Month Sales = Rs."+object.getString("last");
                    lastMcost.setText(t);
                    String l = "This Month Sales = Rs."+object.getString("this");
                    thisMcost.setText(l);
                }catch (JSONException j){
                    j.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("bid", get_current_branch_id());
                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToQueue(request);
    }

}
