package com.developer.tanay.nertia.newTrends;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.tanay.nertia.R;

public class CardDetail extends AppCompatActivity {

    TextView card_title;
    WebView card_text;
    ImageView card_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_detail);
        //Bundle extras = getIntent().getExtras();
        String title = getIntent().getExtras().getString("card_title");
        card_title = findViewById(R.id.cd_title);
        card_title.setText(title);
        card_text = findViewById(R.id.cd_text);
        String url  = getIntent().getExtras().getString("url");
        String text;
        text = "<html><body style=\"backgroumd-color:transparent; overflow-wrap:break-word; overflow-y:scroll\">" +
                "<p align=\"justify\" style=\"color:#373737;padding:15px;\">";
        text+= getIntent().getExtras().getString("card_text");
        text+= "</p><br></body></html>";
        card_text.setBackgroundColor(Color.TRANSPARENT);
        card_text.loadData(text, "text/html", "utf-8");
        card_img = findViewById(R.id.cd_img);
        Log.d("img_url", url);
        Glide.with(getBaseContext()).load(url).into(card_img);
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(CardDetail.this, NewTrends.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/
}
