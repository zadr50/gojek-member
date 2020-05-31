package com.talagasoft.gojek.libs;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.talagasoft.gojek.adapter.AutoCompleteAdapter;
import com.talagasoft.gojek.MapsActivity;
import com.talagasoft.gojek.R;

public class PlaceFind extends  FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;

    private int PLACE_PICKER_REQUEST = 1;
    private AutoCompleteAdapter mAdapter;

    private TextView mTextView;
    private AutoCompleteTextView mPredictTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_find);
        mTextView = (TextView) findViewById( R.id.textview );

        mPredictTextView = (AutoCompleteTextView) findViewById( R.id.txtSearch);
        mAdapter = new AutoCompleteAdapter( this );
        mPredictTextView.setAdapter( mAdapter );

        mPredictTextView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoCompletePlace place = (AutoCompletePlace) parent.getItemAtPosition( position );
                findPlaceById( place.getId() );
            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder( this )
                .enableAutoManage( this, 0, this )
                .addApi( Places.GEO_DATA_API )
                .addApi( Places.PLACE_DETECTION_API )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mAdapter.setGoogleApiClient( null );
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void findPlaceById( String id ) {
        if( TextUtils.isEmpty( id ) || mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
            return;

        Places.GeoDataApi.getPlaceById( mGoogleApiClient, id ) .setResultCallback( new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if( places.getStatus().isSuccess() ) {
                    Place place = places.get( 0 );
                    displayPlace( place );
                    mPredictTextView.setText( "" );
                    mAdapter.clear();
                }

                //Release the PlaceBuffer to prevent a memory leak
                places.release();
            }
        } );
    }

    private void displayPlace( Place place ) {
        if( place == null )
            return;

        String content = "";
        if( !TextUtils.isEmpty( place.getName() ) ) {
            content += "Name: " + place.getName() + "\n";
        }
        if( !TextUtils.isEmpty( place.getAddress() ) ) {
            content += "Address: " + place.getAddress() + "\n";
        }
        if( !TextUtils.isEmpty( place.getPhoneNumber() ) ) {
            content += "Phone: " + place.getPhoneNumber();
        }
        if( !TextUtils.isEmpty( place.getLatLng().toString() ) ) {
            content += "LatLng: " + place.getLatLng().toString() + "\n";
        }
        mTextView.setText( content );

        Bundle mBundle;
        mBundle = new Bundle();
        mBundle.putDouble("Latitude", place.getLatLng().latitude);
        mBundle.putDouble("Longitude", place.getLatLng().longitude);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Intent mIntent = new Intent(this, MapsActivity.class);
            mIntent.putExtras(mBundle);
            startActivity(mIntent);
            finish();
        }

    }
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK ) {
            displayPlace( PlacePicker.getPlace( data, this ) );
        }
    }
    private void guessCurrentPlace() {
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace( mGoogleApiClient, null );
        result.setResultCallback( new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult( PlaceLikelihoodBuffer likelyPlaces ) {

                PlaceLikelihood placeLikelihood = likelyPlaces.get( 0 );
                String content = "";
                if( placeLikelihood != null && placeLikelihood.getPlace() != null && !TextUtils.isEmpty( placeLikelihood.getPlace().getName() ) )
                    content = "Most likely place: " + placeLikelihood.getPlace().getName() + "\n";
                if( placeLikelihood != null )
                    content += "Percent change of being there: " + (int) ( placeLikelihood.getLikelihood() * 100 ) + "%";
                mTextView.setText( content );

                likelyPlaces.release();
            }
        });
    }

    private void displayPlacePicker() {
        if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult( builder.build((Activity) getApplicationContext()), PLACE_PICKER_REQUEST );
        } catch ( GooglePlayServicesRepairableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
        } catch ( GooglePlayServicesNotAvailableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if( mAdapter != null )
            mAdapter.setGoogleApiClient( mGoogleApiClient );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
