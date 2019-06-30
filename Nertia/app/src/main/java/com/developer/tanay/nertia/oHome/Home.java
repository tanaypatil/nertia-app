package com.developer.tanay.nertia.oHome;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;

import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.logsign.Login;

public class Home extends AppCompatActivity {

    Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    String sid, bid;
    private static final String spUserType = "log_in_user_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        save_ids();

        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navfragment);
        navigationDrawerFragment.setup(R.id.navfragment, (DrawerLayout)findViewById(R.id.drawer_layout), toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.view_pager_id);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(PersonaFragment.newInstance(), "Persona");
        //adapter.addFragment(, "Sales");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog_Alert));
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position){
                case 0:
                    return PersonaFragment.newInstance();
                case 1:
                    if (getUserDetails().equals("Stylist")){
                        return AccountabilityFragment.newInstance();
                    }else {
                        return SalesFragment.newInstance();
                    }
            }
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Style Guide";
                case 1:
                    return "Sales Register";
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private String getUserDetails(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ret;
        ret = preferences.getString(spUserType, "");
        return ret;
    }

    private void save_ids(){
        if (getIntent().hasExtra("sid")){
            sid = getIntent().getExtras().getString("sid");

        }
        if (getIntent().hasExtra("bid")){
            bid = getIntent().getExtras().getString("bid");
        }
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/
}
