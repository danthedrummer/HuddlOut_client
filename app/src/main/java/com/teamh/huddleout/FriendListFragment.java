package com.teamh.huddleout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "DevMsg";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ArrayList<JSONObject> friends;
    ArrayList<JSONObject> friendRequests;
    ArrayList<String> friendList;

    ArrayAdapter<String> friendAdapter;

    public FriendListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FriendListFragment newInstance() {
        FriendListFragment fragment = new FriendListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        friendList = new ArrayList<String>();

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            final User currentUser = User.getInstance(this.getActivity().getApplicationContext());
            if(friendList.size() == 0) {
                friends = currentUser.getFriends();
                friendRequests = currentUser.getFriendRequests();
                for (int i = 0; i < friends.size(); i++) {
                    try {
                        friendList.add(friends.get(i).getString("first_name") + " " + friends.get(i).getString("last_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

//                for (int i = 0; i < friends.size(); i++) {
//                    try {
//                        friendList.add(friendRequests.get(i).getString("first_name") + " " + friends.get(i).getString("last_name"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }

                setListAdapter(friendAdapter);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout rellay = (FrameLayout) inflater.inflate(R.layout.fragment_friend_list, container, false);
        friendAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friendList);
        return rellay;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        viewFriendProfile((int) id);
    }

    
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        final User currentUser = User.getInstance(this.getActivity().getApplicationContext());
//        try {
//            String profilePic = friends.get((int) id).getString("profile_picture");
//            String name = friends.get((int) id).getString("first_name") + " " + friends.get((int) id).getString("last_name");
//            String description = friends.get((int) id).getString("desc");
//            Log.i(TAG, "about to show friend");
//            ((MainMenuActivity)getActivity()).showFriend(v, name, description, profilePic);
//
//        } catch (JSONException e) {
//            Log.i(TAG, "list click fail: " + e);
//            e.printStackTrace();
//        }
//
//    }

    //Create a new ProfileActivity by passing in the friend's id
    private void viewFriendProfile(int id) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        Bundle b = new Bundle();
        b.putInt("friendId", id);
        intent.putExtras(b);
        startActivity(intent);
    }

}

