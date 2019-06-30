package com.developer.tanay.nertia.beautyTraining;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.tanay.nertia.R;

import java.util.List;

/**
 * Created by Tanay on 20-Jan-18.
 */

public class BLinksAdapter extends RecyclerView.Adapter<BLinksAdapter.BLinksViewHolder> {

    List<BLinksItem> data;
    private Context context;
    private LayoutInflater inflater;
    private ClickListener clickListener;

    public BLinksAdapter(List<BLinksItem> data, Context context){
        this.context = context;
        this.data = data;
        //Log.d("in adapter", data.get(0).getName());
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BLinksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.blink_item, parent, false);
        return new BLinksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BLinksViewHolder holder, int position) {
        BLinksItem item = data.get(position);
        holder.name.setText(item.getName());
        holder.slug = item.getSlug();
        Glide.with(context).load(item.getThumb_link()).into(holder.thumb);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setClickListener(BLinksAdapter.ClickListener clickListener){
        this.clickListener=clickListener;
    }


    public interface ClickListener{
        public void itemClicked(View view, int position, String slug);
    }

    public class BLinksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View container;
        TextView name;
        String slug;
        ImageView thumb;
        public BLinksViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.blink_root);
            name = itemView.findViewById(R.id.per_vid_title);
            thumb = itemView.findViewById(R.id.per_thumb);
            thumb.setOnClickListener(this);
            name.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (clickListener!=null){
                clickListener.itemClicked(v, getAdapterPosition(), slug);
            }
        }
    }

}
