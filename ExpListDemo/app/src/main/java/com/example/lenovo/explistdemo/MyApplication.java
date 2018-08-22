package com.example.lenovo.explistdemo;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Lenovo on 19-08-2018.
 */

public class MyApplication extends Application {

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public static synchronized MyApplication getInstance(){
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue==null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T>request, String tag){
        getRequestQueue().add(request);
    }


    public <T> void addToRequestQueue(Request<T>request){
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader(){
        getRequestQueue();
        if (imageLoader==null){
            imageLoader = new ImageLoader(this.requestQueue,new BitmapLruCache());
        }

        return this.imageLoader;
    }

    public void cancelPendingReq(Object tag){
        if (requestQueue!=null){
            requestQueue.cancelAll(tag);
        }
    }


}
