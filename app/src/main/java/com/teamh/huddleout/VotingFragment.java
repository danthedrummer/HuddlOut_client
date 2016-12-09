package com.teamh.huddleout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VotingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VotingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VotingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "DevMsg";

    private OnFragmentInteractionListener mListener;

    //Group Activity instance
    GroupMenuActivity groupActivity;

    //HuddlOut API
    private HuddlOutAPI hAPI = HuddlOutAPI.getInstance(this.getActivity());

    //Current vote details
    private JSONArray voteJSONObject;
    private JSONObject currentVoteObject;
    private JSONArray voteOptions;

    //UI elements
    private RelativeLayout voteLayout;
    private RelativeLayout loadLayout;
    private RelativeLayout createVoteLayout;

    //Vote Layout elements
    private TextView voteNameText;
    private TextView voteDescText;
    private RadioButton voteOption1Button;
    private RadioButton voteOption2Button;
    private RadioButton voteOption3Button;
    private RadioButton voteOption4Button;

    public VotingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VotingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VotingFragment newInstance() {
        VotingFragment fragment = new VotingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout fLayout = (FrameLayout)inflater.inflate(R.layout.fragment_voting, container, false);

        //Get UI element instances
        voteLayout = (RelativeLayout)fLayout.findViewById(R.id.voteLayout);
        loadLayout = (RelativeLayout)fLayout.findViewById(R.id.loadLayout);
        createVoteLayout = (RelativeLayout)fLayout.findViewById(R.id.createVoteLayout);

        //Vote layout UI
        voteNameText = (TextView)fLayout.findViewById(R.id.voteName);
        voteDescText = (TextView)fLayout.findViewById(R.id.voteDescriptionBox);
        voteOption1Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton1);
        voteOption2Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton2);
        voteOption3Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton3);
        voteOption4Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton4);

        //Check for current votes
        groupActivity = (GroupMenuActivity)getActivity();
        hAPI.getVotes(groupActivity.getGroupId(), this);

        //Inflate the layout for this fragment
        return fLayout;
    }

    //Called by the HuddlOut API to update the votes
    public void setVotes(String voteJSONString) {
        Log.i(TAG, "Setting votes");

        //If there are no votes, display create page
        if(voteJSONString.equals("no votes")) {
            swapToLayout(1);
        } else {
            try{
                voteJSONObject = new JSONArray(voteJSONString);
                Log.i(TAG, voteJSONObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Look for the newest vote
            SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long timestamp = 0;
            for (int i = 0; i < voteJSONObject.length(); i++) {
                try {
                    JSONObject vote = (JSONObject) voteJSONObject.get(i);

                    //Format date string
                    String dateString = vote.getString("creation_date").replaceAll("(T)", " ");
                    dateString = dateString.replaceAll("(Z)", "");

                    //Parse date
                    try {
                        Date creationDate = sqlDate.parse(dateString);
                        Log.i(TAG, "Parsed Time: " + creationDate.getTime());

                        if(timestamp < creationDate.getTime()) {
                            timestamp = creationDate.getTime();
                            currentVoteObject = vote;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                voteOptions = currentVoteObject.getJSONArray("options");

                Log.i(TAG, "Length: " + voteOptions.length());

                voteNameText.setText(currentVoteObject.getString("name"));
                voteDescText.setText(currentVoteObject.getString("description"));

                //Populate options
                voteOption1Button.setText(voteOptions.getJSONObject(0).getString("name"));
                voteOption2Button.setText(voteOptions.getJSONObject(1).getString("name"));

                if(voteOptions.length() < 3) {
                    voteOption3Button.setVisibility(View.GONE);
                } else {
                    voteOption3Button.setText(voteOptions.getJSONObject(2).getString("name"));
                }

                if(voteOptions.length() < 4) {
                    voteOption4Button.setVisibility(View.GONE);
                } else {
                    voteOption4Button.setText(voteOptions.getJSONObject(3).getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            swapToLayout(0);
        }
    }

    //Swaps the relative layouts of the vote fragment (0 = Vote, 1 = Create, 2 = Load)
    private void swapToLayout(int layout) {
        switch(layout) {
            case 0:
                voteLayout.setVisibility(View.VISIBLE);
                createVoteLayout.setVisibility(View.GONE);
                loadLayout.setVisibility(View.GONE);
                break;
            case 1:
                voteLayout.setVisibility(View.GONE);
                createVoteLayout.setVisibility(View.VISIBLE);
                loadLayout.setVisibility(View.GONE);
                break;
            case 2:
                voteLayout.setVisibility(View.GONE);
                createVoteLayout.setVisibility(View.GONE);
                loadLayout.setVisibility(View.VISIBLE);
                break;
            default:
                Log.i(TAG, "SWAP TO LAYOUT only works with an input range between 0 and 2");
        }
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
