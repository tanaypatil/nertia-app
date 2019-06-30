package com.developer.tanay.nertia.oHome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.developer.tanay.nertia.R;

import java.util.List;

/**
 * Created by Tanay on 14-Feb-18.
 */

public class Acc2Adapter extends RecyclerView.Adapter<Acc2Adapter.SalesViewHolder>{
    List<AccItem> data;
    Context context;
    LayoutInflater inflater;
    float total;
    AccAdapter.GetTotalFunc getTotalFunc;

    public Acc2Adapter(Context context, List<AccItem> data){
        this.context = context;
        this.data = data;
        Log.d("In adapter", "Constructor");
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Acc2Adapter.SalesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.acc_item, parent, false);
        return new Acc2Adapter.SalesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(Acc2Adapter.SalesViewHolder holder, int position) {
        AccItem item = data.get(position);
        holder.sname.setText(item.getSername());
        Log.d("In Adapter, sname - ", item.getSername());
        holder.scost.setText(item.getScost());
        holder.stime.setText(item.getTime());
        total+=Float.parseFloat(item.getScost());
        if (position==data.size()-1){
            if (getTotalFunc!=null){
                getTotalFunc.getTotal(total+"");
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface GetTotalFunc{
        void getTotal(String total);
    }

    public void setTotalFunc(AccAdapter.GetTotalFunc getTotalFunc){
        this.getTotalFunc = getTotalFunc;
    }

    public class SalesViewHolder extends RecyclerView.ViewHolder {
        TextView sname, scost, stime;
        View container;
        public SalesViewHolder(View itemView) {
            super(itemView);
            sname = itemView.findViewById(R.id.sitem_sname);
            scost = itemView.findViewById(R.id.sitem_scost);
            stime = itemView.findViewById(R.id.sitem_sdate);
            container = itemView.findViewById(R.id.sitem_root);
        }
    }
}
