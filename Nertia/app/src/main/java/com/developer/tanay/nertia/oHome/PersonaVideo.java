package com.developer.tanay.nertia.oHome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.developer.tanay.nertia.Nertiapp;
import com.developer.tanay.nertia.R;

public class PersonaVideo extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_video);
        webView = findViewById(R.id.persona_video_webview);
        String slug = getIntent().getExtras().getString("slug");
        String url = Nertiapp.server_url+"/pvideos/"+slug+"/";
        webView.loadUrl(url);
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PersonaVideo.this, PersonaDetail.class);
        intent.putExtra("topic", getIntent().getExtras().getString("topic"));
        intent.putExtra("text", getIntent().getExtras().getString("text"));
        startActivity(intent);
    }*/
}
