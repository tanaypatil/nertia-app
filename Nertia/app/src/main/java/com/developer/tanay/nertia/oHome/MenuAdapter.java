package com.developer.tanay.nertia.oHome;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.developer.tanay.nertia.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Tanay on 06-Jan-18.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<MenuItem> data = Collections.emptyList();
    private Context context;
    private ClickListener clickListener;
    private static final String spUserType = "log_in_user_type";

    MenuAdapter(Context context, List<MenuItem> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.menu_item,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MenuItem current = data.get(position);
        holder.title.setText(current.getTitle());
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener=clickListener;
    }


    public interface ClickListener{
        public void itemClicked(View view, int position, String s);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.menu_item_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();
            String s = "";
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String type = sharedPreferences.getString(spUserType, "");
            if (type.equals("Stylist")){
                switch (i){
                    case 0:
                        s = "Dashboard";
                        break;
                    case 1:
                        s = "NewTrends";
                        break;
                    case 2:
                        s = "Query";
                        break;
                    case 3:
                        s = "Logout";
                        break;

                }
            }else {
                switch (i){
                    case 0:
                        s = "ODashboard";
                        break;
                    case 1:
                        s = "NewTrends";
                        break;
                    case 2:
                        s = "Query";
                        break;
                    case 3:
                        s = "BranchOptions";
                        break;
                    case 4:
                        s = "Logout";
                        break;
                }
            }

            if (clickListener!=null){
                clickListener.itemClicked(v, getAdapterPosition(), s);
            }

        }
    }

}
