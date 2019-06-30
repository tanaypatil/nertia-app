package com.developer.tanay.nertia.Volley;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Tanay on 07-Jan-18.
 */

public class MySingleton {

    @SuppressLint("StaticFieldLeak")
    private static MySingleton mInstance;
    private  Context mCtx;
    private RequestQueue requestQueue;

    private MySingleton(Context context){
        mCtx = context;
        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized MySingleton getInstance(Context context){
        if (mInstance==null){
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public<T> void addToQueue(Request<T> request){
        requestQueue.add(request);
    }

}
