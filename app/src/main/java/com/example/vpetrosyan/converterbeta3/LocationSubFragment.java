package com.example.vpetrosyan.converterbeta3;

import android.app.Activity;
import android.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Geocoder;
import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;

import java.util.ArrayList;

/**
 * Created by vpetrosyan on 06.05.2015.
 */
public class LocationSubFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {


    private RadioButton manuallyLocationSelection_;
    private Spinner countrySpinner;
    private SpinnerAdapter countyAdapter_;
    private SortedList countrylist_;
    final private UnitConverter converter = ConverterEngine.getUnitConverter();

    public LocationSubFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.currency_converter_location_sub_fragment, container, false);

        mResultReceiver = new AddressResultReceiver(new Handler());

        mLocationAddressTextView = (TextView)rootView.findViewById(R.id.location_address_view);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        currentLocBtn_ = (RadioButton)rootView.findViewById(R.id.currentLocBtnID);
        manuallyLocationSelection_ = (RadioButton)rootView.findViewById(R.id.useChooseLoc);

        spFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_container, spFragment);
        fragmentTransaction.commit();

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";

        currentMarker = null;

        setUpMapIfNeeded();

        updateValuesFromBundle(savedInstanceState);

        currentLocBtn_.setOnClickListener(currentLocBtnListener);
        manuallyLocationSelection_.setOnClickListener(manuallyLocationButtonListener_);

        countrySpinner = (Spinner)rootView.findViewById(R.id.countrySpinner);

        countrylist_ = new SortedList();

        ArrayList<String> tempCountry = converter.getSupportedCountries();

        for(int i = 0; i < tempCountry.size(); ++i)
        {
            countrylist_.add(tempCountry.get(i));
        }

        countrylist_.sort();

        countyAdapter_ = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,countrylist_);

        ((ArrayAdapter<String>) countyAdapter_).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        countrySpinner.setAdapter(countyAdapter_);

        ((ArrayAdapter<String>) countyAdapter_).notifyDataSetChanged();

        countrySpinner.setOnItemSelectedListener(spinnerItemSelected);

        updateButton_ = (Button) rootView.findViewById(R.id.cc_sub_loc_update);

        updateButton_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CONVERTER","UPDATE STARTED");
                converter.updateCurrencyRates(converter.getCurrencyCode(mAddressOutput));
            }
        });

        updateUIWidgets();

        if(mAddressOutput.isEmpty())
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            mAddressOutput = prefs.getString(Constants.CURRENT_LOCATION,"No Location");

            mLocationAddressTextView.setText(mAddressOutput);

            mLatitude = Double.valueOf(prefs.getString(Constants.CURRENT_LATITUDE_KEY,"0.0"));
            mLongitude = Double.valueOf(prefs.getString(Constants.CURRENT_LONGITUDE_KEY,"0.0"));
            if((mLatitude != 0) && (mLongitude != 0))
            {
                if(currentMarker != null) {
                    currentMarker.remove();
                }

                if(mMap != null)
                {
                    mMap.clear();
                }

                if(mMap != null) {
                    currentMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("Current Position"));
                }
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        SharedPreferences.Editor editor = prefs.edit();

        if(currentCountryPosition_ != -1)
        {
            editor.putInt(Constants.USER_CHOICE_POSITION_KEY,currentCountryPosition_);
        }

        editor.putBoolean(Constants.USER_CHOICE_KEY,useCurrentLocation_);

        editor.commit();
    }

    @Override
     public void onResume() {
        super.onResume();
        if(mMap == null) {
            setUpMapIfNeeded();
        }

        if(mMap != null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("Current Position"));
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        if(prefs.contains(Constants.USER_CHOICE_KEY))
        {
            useCurrentLocation_ = prefs.getBoolean(Constants.USER_CHOICE_KEY,true);

            if(useCurrentLocation_)
            {
                currentLocBtn_.setChecked(true);
                manuallyLocationSelection_.setChecked(false);
            }
            else
            {
                manuallyLocationSelection_.setChecked(true);
                currentLocBtn_.setChecked(false);
            }
        }
        else
        {
            useCurrentLocation_ = true;
        }

        if(useCurrentLocation_)
        {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
            countrySpinner.startAnimation(anim);
            countrySpinner.setVisibility(View.GONE);
            displayAddressOutput();
        }
        else
        {
            if(prefs.contains(Constants.USER_CHOICE_POSITION_KEY))
            {
                currentCountryPosition_ = prefs.getInt(Constants.USER_CHOICE_POSITION_KEY, -1);
                countrySpinner.setSelection(currentCountryPosition_);
            }

            Animation animforText = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeoutonly);
            mLocationAddressTextView.startAnimation(animforText);
            mLocationAddressTextView.setVisibility(View.GONE);
            countrySpinner.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
            countrySpinner.startAnimation(anim);
        }

        mAddressOutput = prefs.getString(Constants.CURRENT_LOCATION,"No Location");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        if (mMap != null) {
            setUpMap();
        }
    }

    SupportMapFragment spFragment;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private RadioButton currentLocBtn_;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double mLatitude;
    private double mLongitude;
    private boolean useCurrentLocation_;
    private int currentCountryPosition_;

    private Marker currentMarker;

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    protected static final String LATITUDE_KEY = "location-latitude";
    protected static final String LONGITUDE_KEY = "location-longitude";

    protected boolean mAddressRequested;
    protected String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    protected TextView mLocationAddressTextView;
    ProgressBar mProgressBar;
    private Button updateButton_;

    private Button.OnClickListener currentLocBtnListener = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            if (manuallyLocationSelection_.isChecked() && (!useCurrentLocation_)) {

                useCurrentLocation_ = true;
                currentLocBtn_.setChecked(true);
                manuallyLocationSelection_.setChecked(false);

                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
                countrySpinner.startAnimation(anim);
                countrySpinner.setVisibility(View.GONE);

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    mLatitude = mLastLocation.getLatitude();
                    mLongitude = mLastLocation.getLongitude();
                }

                if (mMap != null) {
                    mMap.clear();
                }

                if (currentMarker != null) {
                    currentMarker.remove();
                }
                currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("Current Position"));

                // We only start the service to fetch the address if GoogleApiClient is connected.
                if (mGoogleApiClient.isConnected() && mLastLocation != null) {
                    startIntentService();
                }
                // If GoogleApiClient isn't connected, we process the user's request by setting
                // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
                // fetch the address. As far as the user is concerned, pressing the Fetch Address button
                // immediately kicks off the process of getting the address.
                mAddressRequested = true;

                updateUIWidgets();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                mAddressOutput = prefs.getString(Constants.CURRENT_LOCATION,"No Location");

                displayAddressOutput();
            }
        }
    };

    private RadioButton.OnClickListener manuallyLocationButtonListener_ = new RadioButton.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            if(manuallyLocationSelection_.isChecked() && useCurrentLocation_)
            {
                useCurrentLocation_ = false;
                manuallyLocationSelection_.setChecked(true);
                currentLocBtn_.setChecked(false);
               if(currentCountryPosition_ != -1)
                {
                    mAddressOutput = countrylist_.get(currentCountryPosition_);

                }
                else
                {
                    mAddressOutput = getString(R.string.select_country_msg);
                }
                //displayAddressOutput();
                Animation animforText = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeoutonly);
                mLocationAddressTextView.startAnimation(animforText);
                mLocationAddressTextView.setVisibility(View.GONE);
                countrySpinner.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
                countrySpinner.startAnimation(anim);
            }
        }
    };

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }

            if(savedInstanceState.keySet().contains(LATITUDE_KEY))
            {
                mLatitude = savedInstanceState.getDouble(LATITUDE_KEY);

                if(savedInstanceState.keySet().contains(LONGITUDE_KEY))
                {
                    mLongitude = savedInstanceState.getDouble(LONGITUDE_KEY);

                    if(currentMarker != null) {
                        currentMarker.remove();
                    }

                    if(mMap != null)
                    {
                        mMap.clear();
                    }

                    currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("Current Position"));
                }
            }
        }
    }
    private Spinner.OnItemSelectedListener spinnerItemSelected = new Spinner.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(!useCurrentLocation_)
            {
                currentCountryPosition_ = position;
                mAddressOutput =  parent.getItemAtPosition(position).toString();
                //displayAddressOutput();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if(!useCurrentLocation_)
            {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

                mAddressOutput = prefs.getString(Constants.CURRENT_LOCATION,"No Location");
               // displayAddressOutput();
                currentCountryPosition_ = -1;
            }
        }
    };

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        spFragment.getMapAsync(this);
    }

    private void setUpMap() {
        if(currentMarker != null) {
            currentMarker.remove();
        }

        if(mMap != null)
        {
            mMap.clear();
        }

        if(mMap != null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("Current Position"));
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();

            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(getActivity(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
            // It is possible that the user presses the button to get the address before the
            // GoogleApiClient object successfully connects. In such a case, mAddressRequested
            // is set to true, but no attempt is made to fetch the address (see
            // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
            // user has requested an address, since we now have a connection to GoogleApiClient.
            if (mAddressRequested) {
                startIntentService();
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(),"Please wait",Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(),"Internet Error",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        if(mMap != null)
        {
            mMap.clear();
        }
    }

    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
    }

    protected void displayAddressOutput() {
        mLocationAddressTextView.setVisibility(View.VISIBLE);

        mLocationAddressTextView.setText(mAddressOutput);

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeinonly);
        mLocationAddressTextView.startAnimation(anim);
    }

    private void updateUIWidgets() {
        if (mAddressRequested) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            currentLocBtn_.setEnabled(false);
        } else {
            mProgressBar.setVisibility(ProgressBar.GONE);
            currentLocBtn_.setEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);

        savedInstanceState.putDouble(LATITUDE_KEY,mLatitude);
        savedInstanceState.putDouble(LONGITUDE_KEY,mLongitude);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(Constants.CURRENT_LOCATION,mAddressOutput);
            editor.putString(Constants.CURRENT_LATITUDE_KEY,String.valueOf(mLatitude));
            editor.putString(Constants.CURRENT_LONGITUDE_KEY,String.valueOf(mLongitude));
            editor.commit();

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }





}
