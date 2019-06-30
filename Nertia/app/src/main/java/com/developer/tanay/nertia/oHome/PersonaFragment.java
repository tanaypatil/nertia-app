package com.developer.tanay.nertia.oHome;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.developer.tanay.nertia.Nertiapp;
import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.Volley.MySingleton;
import com.developer.tanay.nertia.beautyTraining.BeautyAdapter;
import com.developer.tanay.nertia.beautyTraining.BeautyItem;
import com.developer.tanay.nertia.beautyTraining.BeautyTraining;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonaFragment extends Fragment implements BeautyAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    BeautyAdapter adapter;
    SwipeRefreshLayout refreshLayout;
    private JSONArray persona_objects;
    private static String persona_url = Nertiapp.server_url+"/pobjs/";

    public static PersonaFragment newInstance(){
        PersonaFragment fragment = new PersonaFragment();
        return fragment;
    }


    public PersonaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_persona, container, false);
        refreshLayout = view.findViewById(R.id.persona_swipe);
        refreshLayout.setOnRefreshListener(this);
        recyclerView = view.findViewById(R.id.persona_recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        get_data();
    }


    private void get_data(){
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, persona_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                set_data(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Network Error.", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getContext()).addToQueue(request);
    }

    private void set_data(JSONArray jsonArray){
        this.persona_objects = jsonArray;
        if (jsonArray.length()==0){
            Toast.makeText(getActivity(), "No Content Posted.", Toast.LENGTH_LONG).show();
        }else {
            adapter = new BeautyAdapter(getPersonaItems(persona_objects), getActivity());
            Log.d("attaching click", "yes");
            adapter.setClickListener(this);
            Log.d("attached click", "yes");
            recyclerView.setAdapter(adapter);
        }
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    private List<BeautyItem> getPersonaItems(JSONArray objects){
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

    @Override
    public void itemClicked(View view, int position, String topic, String text) {
        Intent intent = new Intent("android.intent.action.PersonaDetail");
        intent.putExtra("topic", topic);
        intent.putExtra("text", text);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        get_data();
    }
}
