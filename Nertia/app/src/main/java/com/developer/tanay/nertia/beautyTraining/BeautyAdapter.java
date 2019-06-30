package com.developer.tanay.nertia.beautyTraining;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.developer.tanay.nertia.R;
import com.developer.tanay.nertia.newTrends.CardAdapter;

import java.util.List;

/**
 * Created by Tanay on 17-Jan-18.
 */

public class BeautyAdapter extends RecyclerView.Adapter<BeautyAdapter.BeautyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<BeautyItem> all_data;
    private ClickListener clickListener;

    public BeautyAdapter(List<BeautyItem> all_data, Context context){
        this.context = context;
        this.all_data = all_data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BeautyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.beauty_item, parent, false);
        return new BeautyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BeautyViewHolder holder, int position) {
        BeautyItem item = all_data.get(position);
        holder.topic.setText(item.getTopic());
        holder.text.setText(item.getText());
    }

    @Override
    public int getItemCount() {
        return all_data.size();
    }

    public void setClickListener(BeautyAdapter.ClickListener clickListener){
        this.clickListener=clickListener;
    }


    public interface ClickListener{
        public void itemClicked(View view, int position, String topic, String text);
    }

    public class BeautyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView topic;
        TextView text;
        View container, scontainer;
        public BeautyViewHolder(View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.beauty_topic);
            text = itemView.findViewById(R.id.beauty_text);
            container = itemView.findViewById(R.id.beauty_root);
            scontainer = itemView.findViewById(R.id.bd_sroot);
            itemView.setOnClickListener(this);
            container.setOnClickListener(this);
            scontainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String to = topic.getText().toString().trim();
            String te = text.getText().toString().trim();
            Log.d("in on click", "yes here");
            if (clickListener!=null){
                clickListener.itemClicked(v, getAdapterPosition(), to, te);
            }
        }
    }

}
