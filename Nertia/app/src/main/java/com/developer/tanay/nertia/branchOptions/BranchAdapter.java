package com.developer.tanay.nertia.branchOptions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.newTrends.CardAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tanay on 23-Jan-18.
 */

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {

    Context context;
    LayoutInflater inflater;
    List<BranchItem> data = Collections.emptyList();
    private BranchAdapter.ClickListener clickListener;
    private static final String currentSalonId = "current_salon_id";
    private static final String currentBranchId = "current_branch_id";

    public BranchAdapter(Context c, List<BranchItem> data){
        this.context = c;
        this.data = data;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public BranchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.branch_item, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BranchViewHolder holder, int position) {
        BranchItem item = data.get(position);
        holder.bid.setText(item.getBranch_id());
        holder.bname.setText(item.getBranch_name());
    }

    @Override
    public int getItemCount() {
        if (data!=null){
            return data.size();
        }
        return 1;
    }

    public void setClickListener(BranchAdapter.ClickListener clickListener){
        this.clickListener=clickListener;
    }


    public interface ClickListener{
        public void itemClicked(View view, int position, String bid);
    }

    public class BranchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView bid, bname;
        Button button;
        View container;
        public BranchViewHolder(View itemView) {
            super(itemView);
            bid = itemView.findViewById(R.id.branchitem_id);
            bname = itemView.findViewById(R.id.branchitem_name);
            button = itemView.findViewById(R.id.set_branch_btn);
            container = itemView.findViewById(R.id.branchitem_root);
            //container.setOnClickListener(this);
            button.setOnClickListener(this);
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("On bbtn", "hereeeeeeee");
            if (clickListener!=null){
                clickListener.itemClicked(v, getAdapterPosition(), bid.getText().toString());
            }
        }
    }
}
