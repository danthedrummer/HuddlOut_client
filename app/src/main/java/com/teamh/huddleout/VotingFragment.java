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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;


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
    private int currentVoteIndex;

    //UI elements
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
        voteNameText = (TextView)fLayout.findViewById(R.id.voteName);
        voteDescText = (TextView)fLayout.findViewById(R.id.voteDescriptionBox);
        voteOption1Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton1);
        voteOption2Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton2);
        voteOption3Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton3);
        voteOption4Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton4);

        //Set UI elements to be invisible
        voteNameText.setVisibility(View.INVISIBLE);
        voteDescText.setVisibility(View.INVISIBLE);
        voteOption1Button.setVisibility(View.INVISIBLE);
        voteOption2Button.setVisibility(View.INVISIBLE);
        voteOption3Button.setVisibility(View.INVISIBLE);
        voteOption4Button.setVisibility(View.INVISIBLE);

        //Check for current votes
        groupActivity = (GroupMenuActivity)getActivity();
        hAPI.getVotes(groupActivity.getGroupId(), this);

        //Inflate the layout for this fragment
        return fLayout;
    }

    public void setVotes(String voteJSONString) {
        Log.i(TAG, "Hold my drink, doing some mad shit over here!");

        try{
            voteJSONObject = new JSONArray(voteJSONString);
            Log.i(TAG, voteJSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < voteJSONObject.length(); i++) {
            try {
                JSONObject vote = (JSONObject) voteJSONObject.get(i);
                Log.i("Dev", vote.getString("creation_date"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        voteNameText.setText("TITEL");
        voteDescText.setText("DO YEW VANT TO ARRRRRRRRRRRRRRRRRRR PI?");
        voteOption1Button.setText("YES");
        voteOption2Button.setText("YES");
        voteOption3Button.setText("YES");
        voteOption4Button.setText("YES");

        voteNameText.setVisibility(View.VISIBLE);
        voteDescText.setVisibility(View.VISIBLE);
        voteOption1Button.setVisibility(View.VISIBLE);
        voteOption2Button.setVisibility(View.VISIBLE);
        voteOption3Button.setVisibility(View.VISIBLE);
        voteOption4Button.setVisibility(View.VISIBLE);
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
