package com.developer.tanay.nertia.newTrends;

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

public class NewTrends extends AppCompatActivity implements CardAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    List<CardItem> data;
    JSONArray jsonArray = new JSONArray();
    SwipeRefreshLayout refreshLayout;
    private static final String trend_url = Nertiapp.server_url+"/newTrend/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_trends);
        getTrends();

        /*String extras = getIntent().getExtras().getString("trends");
        try {
            jsonArray = new JSONArray(extras);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        refreshLayout = findViewById(R.id.new_trends_swipe);
        refreshLayout.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.ntrec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public  List<CardItem> getdata(JSONArray array){
        data = new ArrayList<>();
        for (int i=0;i<array.length();i++){
            CardItem current = new CardItem();
            JSONObject object;
            try {
                object = (JSONObject)array.get(i);
                current.setTitle(object.getString("title"));
                Log.d("title hai kya?", object.getString("title"));
                current.setText(object.getString("text"));
                //current.setImgResId(img[i]);
                current.setCard_img_url(Nertiapp.server_url+object.getString("image"));
                Log.d("IMAGE URL", Nertiapp.server_url+object.getString("image"));
                data.add(current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    @Override
    public void itemClicked(View view, int position, String s, String text) {
        Log.d("card title", s);
        Intent intent = new Intent("android.intent.action.CardDetail");
        intent.putExtra("card_title", s);
        intent.putExtra("card_text", text);
        intent.putExtra("url", data.get(position).getCard_img_url());
        startActivity(intent);
    }

    private void getTrends(){
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, trend_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                /*String ret = response.toString();
                Intent intent = new Intent("android.intent.action.NewTrends");
                intent.putExtra("trends", ret);
                startActivity(intent);*/
                setJsonArray(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NewTrends.this, "Network Error.", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void setJsonArray(JSONArray jsonArray){
        Log.d("in set func", "yes");
        this.jsonArray = jsonArray;
        if (jsonArray.length()!=0){
            adapter = new CardAdapter(getdata(jsonArray), this);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        }else {
            Toast.makeText(NewTrends.this, "No New Trends to show.", Toast.LENGTH_LONG).show();
        }
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        getTrends();
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(NewTrends.this, Home.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/
}
