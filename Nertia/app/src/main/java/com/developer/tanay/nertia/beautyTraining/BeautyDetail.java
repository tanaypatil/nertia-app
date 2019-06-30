package com.developer.tanay.nertia.beautyTraining;

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
import android.widget.EditText;
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

public class BeautyDetail extends AppCompatActivity implements BLinksAdapter.ClickListener{

    TextView title, b_text;
    TextView link_label, ques_label;
    Button submit;
    JSONArray jsonArraylinks, jsonArrayQues;
    RecyclerView blinks_recview, bques_recview;
    BLinksAdapter bLinksAdapter;
    BQuesAdapter bQuesAdapter;
    String topic, text;
    List<String> ans = new ArrayList<>();
    List<BLinksItem> links = new ArrayList<>();
    List<BQuesItem> ques = new ArrayList<>();
    private static final String spUsername = "logged_in_username";
    private static final String spUserType = "log_in_user_type";
    private static final String beauty_url = Nertiapp.server_url+"/bvids/";
    private static final String ques_url = Nertiapp.server_url+"/bques/";
    private String ans_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beauty_detail);

        if (getUserDetails().get(1).equals("Stylist")){
            ans_url = Nertiapp.server_url+"/banss/";
        }else {
            ans_url = Nertiapp.server_url+"/banso/";
        }

        topic = getIntent().getExtras().getString("topic");
        text = getIntent().getExtras().getString("text");

        title = findViewById(R.id.bd_title);
        b_text = findViewById(R.id.pd_text);
        //link_label = findViewById(R.id.blinks_label);
        //ques_label= findViewById(R.id.bdques_label);
        submit = findViewById(R.id.submit_bans);

        title.setText(topic);
        b_text.setText(text);

        blinks_recview = findViewById(R.id.blinks_recview);
        blinks_recview.setLayoutManager(new LinearLayoutManager(this));

        bques_recview = findViewById(R.id.bd_ques_recview);
        bques_recview.setLayoutManager(new LinearLayoutManager(this));

        get_data();
        get_ques();

        Log.d("Links Size", links.size()+"");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("in on click ", "submit");
                for (int i=0;i<ques.size();i++){
                    View view = bques_recview.getChildAt(i);
                    EditText editText = view.findViewById(R.id.bd_ans);
                    String answ = editText.getText().toString().trim();
                    ans.add(answ);
                }
                send_ans();
            }
        });

    }

    private void get_data(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, beauty_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length()!=0){
                    set_links(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BeautyDetail.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void set_links(JSONArray response){
        try {
            //Log.d("in set links", "yes here");
            jsonArraylinks = response;
            for (int i=0;i<jsonArraylinks.length();i++){
                BLinksItem current = new BLinksItem();
                JSONObject object = (JSONObject)jsonArraylinks.get(i);
                if (object.getString("get_parent").equals(topic)){
                    //Log.d("video name", object.getString("video_name"));
                    current.setName(object.getString("video_name"));
                    current.setLink(object.getString("video"));
                    current.setSlug(object.getString("slug"));
                    current.setThumb_link(object.getString("thumbnail"));
                    links.add(current);
                }
            }
            if (links.size()==0){
                //link_label.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.INVISIBLE);
            }else {
                bLinksAdapter = new BLinksAdapter(links, this);
                bLinksAdapter.setClickListener(this);
                blinks_recview.setAdapter(bLinksAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void get_ques(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ques_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length()!=0){
                    set_ques(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BeautyDetail.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private void set_ques(JSONArray response){
        jsonArrayQues = response;
        try {
            for (int i=0;i<jsonArrayQues.length();i++){
                JSONObject object = (JSONObject)jsonArrayQues.get(i);
                BQuesItem current = new BQuesItem();
                if (object.getString("get_parent").equals(topic)){
                    current.setQues(object.getString("question"));
                    Log.d("setting ques", object.getString("question"));
                    ques.add(current);
                }
            }
            if (ques.size()==0){
                //ques_label.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.INVISIBLE);
            }else {
                bQuesAdapter = new BQuesAdapter(ques, this);
                bques_recview.setAdapter(bQuesAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void send_ans(){
        StringRequest request = new StringRequest(Request.Method.POST, ans_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(BeautyDetail.this, "Answers saved successfully.", Toast.LENGTH_LONG).show();
                ans = new ArrayList<>();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BeautyDetail.this,"Network Error.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                for (int i=0;i<ques.size();i++){
                    params.put(ques.get(i).getQues(), ans.get(i));
                }
                params.put("username", getUserDetails().get(0));
                params.put("topic", topic);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToQueue(request);
    }

    private List<String> getUserDetails(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        List<String> ret = new ArrayList<>();
        ret.add(preferences.getString(spUsername, ""));
        ret.add(preferences.getString(spUserType, ""));
        return ret;
    }

    @Override
    public void itemClicked(View view, int position, String slug) {
        Intent intent = new Intent(BeautyDetail.this, BeautyTrainingVideo.class);
        intent.putExtra("slug", slug);
        intent.putExtra("topic", topic);
        intent.putExtra("text", text);
        startActivity(intent);
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(BeautyDetail.this, BeautyTraining.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/
}
