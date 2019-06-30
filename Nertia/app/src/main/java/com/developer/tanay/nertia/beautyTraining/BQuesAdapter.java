package com.developer.tanay.nertia.beautyTraining;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.developer.tanay.nertia.R;

import java.util.List;

/**
 * Created by Tanay on 20-Jan-18.
 */

public class BQuesAdapter extends RecyclerView.Adapter<BQuesAdapter.BQuesViewHolder> {

    Context context;
    LayoutInflater inflater;
    List<BQuesItem> data;

    public BQuesAdapter(List<BQuesItem> data, Context context){
        this.context= context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BQuesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bques_item, parent, false);
        return new BQuesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BQuesViewHolder holder, int position) {
        BQuesItem bQuesItem = data.get(position);
        holder.ques.setText(bQuesItem.getQues());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class BQuesViewHolder extends RecyclerView.ViewHolder {
        TextView ques;
        EditText ans;
        View container;
        public BQuesViewHolder(View itemView) {
            super(itemView);
            ques = itemView.findViewById(R.id.bd_ques);
            ans = itemView.findViewById(R.id.bd_ans);
            container = itemView.findViewById(R.id.bd_ques_root);
        }
    }

}
