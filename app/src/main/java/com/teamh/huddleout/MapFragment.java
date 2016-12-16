package com.teamh.huddleout;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.util.ArrayList;

public class MapFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private GoogleMap myMap;
    private MapView mapView;

    public MapFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
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
        // Inflate the layout for this fragment
        FrameLayout v = (FrameLayout) inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.i("DevMsg", "Success!!!!!");
                myMap = googleMap;
                populateMap();
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

    private void populateMap(){
        try {
            String activityType = User.getInstance(this.getContext()).getGroupInFocus().getString("activity");
            Log.i("DevMsg", activityType);

            Resources res = getResources();
            ArrayList<String[]> coords = new ArrayList<>();
            String[] locationArray = new String[0];

            if(activityType.equals("Drinking")) {
                for(int j = 0; j < 2; j++){
                    switch (j) {
                        case 0:
                            locationArray = res.getStringArray(R.array.Bars);
                            break;
                        case 1:
                            locationArray = res.getStringArray((R.array.FastFood));
                            break;
                    }
                    for (int i = 0; i < locationArray.length; i += 3) {
                        coords.add(new String[]{locationArray[i], locationArray[i + 1], locationArray[i + 2]});
                    }
                    for (int i = 0; i < coords.size(); i++) {
                        myMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(coords.get(i)[1]), Double.parseDouble(coords.get(i)[2])))
                                .title(coords.get(i)[0]));
                        Log.i("DevMsg", coords.get(i)[0]);
                    }
                }
            }else if(activityType.equals("Social Meet")){
                for(int j = 0; j < 2; j++){
                    switch (j) {
                        case 0:
                            locationArray = res.getStringArray(R.array.Restaurants);
                            break;
                        case 1:
                            locationArray = res.getStringArray((R.array.Cafes));
                            break;
                    }
                    for (int i = 0; i < locationArray.length; i += 3) {
                        coords.add(new String[]{locationArray[i], locationArray[i + 1], locationArray[i + 2]});
                    }
                    for (int i = 0; i < coords.size(); i++) {
                        myMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(coords.get(i)[1]), Double.parseDouble(coords.get(i)[2])))
                                .title(coords.get(i)[0]));
                        Log.i("DevMsg", coords.get(i)[0]);
                    }
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

}
