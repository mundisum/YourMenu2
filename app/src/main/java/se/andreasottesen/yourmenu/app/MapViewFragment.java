package se.andreasottesen.yourmenu.app;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MapViewFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapClickListener {

    public static final int UPDATE_INTERVAL = 10000;
    public static final int FASTEST_INTERVAL = 5000;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private int mSectionNumber;

    private LocationRequest locationRequest;
    private GoogleMap googleMap;
    private LocationClient locationClient;
    private boolean updatesRequested;
    private Location currentLocation;

    private OnFragmentInteractionListener mListener;

    public static MapViewFragment newInstance(int sectionNumber) {
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }

        final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (result == ConnectionResult.SUCCESS){
            locationRequest = LocationRequest.create();
            locationClient = new LocationClient(getActivity(), this, this);

            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(UPDATE_INTERVAL);
            locationRequest.setFastestInterval(FASTEST_INTERVAL);

            updatesRequested = true;
        }
        else{
            Toast.makeText(getActivity(), "Google play services not available...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (googleMap == null) {
            SupportMapFragment sm = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapView);
            googleMap = sm.getMap();
            googleMap.setOnMapClickListener(this);
            googleMap.setMyLocationEnabled(true);
        }

        if (!locationClient.isConnected()){
            locationClient.connect();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();

        if (updatesRequested){
            locationClient.requestLocationUpdates(locationRequest, this);
        }

        currentLocation = locationClient.getLastLocation();
        Log.d("Location", currentLocation.toString());

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 16));

        Geocoder geocoder = new Geocoder(getActivity());

        try {
            List<Address> result = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            Toast.makeText(getActivity(), addressToText(result.get(0)), Toast.LENGTH_LONG).show();
            //addMarker(currentLocation);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;

        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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
        public void onFragmentInteraction(Uri uri);
    }

    private void addMarker(Location location){
        if (googleMap != null){
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

            googleMap.clear();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));

            googleMap.addMarker(new MarkerOptions()
                            .position(currentPosition)
                            .title("Marker")
                            .draggable(true)
                            .snippet("Lat:" + location.getLatitude() + "Long:" + location.getLongitude())
            );
        }
    }

    private void addMarker(LatLng latLng){
        if (googleMap != null){
            LatLng currentPosition = latLng;

            googleMap.clear();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));

            googleMap.addMarker(new MarkerOptions()
                            .position(currentPosition)
                            .title("Marker")
                            .draggable(true)
                            .snippet("Lat:" +latLng.latitude + "Long:" + latLng.longitude)
            );
        }
    }

    private CharSequence addressToText(Address address) {
        final StringBuilder addressText = new StringBuilder();
        for (int i = 0, max = address.getMaxAddressLineIndex(); i < max; ++i) {
            addressText.append(address.getAddressLine(i));
            if ((i+1) < max) {
                addressText.append(", ");
            }
        }
        addressText.append(", ");
        addressText.append(address.getCountryName());
        return addressText;
    }
}
