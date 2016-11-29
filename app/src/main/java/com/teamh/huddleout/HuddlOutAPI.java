package com.teamh.huddleout;

import com.android.volley.*;
import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.*;

import org.json.JSONException;
import org.json.JSONObject;

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

    private String responseFromServer;
    private boolean authorised;
    private String message;
    static String token;
    private String url;

    private static HuddlOutAPI hAPI;
//    private RequestQueue reQueue;
    private Cache cache;
    private Context context;
    private Network network;

    public HuddlOutAPI(Context context){
        responseFromServer = "";
        token = null;
        authorised = false;
        url = "https://huddlout-server-reccy.c9users.io:8081/";

        this.context = context;

        cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        network = new BasicNetwork(new HurlStack());
//        reQueue = new RequestQueue(cache, network);
//        reQueue.start();
    }

    // set up API as a singleton
    public static synchronized  HuddlOutAPI getInstance(Context context){
        if(hAPI == null){
            hAPI = new HuddlOutAPI(context);
        }
        return hAPI;
    }

    // AUTHORISATION
    public RequestQueue login(String username, String password){
        Log.i(TAG, "Login start");
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/auth/login?username=" + username + "&password=" + password;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                if(!response.contains("invalid")){
                    try {
                        JSONObject loginJSON = new JSONObject(response);
                        token = (String) loginJSON.get("auth");
                        User u = User.getInstance((Integer) loginJSON.get("id"), context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    authorised = true;
                    message = "";
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

    public RequestQueue register (String username, String password, String firstName, String lastName) {
        Log.i(TAG, "Register start");
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/auth/register?username=" + username + "&password=" + password + "&firstName=" + firstName + "&lastName=" + lastName;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){

                        if(response.contains("invalid") || response.contains("occupied")){
                            message = response;
                            authorised = false;
                        }else{
                            try {
                                JSONObject loginJSON = new JSONObject(response);
                                token = (String) loginJSON.get("auth");
                                User u = User.getInstance((Integer) loginJSON.get("id"), context);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            authorised = true;
                            message = "";
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
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/auth/checkAuth?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        if(response.equals(token)){
                            authorised = true;
                            message = "";
                        }else{
                            authorised = false;
                            message = response;
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
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
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


    // GROUPS
    public RequestQueue createGroup(String groupName, String activity){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
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
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
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

    public RequestQueue leaveGroup(int groupId){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/group/leave?token=" + token + "&groupId=" + groupId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "not found" if group membership not found
                        //Returns "invalid role" if user is group admin
                        //Returns "success" if deletion successful
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

    public RequestQueue getGroupMembers(int groupId){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
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

    public RequestQueue getGroups(){
        Log.i(TAG, "getGroups start");
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/group/getGroups?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "no groups" if user is not member of a group
                        //Returns list of ids of groups if successful
                        if(response.equalsIgnoreCase("invalid params") || response.equalsIgnoreCase("no groups")){
                            message = response;
                        }else{
                            responseFromServer = response;
                            message = "";
                        }
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

    public RequestQueue inviteGroupMember(int groupId, int profileId){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
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
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
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
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
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
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
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
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
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


    // USER
    public RequestQueue getProfile(int profileId){
        Log.i(TAG, "getProfile start");
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/getProfile?token=" + token + "&profileId=" + profileId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "not found" if user does not exist
                        //Returns profile as JSON if registration successful
                        if(response.equalsIgnoreCase("invalid params") || response.equalsIgnoreCase("not found")){
                            message = response;
                        }else{
                            responseFromServer = response;
                            message = "";
                        }
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

    public RequestQueue editProfile(String firstName, String lastName, String profilePicture, int age, String description, String privacy){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/edit?token=" + token + "&firstName="
                + firstName + "&lastName=" + lastName + "&profilePicture="
                + profilePicture + "&age=" + age + "&description="
                + description + "&privacy=" + privacy;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "description invalid range" if the description value is too large
                        //Returns "privacy invalid value" if privacy does not match either "PUBLIC" or "PRIVATE"
                        //Returns "success" if edit is successful
                        if(response.equalsIgnoreCase("invalid params") || response.equalsIgnoreCase("description invalid range") || response.equalsIgnoreCase("privacy invalid value")){
                            message = response;
                        }else{
                            responseFromServer = response;
                            message = "";
                        }
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

    public RequestQueue getProfilePicture(){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/getProfilePictures?token=" + token;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns list (string) of profile pictures
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

    public RequestQueue downloadPicture(String imageName){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/downloadPicture?token=" + token + "&imageName=" + imageName;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns an error if the file cannot be found, or a backtracking has been attempted
                        //Returns a profile picture file if file is found
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


    // FRIEND
    public RequestQueue sendFriendRequest(String username){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/sendFriendRequest?token=" + token + "&username=" + username;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "relationship already exists" if user_a already has a relationship with user_b
                        //Returns "user not found" if user_b cannot be found
                        //Returns "success" if friend request is successfully created
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

    public RequestQueue getFriendRequests(){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/getFriendRequests?token=" + token;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "no requests found" if there are no friend requests
                        //Returns list of friend requests
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

    public RequestQueue resolveFriendRequest(int profileId, String action){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/resolveFriendRequest?token=" + token + "&profileId=" + profileId + "&action=" + action;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "cannot befriend yourself" if user tried to befriend themself
                        //Returns "invite not found" if invite does not exist
                        //Returns "success" if friend request action completes
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

    public RequestQueue getFriends(){
        Log.i(TAG, "getFriends start");
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/viewFriends?token=" + token;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Returns "invalid params" if invalid params
                        //Returns "no friends" if user has no friends
                        //Returns list of friend ids and relationshp types if user has friends
                        if(response.equalsIgnoreCase("invalid params") || response.equalsIgnoreCase("no friends")){
                            message = response;
                        }else{
                            responseFromServer = response;
                            message = "";
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                Log.i(TAG, "Failed Check Invites" + err);
                message = "error";
                responseFromServer = "error";
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue deleteFriend(int profileId){
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/deleteFriend?token=" + token + "&profileId" + profileId;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){

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


    // RETURN DATA
    public boolean getAuth(){
        return authorised;
    }

    public String getMessage(){

        return message;
    }

    public String getResponse(){
        return responseFromServer;
    }
}


//        String params = url +
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
//                new Response.Listener<String>(){
//                    @Override
//                    public void onResponse(String response){
//
//                    }
//                }, new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError err){
//                Log.i(TAG, "Failed Check Invites" + err);
//            }
//        });
//        reQueue.add(stringRequest);
//        return reQueue;