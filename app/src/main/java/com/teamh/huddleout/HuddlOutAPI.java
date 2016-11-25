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
        String params = url + "api/auth/login?username=" + username + "&password=" + password;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                if(!response.contains("invalid")){
                    token = response;
                    authorised = true;
                }else{
                    message = response;
                    authorised = false;
                }
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
        String params = url + "api/auth/checkAuth?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        if(response.equals(token)){
                            authorised = true;
                        }else{
                            authorised = false;
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

    public RequestQueue changePassword(String oldPassword, String newPassword){
        String params = url + "api/auth/changePassword?token=" + token + "&oldPassword=" + oldPassword + "&newPassword=" + newPassword;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        if(!response.contains("invalid")){
                            token = response;
                            authorised = true;
                        }else{
                            message = response;
                            authorised = false;
                        }
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

    public RequestQueue createGroup(String groupName, String activity){
        String params = url + "api/group/create?token=" + token + "&name=" + groupName + "&activity=" + activity;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        // Returns "invalid params" if invalid params
                        // Returns groupID if registration successful
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed Group Create" + err);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue deleteGroup(int groupId){
        String params = url + "api/group/delete?token=" + token + "&groupId=" + groupId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        // returns 'not found' if group membership is not found
                        // returms 'invalid role' if user is not group admin
                        // returns 'success' if deletion is successful
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed Delete Group" + err);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue getGroupMembers(int groupId){
        String params = url + "api/group/getMembers?token=" + token + "&groupId=" + groupId;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        // returns 'not member' if user is not a member of the group
                        // returns list of ids of group member profiles if successful
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed Get Members" + err);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue inviteGroupMember(int groupId, int profileId){
        String params = url + "api/group/inviteMember?token=" + token + "&groupId=" + groupId + "&profileId=" + profileId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "membership not found" if the membership is not found
                        //Returns "invalid role" if user is not an admin or moderator
                        //Returns "user not found" if invited user does not exist
                        //Returns "invitation already exists" if invited user already contains an invite
                        //Returns "already member" if user is already part of the group
                        //Returns "success" if invitation successful
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed Invite Group Member" + err);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue checkInvites(){
        String params = url + "api/group/checkInvites?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "user not found" if the user profile cannot be found
                        //Returns "no invites" if there are no invites
                        //Returns array of group ids if there are invites
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed Check Invites" + err);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue resolveGroupInvite(int groupId, String action){
        String params = url + "api/group/resolveInvite?token=" + token + "&groupId=" + groupId + "&action=" + action;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "user not found" if the user profile cannot be found
                        //Returns "no invites" if no invites where found
                        //Returns "success" if action completes successfully
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed Resolve Group Invite" + err);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue kickGroupMember(int groupId, int profileId){
        String params = url + "/api/group/kickMember?token=" + token + "&groupId=" + groupId + "&profileId=" + profileId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "membership not found" if user is not member of the group
                        //Returns "invalid role" if user is not an admin or moderator
                        //Returns "dont kick yourself" if user tried to kick themself
                        //Returns "user not found" if user is not in the group
                        //Returns "already kicked" if user was already kicked
                        //Returns "success" if kick is successful
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed " + err);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue checkKicks(){
        String params = url + "api/group/checkKicks?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "not kicked" if there are no kicks
                        //Returns array of group ids that the user has been kicked from
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed " + err);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue getProfile(int profileId){
        String params = url + "api/user/getProfile?token=" + token + "&profileId=" + profileId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "not found" if user does not exist
                        //Returns profile as JSON if registration successful
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed " + err);
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