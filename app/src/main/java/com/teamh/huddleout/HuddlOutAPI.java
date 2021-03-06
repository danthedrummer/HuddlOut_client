package com.teamh.huddleout;

import com.android.volley.*;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Member;
import java.net.URI;
import java.net.URLEncoder;
import java.security.acl.Group;
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
    static String token;
    private String url;

    private static HuddlOutAPI hAPI;
    private RequestQueue reQueue;
    private Cache cache;
    private Context context;
    private Network network;

    public HuddlOutAPI(Context context) {
        token = null;
        authorised = false;
        url = "https://huddlout-server-reccy.c9users.io:8081/";

        this.context = context;

        cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        network = new BasicNetwork(new HurlStack());
        reQueue = new RequestQueue(cache, network);
        reQueue.start();
    }

    // set up API as a singleton
    public static synchronized HuddlOutAPI getInstance(Context context) {
        if (hAPI == null) {
            hAPI = new HuddlOutAPI(context);
        }
        return hAPI;
    }

    // AUTHORISATION
    public RequestQueue login(String username, String password) {
//        Log.i(TAG, "Login start");
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/auth/login?username=" + Uri.encode(username) + "&password=" + Uri.encode(password);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.contains("invalid")) {
                            try {
                                JSONObject loginJSON = new JSONObject(response);
                                token = (String) loginJSON.get("auth");
                                User.getInstance((Integer) loginJSON.get("id"), context);
                                getFriends();
                                getFriendRequests();
                                getGroups();
                                checkInvites();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            authorised = true;
                        } else {
                            Popup.show(response, context);
                            authorised = false;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Login " + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue register(String username, String password, String firstName, String lastName) {
//        Log.i(TAG, "Register start");
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/auth/register?username=" + Uri.encode(username) + "&password=" + Uri.encode(password) + "&firstName=" + Uri.encode(firstName) + "&lastName=" + Uri.encode(lastName);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        if (response.contains("invalid") || response.contains("occupied")) {
                            Popup.show(response, context);
                            authorised = false;
                        } else {
                            try {
                                JSONObject loginJSON = new JSONObject(response);
                                token = (String) loginJSON.get("auth");
                                User u = User.getInstance((Integer) loginJSON.get("id"), context);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            authorised = true;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Register: " + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });

        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue authoriseUser() {
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/auth/checkAuth?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals(token)) {
                            authorised = true;
                        } else {
                            authorised = false;
                            Popup.show(response, context);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Authorise" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue changePassword(String oldPassword, String newPassword) {
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/auth/changePassword?token=" + token + "&oldPassword=" + Uri.encode(oldPassword) + "&newPassword=" + Uri.encode(newPassword);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.contains("invalid")) {
                            token = response;
                            authorised = true;
                        } else {
                            Popup.show(response, context);
                            authorised = false;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Login" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }


    // GROUPS
    public void createGroup(String groupName, String activity, final MainMenuActivity mainMenuActivity) {
        String params = url + "api/group/create?token=" + token + "&name=" + Uri.encode(groupName) + "&activity=" + Uri.encode(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Returns "invalid params" if invalid params
                        // Returns groupID if registration successful
                        if(!response.equalsIgnoreCase("invalid params")){
                            getGroups();
                            mainMenuActivity.openGroupMenu(Integer.parseInt(response));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Group Create" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public RequestQueue deleteGroup(int groupId) {
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/group/delete?token=" + token + "&groupId=" + groupId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // returns 'not found' if group membership is not found
                        // returms 'invalid role' if user is not group admin
                        // returns 'success' if deletion is successful
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Delete Group" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue leaveGroup(int groupId) {
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/group/leave?token=" + token + "&groupId=" + groupId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "not found" if group membership not found
                        //Returns "invalid role" if user is group admin
                        //Returns "success" if deletion successful
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue getGroupMembers(int groupId, final GroupWelcomeFragment groupWelcomeFragment) {
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/group/getMembers?token=" + token + "&groupId=" + groupId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // returns 'not member' if user is not a member of the group
                        // returns list of ids of group member profiles if successful
                        groupWelcomeFragment.getGroupMembersCallback(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Get Members" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public void getGroups() {
//        Log.i(TAG, "getGroups start");
        String params = url + "api/group/getGroups?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "no groups" if user is not member of a group
                        //Returns list of ids of groups if successful
                        if (response.equalsIgnoreCase("invalid params")) {
                            Popup.show(response, context);
                        } else {
                            User.getInstance(context).setGroupList(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public RequestQueue inviteGroupMember(int groupId, String username, final GroupWelcomeFragment activity) {
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/group/inviteMember?token=" + token + "&groupId=" + groupId + "&username=" + username;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "membership not found" if the membership is not found
                        //Returns "invalid role" if user is not an admin or moderator
                        //Returns "user not found" if invited user does not exist
                        //Returns "invitation already exists" if invited user already contains an invite
                        //Returns "already member" if user is already part of the group
                        //Returns "success" if invitation successful

                        activity.inviteMemberCallback(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Invite Group Member" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public void checkInvites() {
        String params = url + "api/group/checkInvites?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "user not found" if the user profile cannot be found
                        //Returns "no invites" if there are no invites
                        //Returns a list of groups if there are invites
                        if(!response.equalsIgnoreCase("invalid params") || !response.equalsIgnoreCase("user not found")){
                            User.getInstance(context).setGroupInvites(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public void resolveGroupInvite(int groupId, String action, final GroupListFragment groupListFragment) {
        String params = url + "api/group/resolveInvite?token=" + token + "&groupId=" + groupId + "&action=" + Uri.encode(action);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "user not found" if the user profile cannot be found
                        //Returns "no invites" if no invites where found
                        //Returns "success" if action completes successfully
                        Popup.show(response, context);
                        if(response.equalsIgnoreCase("success")){
                            getGroups(groupListFragment);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Resolve Group Invite" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public RequestQueue kickGroupMember(int groupId, int profileId, final GroupWelcomeFragment activity) {
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/group/kickMember?token=" + token + "&groupId=" + groupId + "&profileId=" + profileId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "membership not found" if user is not member of the group
                        //Returns "invalid role" if user is not an admin or moderator
                        //Returns "dont kick yourself" if user tried to kick themself
                        //Returns "user not found" if user is not in the group
                        //Returns "already kicked" if user was already kicked
                        //Returns "success" if kick is successful

                        activity.kickCallback(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed " + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue checkKicks() {
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/group/checkKicks?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "not kicked" if there are no kicks
                        //Returns array of group ids that the user has been kicked from
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed " + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }


    // GROUPS
    // used for when groupFragment is calling checkInvites of getGroups
    public void checkInvites(final GroupListFragment groupListFragment) {
        String params = url + "api/group/checkInvites?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "user not found" if the user profile cannot be found
                        //Returns "no invites" if there are no invites
                        //Returns a list of groups if there are invites
                        if(!response.equalsIgnoreCase("invalid params") || !response.equalsIgnoreCase("user not found")){
                            User.getInstance(context).setGroupInvites(response);
                            groupListFragment.setAdapters();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public void getGroups(final GroupListFragment groupListFragment) {
//        Log.i(TAG, "getGroups start");
        String params = url + "api/group/getGroups?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "no groups" if user is not member of a group
                        //Returns list of ids of groups if successful
                        if (response.equalsIgnoreCase("invalid params")) {
                            Popup.show(response, context);
                        } else {
                            User.getInstance(context).setGroupList(response);
                            checkInvites(groupListFragment);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }


    // USER
    public void getProfile(final int profileId) {
//        Log.i(TAG, "getProfile start");
        String params = url + "api/user/getProfile?token=" + token + "&profileId=" + profileId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "not found" if user does not exist
                        //Returns profile as JSON if registration successful
                        if (response.equalsIgnoreCase("invalid params") || response.equalsIgnoreCase("not found")) {
                            Popup.show(response, context);
                        } else {
                            User.getInstance(context).setProfileInformation(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed " + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    // USER
    public void getProfile(final int profileId, final ProfileActivity profileActivity) {
//        Log.i(TAG, "getProfile start");
        String params = url + "api/user/getProfile?token=" + token + "&profileId=" + profileId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "not found" if user does not exist
                        //Returns profile as JSON if registration successful
                        if (response.equalsIgnoreCase("invalid params") || response.equalsIgnoreCase("not found")) {
                            Popup.show(response, context);
                        } else {
                            profileActivity.getProfileCallback(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed " + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public void editProfile(String firstName, String lastName, String profilePicture, int age, String description, String privacy) {
        String params = url + "api/user/edit?token=" + token + "&firstName="
                + Uri.encode(firstName) + "&lastName=" + Uri.encode(lastName) + "&profilePicture="
                + Uri.encode(profilePicture) + "&age=" + age + "&desc="
                + Uri.encode(description) + "&privacy=" + Uri.encode(privacy);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "description invalid range" if the description value is too large
                        //Returns "privacy invalid value" if privacy does not match either "PUBLIC" or "PRIVATE"
                        //Returns "success" if edit is successful
                        if (response.equalsIgnoreCase("invalid params") || response.equalsIgnoreCase("description invalid range") || response.equalsIgnoreCase("privacy invalid value")) {
                            Popup.show(response, context);
                        } else {
                            getProfile(User.getInstance(context).getProfileID());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public RequestQueue getProfilePicture() {
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/getProfilePictures?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns list (string) of profile pictures
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }

    public RequestQueue downloadPicture(String imageName) {
        RequestQueue reQueue = new RequestQueue(cache, network);
        reQueue.start();
        String params = url + "api/user/downloadPicture?token=" + token + "&imageName=" + Uri.encode(imageName);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns an error if the file cannot be found, or a backtracking has been attempted
                        //Returns a profile picture file if file is found
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
        return reQueue;
    }


    // FRIEND
    public void sendFriendRequest(String username) {
        String params = url + "api/user/sendFriendRequest?token=" + token + "&username=" + Uri.encode(username);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "relationship already exists" if user_a already has a relationship with user_b
                        //Returns "user not found" if user_b cannot be found
                        //Returns "success" if friend request is successfully created
                        Popup.show(response, context);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public void getFriendRequests() {
//        Log.i(TAG, "getFriendRequests");
        String params = url + "api/user/getFriendRequests?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "no requests found" if there are no friend requests
                        //Returns list of friend requests
                        if (!response.equalsIgnoreCase("invalid params")) {
                            User.getInstance(context).setFriendRequests(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public void resolveFriendRequest(int profileId, String action, final FriendListFragment friendListFragment) {
//        Log.i(TAG, "Resolve friend request: " + profileId + " " + action);
        String params = url + "api/user/resolveFriendRequest?token=" + token + "&profileId=" + profileId + "&action=" + Uri.encode(action);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "cannot befriend yourself" if user tried to befriend themself
                        //Returns "invite not found" if invite does not exist
                        //Returns "success" if friend request action completes
                        Popup.show(response, context);
                        if(response.equalsIgnoreCase("success")){
                            getFriends(friendListFragment);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public void getFriends() {
//        Log.i(TAG, "getFriends start");
        String params = url + "api/user/viewFriends?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "no friends" if user has no friends
                        //Returns list of friend ids and relationshp types if user has friends
                        if (response.equalsIgnoreCase("invalid params")) {
                            Popup.show(response, context);
                        } else {
                            User.getInstance(context).setFriendsList(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public void deleteFriend(int profileId) {
//        Log.i(TAG, "Start deletefriend with params: " + profileId + "   " + token);
        String params = url + "api/user/deleteFriend?token=" + token + "&profileId=" + profileId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Params: ?token, ?profileId
                        //Returns "invalid params" if invalid params
                        //Returns "friend not found" if friend cannot be found
                        //Returns "success" if friend is deleted
                        Popup.show(response, context);
                        if(response.equalsIgnoreCase("success")){
                            getFriends();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Delete Friend: " + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    // FRIENDS
    // Use when Friend List fragment is calling getFriends or friend requests
    public void getFriends(final FriendListFragment friendListFragment) {
//        Log.i(TAG, "getFriends start");
        String params = url + "api/user/viewFriends?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "no friends" if user has no friends
                        //Returns list of friend ids and relationshp types if user has friends
                        if (response.equalsIgnoreCase("invalid params")) {
                            Popup.show(response, context);
                        } else {
                            User.getInstance(context).setFriendsList(response);
                            getFriendRequests(friendListFragment);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    public void getFriendRequests(final FriendListFragment friendListFragment) {
//        Log.i(TAG, "getFriendRequests");
        String params = url + "api/user/getFriendRequests?token=" + token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Returns "invalid params" if invalid params
                        //Returns "no requests found" if there are no friend requests
                        //Returns list of friend requests
                        if (!response.equalsIgnoreCase("invalid params")) {
                            User.getInstance(context).setFriendRequests(response);
                            friendListFragment.setAdapters();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Check Invites" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }



    // VOTING
    //Create a new vote for the group (UNTESTED, WIP)
    public void createVote(int groupId, String name, String desc, int duration, String option1, String option2, String option3, String option4, final VotingFragment votingFragment) {
        Log.i(TAG, "Creating Vote for group: " + groupId);

        if(groupId == -1 || name == null || desc == null || duration == -1 || option1 == null || option2 == null) {
            votingFragment.createVoteCallback("invalid input paramaters");
        } else {
            //Build GET request
            String params = "";
            try {
                if(option3 == null && option4 == null) {
                    params = url + "api/group/createVote?token=" + token +
                            "&groupId=" + groupId +
                            "&name=" + URLEncoder.encode(name, "UTF-8") +
                            "&desc=" + URLEncoder.encode(desc, "UTF-8") +
                            "&duration=" + duration +
                            "&option1=" + URLEncoder.encode(option1, "UTF-8") +
                            "&option2=" + URLEncoder.encode(option2, "UTF-8");
                } else if(option4 == null) {
                    params = url + "api/group/createVote?token=" + token +
                            "&groupId=" + groupId +
                            "&name=" + URLEncoder.encode(name, "UTF-8") +
                            "&desc=" + URLEncoder.encode(desc, "UTF-8") +
                            "&duration=" + duration +
                            "&option1=" + URLEncoder.encode(option1, "UTF-8") +
                            "&option2=" + URLEncoder.encode(option2, "UTF-8") +
                            "&option3=" + URLEncoder.encode(option3, "UTF-8");
                } else {
                    params = url + "api/group/createVote?token=" + token +
                            "&groupId=" + groupId +
                            "&name=" + URLEncoder.encode(name, "UTF-8") +
                            "&desc=" + URLEncoder.encode(desc, "UTF-8") +
                            "&duration=" + duration +
                            "&option1=" + URLEncoder.encode(option1, "UTF-8") +
                            "&option2=" + URLEncoder.encode(option2, "UTF-8") +
                            "&option3=" + URLEncoder.encode(option3, "UTF-8") +
                            "&option4=" + URLEncoder.encode(option4, "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(TAG, "Create Group Response: " + response);
                            votingFragment.createVoteCallback(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError err) {
                    Log.i(TAG, "Failed Create Vote" + err);
                    Popup.show("INTERNAL ERROR: " + err, context);
                }
            });
            reQueue.add(stringRequest);
        }
    }

    //Get votes a new vote for the group (UNTESTED, WIP)
    public void getVotes(int groupId, final VotingFragment votingFragment) {
        Log.i(TAG, "Checking Vote for group: " + groupId);

        //Build GET request
        String params = url + "api/group/getVotes?token=" + token +
                "&groupId=" + groupId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        votingFragment.setVotes(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Create Vote" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    //Submit a vote (UNTESTED, WIP)
    public void submitVote(int optionId, final VotingFragment votingFragment) {
        Log.i(TAG, "Submitting Vote: " + optionId);

        //Build GET request
        String params = url + "api/group/submitVote?token=" + token +
                "&optionId=" + optionId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        votingFragment.submitVoteCallback(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.i(TAG, "Failed Create Vote" + err);
                Popup.show("INTERNAL ERROR: " + err, context);
            }
        });
        reQueue.add(stringRequest);
    }

    // RETURN DATA
    public boolean getAuth() {
        return authorised;
    }

}