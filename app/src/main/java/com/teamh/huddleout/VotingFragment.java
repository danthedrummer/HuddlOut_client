package com.teamh.huddleout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.acl.Group;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Inflater;


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

    //Views for dialog
    private LayoutInflater thisInflater;
    private EditText voteCreateNameText;
    private EditText voteCreateDescText;
    private EditText voteCreateDurationText;
    private EditText voteCreateOption1Text;
    private EditText voteCreateOption2Text;
    private EditText voteCreateOption3Text;
    private EditText voteCreateOption4Text;

    //Current vote details
    private JSONArray voteJSONObject;
    private JSONObject currentVoteObject;
    private JSONArray voteOptions;
    private boolean voteExpired;

    //UI elements
    private RelativeLayout voteLayout;
    private RelativeLayout loadLayout;
    private RelativeLayout createVoteLayout;

    //Vote Layout elements
    private TextView voteNameText;
    private TextView voteDescText;
    private RadioGroup votingOptionsGroup;
    private RadioButton voteOption1Button;
    private RadioButton voteOption2Button;
    private RadioButton voteOption3Button;
    private RadioButton voteOption4Button;
    private TextView voteOption1Text;
    private TextView voteOption2Text;
    private TextView voteOption3Text;
    private TextView voteOption4Text;
    private TextView voteStatusText;
    private Button voteButton;
    private Button voteButtonHidden;

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
        thisInflater = inflater;

        //Get UI element instances
        voteLayout = (RelativeLayout)fLayout.findViewById(R.id.voteLayout);
        loadLayout = (RelativeLayout)fLayout.findViewById(R.id.loadLayout);
        createVoteLayout = (RelativeLayout)fLayout.findViewById(R.id.createVoteLayout);

        //Vote layout UI
        voteNameText = (TextView)fLayout.findViewById(R.id.voteName);
        voteDescText = (TextView)fLayout.findViewById(R.id.voteDescriptionBox);
        votingOptionsGroup = (RadioGroup)fLayout.findViewById(R.id.votingOptionsGroup);
        voteOption1Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton1);
        voteOption2Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton2);
        voteOption3Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton3);
        voteOption4Button = (RadioButton)fLayout.findViewById(R.id.voteOptionButton4);
        voteOption1Text = (TextView)fLayout.findViewById(R.id.voteOptionText1);
        voteOption2Text = (TextView)fLayout.findViewById(R.id.voteOptionText2);
        voteOption3Text = (TextView)fLayout.findViewById(R.id.voteOptionText3);
        voteOption4Text = (TextView)fLayout.findViewById(R.id.voteOptionText4);
        voteStatusText = (TextView)fLayout.findViewById(R.id.voteStatusText);
        voteButton = (Button)fLayout.findViewById(R.id.voteButton);
        voteButtonHidden = (Button)fLayout.findViewById(R.id.createVoteButtonHidden);

        voteButtonHidden.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        createVoteHandler(v);
                    }
                }
        );

        //Check for current votes
        initVotes();

        //Inflate the layout for this fragment
        return fLayout;
    }

    //Initialises the fragment
    private void initVotes() {
        swapToLayout(2);
        groupActivity = (GroupMenuActivity)getActivity();
        hAPI.getVotes(groupActivity.getGroupId(), this);
    }

    //Handler for vote creation
    private void createVoteHandler(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        final AlertDialog alertd = alert.create();

        alert.setTitle("Create New Vote");

        View dialogView = thisInflater.inflate(R.layout.vote_dialog_box, null);

        //Dialog UI
        voteCreateNameText = (EditText)dialogView.findViewById(R.id.voteNameEditText);
        voteCreateDescText = (EditText)dialogView.findViewById(R.id.voteDescEditText);
        voteCreateDurationText = (EditText)dialogView.findViewById(R.id.voteDurationEditText);
        voteCreateOption1Text = (EditText)dialogView.findViewById(R.id.voteOption1EditText);
        voteCreateOption2Text = (EditText)dialogView.findViewById(R.id.voteOption2EditText);
        voteCreateOption3Text = (EditText)dialogView.findViewById(R.id.voteOption3EditText);
        voteCreateOption4Text = (EditText)dialogView.findViewById(R.id.voteOption4EditText);

        alert.setView(dialogView);

        final VotingFragment votingFragment = this;

        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String name, desc, op1, op2, op3, op4;
                int duration = -1;

                if(voteCreateNameText.getText().toString().trim().length() == 0) {
                    name = null;
                } else {
                    name = voteCreateNameText.getText().toString();
                }

                if(voteCreateDescText.getText().toString().trim().length() == 0) {
                    desc = null;
                } else {
                    desc = voteCreateDescText.getText().toString();
                }

                if(voteCreateDurationText.getText().toString().trim().length() > 0) {
                    duration = Integer.parseInt(voteCreateDurationText.getText().toString());
                }

                if(voteCreateOption1Text.getText().toString().trim().length() == 0) {
                    op1 = null;
                } else {
                    op1 = voteCreateOption1Text.getText().toString();
                }

                if(voteCreateOption2Text.getText().toString().trim().length() == 0) {
                    op2 = null;
                } else {
                    op2 = voteCreateOption2Text.getText().toString();
                }

                if(voteCreateOption3Text.getText().toString().trim().length() == 0) {
                    op3 = null;
                } else {
                    op3 = voteCreateOption3Text.getText().toString();
                }

                if(voteCreateOption4Text.getText().toString().trim().length() == 0) {
                    op4 = null;
                } else {
                    op4 = voteCreateOption4Text.getText().toString();
                }

                hAPI.createVote(groupActivity.getGroupId(), name, desc, duration, op1, op2, op3, op4, votingFragment);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.cancel();
                alertd.dismiss();
            }
        });

        alert.show();
    }

    //Called by the HuddlOut API to update the votes
    public void setVotes(String voteJSONString) {
        Log.i(TAG, "Setting votes");

        //If there are no votes, display create page
        if(voteJSONString.equals("no votes")) {

            voteButton.setOnClickListener(
                    new Button.OnClickListener(){
                        public void onClick(View v){
                            createVoteHandler(v);
                        }
                    }
            );

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
            long voteCreationTimestamp = 0;
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

                        if(voteCreationTimestamp < creationDate.getTime()) {
                            voteCreationTimestamp = creationDate.getTime();
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
                voteOption1Text.setText("Votes: " + voteOptions.getJSONObject(0).getInt("count"));

                voteOption2Button.setText(voteOptions.getJSONObject(1).getString("name"));
                voteOption2Text.setText("Votes: " + voteOptions.getJSONObject(1).getInt("count"));

                if(voteOptions.length() < 3) {
                    voteOption3Button.setVisibility(View.GONE);
                    voteOption3Text.setVisibility(View.GONE);
                } else {
                    voteOption3Button.setVisibility(View.VISIBLE);
                    voteOption3Text.setVisibility(View.VISIBLE);
                    voteOption3Button.setText(voteOptions.getJSONObject(2).getString("name"));
                    voteOption3Text.setText("Votes: " + voteOptions.getJSONObject(2).getInt("count"));
                }

                if(voteOptions.length() < 4) {
                    voteOption4Button.setVisibility(View.GONE);
                    voteOption4Text.setVisibility(View.GONE);
                } else {
                    voteOption3Button.setVisibility(View.VISIBLE);
                    voteOption3Text.setVisibility(View.VISIBLE);
                    voteOption4Button.setVisibility(View.VISIBLE);
                    voteOption4Text.setVisibility(View.VISIBLE);
                    voteOption4Button.setText(voteOptions.getJSONObject(3).getString("name"));
                    voteOption4Text.setText("Votes: " + voteOptions.getJSONObject(3).getInt("count"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Check if the vote is open
            long currentTimestamp = System.currentTimeMillis();
            long expiryTimestamp;

            String expiryDateString;
            Date expiryDate;
            try {
                expiryDateString = currentVoteObject.getString("expiry_date").replaceAll("(T)", " ");
                expiryDateString = expiryDateString.replaceAll("(Z)", "");

                //Parse date
                try {
                    expiryDate = sqlDate.parse(expiryDateString);
                    expiryTimestamp = expiryDate.getTime();
                    Log.i(TAG, "Parsed Time: " + expiryDate.getTime());

                    if(expiryTimestamp > currentTimestamp) {
                        //Vote is open
                        voteStatusText.setText("The vote is currently open!\nClose Date: " + expiryDateString);
                        voteButton.setText("Cast Vote");
                        voteExpired = false;
                    } else {
                        //Vote is closed
                        voteStatusText.setText("The vote is currently closed!\nClose Date: " + expiryDateString);
                        voteButton.setText("Create New Vote");
                        voteExpired = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final VotingFragment thisFragment = this;

            //Setup vote action listeners
            voteButton.setOnClickListener(
                    new Button.OnClickListener(){
                        public void onClick(View v){
                            if(voteExpired) {
                                //Create new vote
                                createVoteHandler(v);
                            } else {
                                //Submit vote
                                int voteIndex = votingOptionsGroup.indexOfChild(getActivity().findViewById(votingOptionsGroup.getCheckedRadioButtonId()));
                                voteIndex = voteIndex / 2;

                                int voteId = -1;
                                try {
                                    voteId = voteOptions.getJSONObject(voteIndex).getInt("option_id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                hAPI.submitVote(voteId, thisFragment);
                            }
                        }
                    }
            );
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

    //HuddlOutAPI Callback
    public void submitVoteCallback(String response) {
        if(response.equals("update success")) {
            Popup.show("Vote Updated", this.getContext());
        } else if(response.equals("vote success")) {
            Popup.show("Vote Submitted", this.getContext());
        } else {
            Popup.show("Error: " + response.toUpperCase(), this.getContext());
        }
        initVotes();
    }

    //HuddlOut Callback
    public void createVoteCallback(String response) {
        Log.i(TAG, "res: " + response);

        if(response.equals("success")) {
            initVotes();
        } else {
            Popup.show("Error: " + response.toString().toUpperCase(), getContext());
            initVotes();
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
