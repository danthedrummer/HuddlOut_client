package com.teamh.huddleout;

import com.android.volley.*;
import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.*;

/**
 * Created by x14729249 on 08/11/2016.
 */


/*
to do lisht:
    add all responses as final strings for checks against server - use enum
 */


public class HuddlOutAPI {

    private static final String TAG = "DevMsg";

    private boolean authorised;

    static String token;
    private String url;

    private RequestQueue reQueue;
    private Cache cache;
    private Context context;
    private Network network;

//    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
//    Network network = new BasicNetwork(new HurlStack());
//    RequestQueue reQueue = new RequestQueue(cache, network);

    public HuddlOutAPI(Context context){
        token = null;
        authorised = false;
        url = "https://huddlout-server-reccy.c9users.io:8081/";

        this.context = context;

        cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        network = new BasicNetwork(new HurlStack());
        reQueue = new RequestQueue(cache, network);
        reQueue.start();
    }

    // Login
    public void login(String username, String password){
        Log.i(TAG, "login start");
        String params = url + "api/auth/login?username=" + username + "&password=" + password;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                if(!response.equalsIgnoreCase("invalid password")){
                    token = response;
                }
                Log.i(TAG, "login response: " + response);
                Log.i(TAG, String.valueOf(reQueue.getSequenceNumber()));
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "You got an error there boss");
            }
        });
        reQueue.add(stringRequest);
    }


    public boolean authoriseUser(){
        Log.i(TAG, String.valueOf(reQueue.getSequenceNumber()));
        Log.i(TAG, "token to auth: " + token);
        String params = url + "api/auth/checkAuth?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        if(response.equals(token)){
                            authorised = true;
                            Log.i(TAG, "auth: " + authorised);
                        }else{
                            authorised = false;
                            Log.i(TAG, "auth: " + authorised);
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "You got an error there boss");
            }
        });
        reQueue.add(stringRequest);
        Log.i(TAG, "return auth: " + authorised);
        return authorised;
    }

}
