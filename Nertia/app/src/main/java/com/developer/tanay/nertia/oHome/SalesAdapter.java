package com.developer.tanay.nertia.oHome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.beautyTraining.BeautyAdapter;

import java.util.List;

/**
 * Created by Tanay on 24-Jan-18.
 */

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {

    Context context;
    List<SalesItem> data;
    LayoutInflater inflater;
    GetTotalFunc getTotalFunc;
    SalesAdapter.ClickListener clickListener;
    float total = 0;

    public SalesAdapter(Context context, List<SalesItem> data){
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public SalesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.sales_item, parent, false);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SalesViewHolder holder, int position) {
        SalesItem item = data.get(position);
        holder.styname.setText(item.getFname());
        holder.styuname.setText(item.getUname());
        holder.stycost.setText(item.getCost());
        Log.d("In Bind", item.getFname());
        total+=Float.parseFloat(item.getCost());
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

    public void setClickListener(SalesAdapter.ClickListener clickListener){
        this.clickListener=clickListener;
    }


    public interface ClickListener{
        public void itemClicked(View view, int position, String un, String fn);
    }

    public class SalesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView styname, styuname, stycost;
        View container;
        public SalesViewHolder(View itemView) {
            super(itemView);
            styname = itemView.findViewById(R.id.salesitem_styname);
            styuname = itemView.findViewById(R.id.salesitem_styuname);
            stycost = itemView.findViewById(R.id.stycost);
            container = itemView.findViewById(R.id.sales_item_root);
            itemView.setOnClickListener(this);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String un = styuname.getText().toString().trim();
            String fn = styname.getText().toString().trim();
            if (clickListener!=null){
                clickListener.itemClicked(v, getAdapterPosition(), un, fn);
            }
        }
    }

    public interface GetTotalFunc{
        void getTotal(String total);
    }

    public void setTotalFunc(GetTotalFunc getTotalFunc){
        this.getTotalFunc = getTotalFunc;
    }


}
