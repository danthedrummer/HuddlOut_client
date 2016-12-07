package com.teamh.huddleout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "DevMsg";

    TextView nameTextView;
    TextView aboutTextView;
    TextView aboutContentTextView;
    TextView preferencesTextView;
    TextView preferencesContentTextView;
    Button topContextButton;

    ImageView profilePicture;

    Context currentContext;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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

        final ImageView profileImageView = (ImageView) getActivity().findViewById(R.id.profileImageView);

        final HuddlOutAPI hAPI = HuddlOutAPI.getInstance(this.getActivity());

        currentContext = this.getActivity().getApplicationContext();

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        Log.i(TAG, "profile visible: " + isVisibleToUser);

        if (isVisibleToUser) {
            try{
                final User currentUser = User.getInstance(currentContext);
                int id = currentContext.getResources().getIdentifier(currentUser.getProfilePicture(), "drawable", "com.teamh.huddleout");
                Log.i(TAG, "id: " + id);
                Log.i(TAG, "currentUser.getName: " + currentUser.getName());

                nameTextView.setText(currentUser.getName());
                aboutContentTextView.setText(currentUser.getDescription());
                profilePicture.setImageResource(id);
            }catch(NullPointerException e){
                Log.i(TAG, "Null pointer reference on profile fragment: " + e);
            }
//            Log.i(TAG, "");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout rellay = (FrameLayout) inflater.inflate(R.layout.fragment_profile, container, false);

        nameTextView = (TextView)rellay.findViewById(R.id.nameTextView);
        aboutTextView = (TextView)rellay.findViewById(R.id.aboutTextView);
        aboutContentTextView = (TextView)rellay.findViewById(R.id.aboutContentTextView);
        topContextButton = (Button)rellay.findViewById(R.id.topContextButton);
        profilePicture = (ImageView)rellay.findViewById(R.id.profileImageView);

        try{
            final User currentUser = User.getInstance(currentContext);
            int id = currentContext.getResources().getIdentifier("chess", "drawable", "com.teamh.huddleout");
            nameTextView.setText(currentUser.getName());
            aboutContentTextView.setText(currentUser.getDescription());
            profilePicture.setImageResource(id);
        }catch(NullPointerException e){
            Log.i(TAG, "Null pointer reference on profile fragment: " + e);
        }

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

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
