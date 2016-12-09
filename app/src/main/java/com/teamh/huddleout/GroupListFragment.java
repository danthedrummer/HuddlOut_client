package com.teamh.huddleout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "DevMsg";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<JSONObject> groups;
    ArrayList<String> groupList;

    ArrayList<JSONObject> groupInvites;
    ArrayList<String> groupInviteList;

    ArrayAdapter<String> groupAdapter;
    ArrayAdapter<String> groupInviteAdapter;

    ArrayAdapter<String> groupActivityAdapter;

    ListView groupListView;
    ListView groupInviteListView;

    final Handler HANDLER = new Handler();

    private OnFragmentInteractionListener mListener;

    Context currentContext;

//    ######## PAUL REID THIS!!! ########
//    the getGroupsList() method in the user class will now return
//    an arrayList of JSONObjects, so we can just do the same thing
//    as the friendsList when populating.
//    The Key names for the JSON are:
//        group_id
//        group_name
//        activity
//    ###################################



    public GroupListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment GroupListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupListFragment newInstance() {
        GroupListFragment fragment = new GroupListFragment();
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

        groupList = new ArrayList<String>();
        groupInviteList = new ArrayList<String>();

        groupInviteList = new ArrayList<String>();

        currentContext = this.getActivity().getApplicationContext();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            HANDLER.postDelayed(new Runnable(){
                @Override
                public void run() {
                    try {
                        final User currentUser = User.getInstance(currentContext);
                        if (groupList.size() == 0) {
                            groups = currentUser.getGroupsList();
                            for (int i = 0; i < groups.size(); i++) {
                                try {
                                    groupList.add(groups.get(i).getString("group_name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.i(TAG, "groups:" + groupList.toString());
                            groupListView.setAdapter(groupAdapter);
                        }
                    } catch (NullPointerException e) {
                        Log.i(TAG, "Failed to user instance: " + e);
                    }
                }
            }, 1500);

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

        FrameLayout rellay = (FrameLayout) inflater.inflate(R.layout.fragment_group_list, container, false);

        groupAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, groupList);
        groupInviteAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, groupInviteList);

        groupListView = (ListView)rellay.findViewById(R.id.groupListView);
        groupInviteListView = (ListView)rellay.findViewById(R.id.groupInviteListView);

        ListUtils.setDynamicHeight(groupListView);
        ListUtils.setDynamicHeight(groupInviteListView);

        groupListView.setAdapter(groupAdapter);
        groupInviteListView.setAdapter(groupInviteAdapter);

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

//    @Override
//    public void onListItemClick(ListView l, View v, int id, long position) {
//        User currentUser = User.getInstance(this.getActivity().getApplicationContext());
//        try {
//            int groupId = groups.get(id).getInt("group_id");
//            currentUser.setGroupInFocus(groupId);
//            ((MainMenuActivity)getActivity()).openGroupMenu(groupId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        };
//    }


//    public void setAdapters(){
//        Log.i(TAG, "Setting Adapters");
//        final User currentUser = User.getInstance(this.getActivity().getApplicationContext());
//        HuddlOutAPI.getInstance(this.getActivity().getApplicationContext()).getFriends();
//        HuddlOutAPI.getInstance(this.getActivity().getApplicationContext()).getFriendRequests();
//
//        try {
//            friendList.clear();
//        }catch (IndexOutOfBoundsException e){
//            Log.i(TAG, "list clearance fail: " + e);
//        }
//
//        try {
//            friendRequestList.clear();
//        }catch (IndexOutOfBoundsException e){
//            Log.i(TAG, "list clearance fail: " + e);
//        }
//
//        friends = currentUser.getFriends();
//        friendRequests = currentUser.getFriendRequests();
//
//        for (int i = 0; i < friends.size(); i++) {
//            try {
//                friendList.add(friends.get(i).getString("first_name") + " " + friends.get(i).getString("last_name"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        for (int i = 0; i < friendRequests.size(); i++) {
//            try {
//                friendRequestList.add(friendRequests.get(i).getString("first_name") + " " + friendRequests.get(i).getString("last_name"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        friendsListView.setAdapter(friendAdapter);
//        friendsRequestListView.setAdapter(friendRequestAdapter);
//
//    }

}