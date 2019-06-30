package com.developer.tanay.nertia.newTrends;

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
 * Created by Tanay on 10-Jan-18.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardItem> card_data;
    private LayoutInflater inflater;
    private ClickListener clickListener;
    private Context context;

    public CardAdapter(List<CardItem> card_data, Context c){
        this.card_data = card_data;
        inflater = LayoutInflater.from(c);
        context = c;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        CardItem item = card_data.get(position);
        Log.d("check", "In Bind View Holder");
        holder.title.setText(item.getTitle());
        holder.card_text.setText(item.getText());
        //holder.card_img.setImageResource(item.getImgResId());
        String url = item.getCard_img_url();
        Log.d("img url", url);
        //Picasso.with(context).load(url).into(holder.card_img);
        Glide.with(context).load(url).into(holder.card_img).clearOnDetach();
    }

    public void setClickListener(CardAdapter.ClickListener clickListener){
        this.clickListener=clickListener;
    }


    public interface ClickListener{
        public void itemClicked(View view, int position, String s, String text);
    }

    @Override
    public int getItemCount() {
        return card_data.size();
    }


    class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView card_text;
        public ImageView card_img;
        View container;
        View card;

        public CardViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            card_text = itemView.findViewById(R.id.card_text);
            card_img = itemView.findViewById(R.id.cd_img);
            container = itemView.findViewById(R.id.card_root);
            card = itemView.findViewById(R.id.card_view);
            itemView.setOnClickListener(this);
            card.setOnClickListener(this);
            card_img.setOnClickListener(this);
            Log.d("card view holder", "here1");
        }

        @Override
        public void onClick(View v) {
            String s = title.getText().toString();
            String text = card_text.getText().toString();
            if (clickListener!=null){
                Log.d("card view holder2", "here2"+s);
                clickListener.itemClicked(v, getAdapterPosition(), s, text);
            }
        }
    }

}
