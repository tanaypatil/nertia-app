package com.developer.tanay.nertia.beautyTraining;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import android.widget.VideoView;

import com.developer.tanay.nertia.Nertiapp;
import com.developer.tanay.nertia.R;

public class BeautyTrainingVideo extends AppCompatActivity {

    WebView webView;
    String topic, text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beauty_training_video);
        webView = findViewById(R.id.beauty_video_webview);
        String slug = getIntent().getExtras().getString("slug");
        String url = Nertiapp.server_url+"/bvideos/"+slug+"/";
        webView.loadUrl(url);
        topic = getIntent().getExtras().getString("topic");
        text = getIntent().getExtras().getString("text");
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BeautyTrainingVideo.this, BeautyDetail.class);
        intent.putExtra("topic", topic);
        intent.putExtra("text", text);
        startActivity(intent);
    }*/
}
