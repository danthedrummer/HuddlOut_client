package com.teamh.huddleout;

import com.android.volley.*;
import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.*;

import java.util.concurrent.CountDownLatch;

/**
 * Created by x14729249 on 08/11/2016.
 */

//Worth looking at to track the token http://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-on-android

/*
to do lisht:
    add all responses as final strings for checks against server - use enum
 */


public class HuddlOutAPI {

    private static final String TAG = "DevMsg";

    private boolean authorised;

    private String message;

    static String token;
    private String url;

    private RequestQueue reQueue;
    private Cache cache;
    private Context context;
    private Network network;

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
    public RequestQueue login(String username, String password){
        Log.i(TAG, "login start");
        String params = url + "api/auth/login?username=" + username + "&password=" + password;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                if(!response.contains("invalid")){
                    token = response;
                    authorised = true;
                }else{
                    authorised = false;
                }
                Log.i(TAG, "login response: " + response);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed Login" + err);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }


    // Register

     /*

        /api/auth/register

        //Params: ?username, ?password
        //Returns "invalid params" if invalid params
        //Returns "occupied username" if username already taken
        //Returns "invalid username" if invalid username
        //Returns "invalid password" if password is invalid
        //Returns "success" if registration successful

      */

    public RequestQueue register (String username, String password) {

        String params = url + "api/auth/register?username=" + username + "&password=" + password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        message = response;
                        switch(response){

                            case "success":
                                token = response;
                                authorised = true;
                                break;

                            case "invalid params":
                                authorised = false;
                                break;

                            case "occupied username":
                                authorised = false;
                                break;

                            case "invalid username":
                                authorised = false;
                                break;

                            case "invalid password":
                                authorised = false;
                                break;

                            default:
                                Log.i(TAG, "default");
                                break;

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Register: " + err);
            }
        });

        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue authoriseUser(){
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
                Log.i(TAG, "Failed Authorise" + err);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }


    public boolean getAuth(){
        return authorised;
    }

    public String getMessage(){
        return message;
    }
}