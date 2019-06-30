package com.developer.tanay.nertia.oHome;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.Volley.MySingleton;
import com.developer.tanay.nertia.newTrends.NewTrends;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment implements MenuAdapter.ClickListener {

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    MenuAdapter menuAdapter;
    TextView tsid, tbid;

    private static final String PREF_FILE_NAME = "testpref";
    private static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private static final String spUserType = "log_in_user_type";
    private static final String currentSalonId = "current_salon_id";
    private static final String currentBranchId = "current_branch_id";



    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER));
        if (savedInstanceState!=null)
            mFromSavedInstanceState = true;

    }

    public  List<MenuItem> getData(){
        List<MenuItem> data = new ArrayList<>();
        String[] titles = {"My Account", "New Trends", "Query", "Branch Options", "Logout"};
        String[] stitles = {"My Account", "New Trends", "Query", "Logout"};

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String type = preferences.getString(spUserType, "");
        String[] types;
        if (type.equals("Stylist")){
            types = stitles;
        }else {
            types = titles;
        }
        //Log.d("login type", type);
        for (String title : types) {
            MenuItem current = new MenuItem();
            current.setTitle(title);
            data.add(current);
        }
        return data;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.rec_view);
        menuAdapter = new MenuAdapter(getActivity(), getData());
        menuAdapter.setClickListener(this);
        recyclerView.setAdapter(menuAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tsid = layout.findViewById(R.id.tsalonid);
        tbid = layout.findViewById(R.id.tbranchid);
        String ts = "SalonID - "+get_ids()[1];
        tsid.setText(ts);
        //Log.d("SALON ID", getActivity().getIntent().getExtras().getString("sid"));
        String tb = "BranchID - "+get_ids()[0];
        if (get_ids()[0]!=null){
            tbid.setText(tb);
        }
        return layout;
    }

    public void setup(int fragmentId, final DrawerLayout drawerLayout, android.support.v7.widget.Toolbar toolbar){
        View containerView = getActivity().findViewById(fragmentId);
        final ActionBarDrawerToggle mActionToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer){
                    mUserLearnedDrawer=true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer+"");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState){
            drawerLayout.openDrawer(containerView);
        }
        drawerLayout.setDrawerListener(mActionToggle);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, "false");
    }

    @Override
    public void itemClicked(View view, int position, String s) {
        if (s.equals("Logout")){
            clearPrefs();
            s = "Login";
            Intent intent = new Intent("android.intent.action."+s);
            startActivity(intent);
            getActivity().finish();
        } else if (s.equals("BranchOptions")){
            Intent intent = new Intent("android.intent.action."+s);
            intent.putExtra("bid", get_ids()[0]);
            intent.putExtra("sid", get_ids()[1]);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent("android.intent.action."+s);
            startActivity(intent);
        }
    }

    private void clearPrefs(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private String[] get_ids(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String[] ids = new String[2];
        if (preferences.contains(currentBranchId)){
            ids[0] = preferences.getString(currentBranchId, "");
            ids[1] = preferences.getString(currentSalonId, "");
            Log.d("SALON ID in frag", ids[1]);
            return ids;
        }
        return null;
    }

}
