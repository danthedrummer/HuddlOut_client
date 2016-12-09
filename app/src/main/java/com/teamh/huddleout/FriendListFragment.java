
package com.teamh.huddleout;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
public class FriendListFragment extends Fragment implements AdapterView.OnItemClickListener {
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
    ArrayList<String> friendRequestList;

    ArrayAdapter<String> friendAdapter;
    ArrayAdapter<String> friendRequestAdapter;

    ListView friendsListView;
    ListView friendsRequestListView;

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
        friendRequestList = new ArrayList<String>();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setAdapters();
        }
    }


    //Solution for this found on http://stackoverflow.com/questions/17693578/android-how-to-display-2-listviews-in-one-activity-one-after-the-other
    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout rellay = (FrameLayout) inflater.inflate(R.layout.fragment_friend_list, container, false);
        friendAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friendList);

        friendsListView = (ListView)rellay.findViewById(R.id.friendListView);
        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long l) {
                viewFriendProfile(id);
            }
        });

        friendRequestAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friendRequestList);
        friendsRequestListView = (ListView)rellay.findViewById(R.id.friendRequestListView);
        friendsRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long l) {
                try {
                    String profilePic = friendRequests.get(id).getString("profile_picture");
                    String name = friendRequests.get(id).getString("first_name") + " " + friendRequests.get(id).getString("last_name");
                    String description = friendRequests.get(id).getString("description");
                    int profileId = friendRequests.get(id).getInt("profile_a");

                    showFriendRequest(view, profileId, name, description, profilePic);

                } catch (JSONException e) {
                    Log.i(TAG, "list click fail: " + e);
                    e.printStackTrace();
                }catch (IndexOutOfBoundsException e){
                    Log.i(TAG, "Index out of bounds for request list: " + e);
                    e.printStackTrace();
                }
            }
        });

        ListUtils.setDynamicHeight(friendsListView);
        ListUtils.setDynamicHeight(friendsRequestListView);

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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }


    public void setAdapters(){
//        Log.i(TAG, "Setting Adapters");
        final User currentUser = User.getInstance(this.getActivity().getApplicationContext());
        HuddlOutAPI.getInstance(this.getActivity().getApplicationContext()).getFriends();
        HuddlOutAPI.getInstance(this.getActivity().getApplicationContext()).getFriendRequests();

        try {
            friendList.clear();
        }catch (IndexOutOfBoundsException e){
            Log.i(TAG, "list clearance fail: " + e);
        }

        try {
            friendRequestList.clear();
        }catch (IndexOutOfBoundsException e){
            Log.i(TAG, "list clearance fail: " + e);
        }

        friends = currentUser.getFriends();
        friendRequests = currentUser.getFriendRequests();

        for (int i = 0; i < friends.size(); i++) {
            try {
                friendList.add(friends.get(i).getString("first_name") + " " + friends.get(i).getString("last_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < friendRequests.size(); i++) {
            try {
                friendRequestList.add(friendRequests.get(i).getString("first_name") + " " + friendRequests.get(i).getString("last_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        friendsListView.setAdapter(friendAdapter);
        friendsRequestListView.setAdapter(friendRequestAdapter);

    }


    //Create a new ProfileActivity by passing in the friend's id
    private void viewFriendProfile(int id) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        Bundle b = new Bundle();
        b.putInt("friendId", id);
        intent.putExtras(b);
        startActivity(intent);
    }


    public void showFriendRequest(View v, final int profileId, String name, String description, String profilePic){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        final Handler HANDLER = new Handler();

        alert.setIcon(getActivity().getApplicationContext().getResources().getIdentifier(profilePic, "drawable", "com.teamh.huddleout"));
        alert.setTitle(name);
        alert.setMessage(description);

        final TextView input = new TextView(v.getContext());
        alert.setView(input);

        alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                User.getInstance(getActivity().getApplicationContext()).resolveFriendRequest(profileId, "accept");
                HANDLER.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        setAdapters();
                    }
                }, 2000);
            }
        });

        alert.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                User.getInstance(getActivity().getApplicationContext()).resolveFriendRequest(profileId, "deny");
                HANDLER.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        setAdapters();
                    }
                }, 2000);
            }
        });

        alert.show();
    }


    @Override
    public void onResume(){
        super.onResume();
        setAdapters();
    }

}