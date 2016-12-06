package com.teamh.huddleout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

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

    ArrayAdapter<String> groupAdapter;



    private OnFragmentInteractionListener mListener;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_list, container, false);

        FrameLayout rellay = (FrameLayout) inflater.inflate(R.layout.fragment_friend_list, container, false);
        groupAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, groupList);

        ImageButton groupButton1 = (ImageButton)v.findViewById(R.id.memberButton1);
        ImageButton groupButton2 = (ImageButton)v.findViewById(R.id.groupButton2);
        ImageButton groupButton3 = (ImageButton)v.findViewById(R.id.groupButton3);
        ImageButton newGroupButton = (ImageButton)v.findViewById(R.id.groupButton4);

        groupButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySwap.swapToNextActivity(getActivity(), GroupMenuActivity.class);
            }
        });

        groupButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySwap.swapToNextActivity(getActivity(), GroupMenuActivity.class);
            }
        });

        groupButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySwap.swapToNextActivity(getActivity(), GroupMenuActivity.class);
            }
        });

        newGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setTitle("Create New Group");
                alert.setMessage("Enter group name:");

                // Set an EditText view to get user input
                final EditText input = new EditText(getContext());
                alert.setView(input);

                alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        System.out.println(input.getText());
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });

        return v;
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
}