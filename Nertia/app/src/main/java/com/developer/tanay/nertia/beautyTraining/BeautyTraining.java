package com.developer.tanay.nertia.beautyTraining;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.developer.tanay.nertia.Nertiapp;
import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.Volley.MySingleton;
import com.developer.tanay.nertia.oHome.Home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BeautyTraining extends AppCompatActivity implements BeautyAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private BeautyAdapter adapter;
    SwipeRefreshLayout refreshLayout;
    private JSONArray beauty_objects = new JSONArray();
    private static final String beauty_url = Nertiapp.server_url+"/bobjs/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beauty_training);
        recyclerView = findViewById(R.id.beautyrecview);
        refreshLayout = findViewById(R.id.beauty_swipe);
        refreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getBeauty();

    }

    private List<BeautyItem> getBeautyItems(JSONArray objects){
        List<BeautyItem> beautyItems = new ArrayList<>();
        try {
            int l = objects.length();
            if (l!=0){
                for (int i=0; i<l;i++){
                    JSONObject bobject;
                    //Log.d("were here", "yeah we are");
                    BeautyItem current = new BeautyItem();
                    bobject = (JSONObject)objects.get(i);
                    current.setTopic(bobject.getString("topic"));
                    //Log.d("TOPIC", bobject.getString("topic"));
                    current.setText(bobject.getString("text"));
                    //Log.d("TEXT", bobject.getString("text"));
                    beautyItems.add(current);
                }
            }
        }catch (NullPointerException | JSONException e){
            e.printStackTrace();
        }
        return beautyItems;
    }

    private void getBeauty(){
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, beauty_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                setBeauty_objects(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BeautyTraining.this, "Network Error.", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(BeautyTraining.this).addToQueue(request);
    }

    private void setBeauty_objects(JSONArray jsonArray){
        this.beauty_objects = jsonArray;
        if (jsonArray.length()==0){
            Toast.makeText(BeautyTraining.this, "No Content Posted.", Toast.LENGTH_LONG).show();
        }else {
            adapter = new BeautyAdapter(getBeautyItems(beauty_objects), this);
            Log.d("attaching click", "yes");
            adapter.setClickListener(this);
            Log.d("attached click", "yes");
            recyclerView.setAdapter(adapter);
        }
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void itemClicked(View view, int position, String topic, String text) {
        Log.d("IN ITEM CLICKED", "true");
        Intent intent = new Intent("android.intent.action.BeautyDetail");
        intent.putExtra("topic", topic);
        intent.putExtra("text", text);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getBeauty();
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(BeautyTraining.this, Home.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BeautyTraining.this.finish();
    }*/
}
