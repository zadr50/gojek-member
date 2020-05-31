package com.talagasoft.gojek;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.talagasoft.gojek.adapter.AutoCompleteAdapter;
import com.talagasoft.gojek.adapter.PopupAdapter;
import com.talagasoft.gojek.libs.AutoCompletePlace;
import com.talagasoft.gojek.libs.GMapV2Direction;
import com.talagasoft.gojek.model.Driver;
import com.talagasoft.gojek.model.HttpXml;
import com.talagasoft.gojek.model.User;

import org.w3c.dom.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends AbstractMapActivity
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        LocationListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMapClickListener {

    // global variabel

    String TAG = "MapsActivity";
    AdView mAdView;
    SharedPreferences mSetting;
    boolean needsInit = false;

    // gooogle api
    Location location;
    LocationManager locationManager;
    GoogleMap map;
    Criteria criteria;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    String mWebsite = "", mJenis = "";

    // penumpang variabel
    String mNomorHp, mNama, mPlaceTujuanText;
    float mToLat, mToLng;
    LatLng myLatLng;
    float myLat;
    float myLng;
    private AutoCompleteAdapter mAdapter;
    private AutoCompleteTextView mPredictTextView;
    TextView mJarak, mAsal, mTujuan, mOngkos, mDeposit, txtMyLocation, txtTujuan, txtCatatan;
    int mOngkosVal = 0, mDepositVal = 0, mTarif = 0;
    PolylineOptions mPolyline;
    private Place mPlaceTujuan;
    private float mJarakVal;
    private boolean mWaitingDriver;
    MarkerOptions markerPenumpang;

    // driver variabel
    Bitmap mIconDriver, mIconDriverAccept, mIconPenumpang;
    List<Driver> arrDriver = new ArrayList<Driver>();
    private boolean mDriverLagiNgantar;
    LinearLayout divDriver;
    TextView txtDriverName, txtDriverHp;
    String mDriverName, mDriverHandphone;

    // handle timer schedule mulai jalan ke tujuan
    TimerTask _task;
    final Handler _handler = new Handler();
    Timer _timer;

    int mMode = 0, MODE_CARI_AlAMAT = 0, MODE_WAIT_DRIVER = 1, MODE_ANTAR_BERSAMA = 2;
    //0 - lagi cari alamat tujuan, 1 - nunggu driver, 2 - lagi jalan sama driver cie-ciee
    // controls
    Button btnCall, btnStart, btnStop, btnRate, btnChat;
    Button btnTopUp, btnSubmit, btnRefresh;
    Button btnClikOnMap, btnSearchInput;
    LinearLayout divSearch, divResult;

    private User mMember, mDriver;
    private boolean mFoundMyLocation = false;
    private boolean mModeClikOnMap = false;
    private PlacePicker.IntentBuilder builder;
    private Activity intent;


    @Override
    public void onAttachFragment(Fragment fragment) {

        super.onAttachFragment(fragment);
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    @Override
    public void onMapReady(final GoogleMap map1) {
        this.map = map1;
        map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        map.setOnInfoWindowClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        intent = this;

        myLocation();
        if (myLatLng == null) {
            Toast.makeText(getBaseContext(), "GPS Not Active !", Toast.LENGTH_LONG).show();
            return;

        }

        mAdapter.setRadiusPlace(1, myLatLng); //set radius 10 km dari sekarang

        if (needsInit && myLatLng != null) {
            needsInit = false;
            CameraUpdate center = CameraUpdateFactory.newLatLng(myLatLng);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);
            map.moveCamera(center);
            map.animateCamera(zoom);

        }
        if (mMode == MODE_WAIT_DRIVER) {
            //divSearch.setVisibility(View.GONE);
            mWaitingDriver = true;
            //btnClikOnMap.setVisibility(View.GONE);
            //btnSearchInput.setVisibility(View.GONE);
        }

        moveCameraTo(map, myLatLng);

        addMarkerMe(map, myLatLng.latitude, myLatLng.longitude,
                mMember.get_user_name(),
                mMember.get_handphone());

        if (mToLat != 0) {
            addMarkerTujuan();
            DrawRoute(myLatLng, new LatLng(mToLat, mToLng));
        }
        addMarkerDrivers();

        //startTimer();
    }

    private double getRadius(int inKm) {
        double latDistance = Math.toRadians(myLatLng.latitude - inKm);
        double lngDistance = Math.toRadians(myLatLng.longitude - inKm);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(myLatLng.latitude))) *
                        (Math.cos(Math.toRadians(inKm))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = 6371 / c;
        if (dist < 50) {
            /* Include your code here to display your records */
        }
        return dist;

    }

    private void getDriverPosition() {
        if (mDriver != null) {
            mDriver.loadByPhoneJob(mDriver.get_handphone(), "driver");
        }
    }

    private void getDriverCurrentLocation() {
        if (myLatLng == null) return;
        arrDriver.clear();
        String mUrl = mWebsite + "drivers.php?hp=" + mNomorHp + "&lat=" + myLatLng.latitude + "&lng=" + myLatLng.longitude;
        if (mJenis.equals("car")) {
            mUrl = mWebsite + "drivers_car.php?hp=" + mNomorHp + "&lat=" + myLatLng.latitude + "&lng=" + myLatLng.longitude;
        }
        HttpXml web = new HttpXml(mUrl);
        web.getGroup("people");
        float lat, lng;
        String hp, nama, driverHp;
        int status;
        for (int i = 0; i < web.getCount(); i++) {
            lat = Float.parseFloat(web.getKeyIndex(i, "lat"));
            lng = Float.parseFloat(web.getKeyIndex(i, "lng"));
            hp = web.getKeyIndex(i, "handphone");
            nama = web.getKeyIndex(i, "user_name");
            status = Integer.parseInt(web.getKeyIndex(i, "status"));
            driverHp = web.getKeyIndex(i, "driver");

            Driver drv = new Driver(this);
            drv.set_user_name(nama);
            drv.set_handphone(hp);
            drv.set_lng(lng);
            drv.set_lat(lat);
            arrDriver.add(drv);
            if (mDriver != null) {
                // saya gak ngerti kenapa bisa ada driver tapi nomor hp penumpangnya gak ada
                // mungkin gagal waktu closing oleh driver
                if (mDriver.get_user_name() != null) {
                    if (mDriver.get_user_name().contains(nama)) {
                        driverHp = mNomorHp;
                        status = MODE_ANTAR_BERSAMA;
                    }
                }
            }

            if (driverHp.contains(mNomorHp) && status == MODE_ANTAR_BERSAMA) {
                //2 - penumpang di accept oleh driver
                // mDriver=new User(this);
                //mDriver.loadByPhoneJob(hp,"driver");
                //save to setting tujuan untuk menghindari buffer kosong
                SharedPreferences.Editor editor = mSetting.edit();
                editor.putString("driver_handphone", hp);
                editor.putString("driver_name", nama);
                editor.commit();
            }

        }
    }

    public void addMarkerDrivers() {
        for (int i = 0; i < arrDriver.size(); i++) {
            Driver driver = arrDriver.get(i);
            addMarkerDriver(map, driver.get_lat(), driver.get_lng(),
                    driver.get_user_name(), driver.get_handphone());
        }
    }

    public void myLocation() {

        getLocation();

        if (location == null) {
            Toast.makeText(getBaseContext(), "GPS Not Active !", Toast.LENGTH_LONG).show();
        } else {
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            String address = AddressFromLatLng(myLatLng);
            txtMyLocation.setText(address);
            addMarkerMe(map, myLatLng.latitude, myLatLng.longitude,
                    mMember.get_user_name(),
                    mMember.get_handphone());
            Log.d("myLocation", "Lat/Lng:" + myLatLng.latitude + "/" + myLatLng.longitude);
            mFoundMyLocation = true;
        }

    }

    private void DrawRoutePoly() {
        if (mPolyline == null) return;
        Polyline line = map.addPolyline(mPolyline);
    }

    public void DrawRoute(LatLng from, LatLng to) {

        float mDistance = getDistanceKm(from, to);

        Document document = null;
        try {
            document = new GMapV2Direction().getDocument(from, to, "drive");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (document == null) {
            Toast.makeText(getBaseContext(), "Unable DrawRoute", Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<LatLng> oLat = new GMapV2Direction().getDirection(document);

        mPolyline = new PolylineOptions();
        for (int i = 0; i < oLat.size(); i++) {
            mPolyline.add(oLat.get(i));
        }
        mPolyline.width(5);
        mPolyline.color(Color.BLUE);

        DrawRoutePoly();

        DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"
        mJarakVal = mDistance;
        mJarak.setText(df.format(mJarakVal) + " KM");
        int o = (int) (mDistance * mTarif);   //ongkos 1000 tiap kilometer
        mOngkos.setText(df.format(o));
        mOngkosVal = o;

    }

    public float getDistanceKm(LatLng my_latlong, LatLng frnd_latlong) {
        float distance = getDistanceFloat(my_latlong, frnd_latlong);
        if (distance > 1000.0f) {
            distance = distance / 1000.0f;
        } else {
            distance = 1.0f;
        }
        return distance;
    }

    public float getDistanceFloat(LatLng my_latlong, LatLng frnd_latlong) {
        Location l1 = new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2 = new Location("Two");
        l2.setLatitude(frnd_latlong.latitude);
        l2.setLongitude(frnd_latlong.longitude);

        float distance = l1.distanceTo(l2);
        return distance;
    }

    private void moveCameraTo(GoogleMap mMap, LatLng currentLocation) {
        if (currentLocation == null) return;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle() + " - " + marker.getSnippet(), Toast.LENGTH_LONG).show();
    }

    private void addMarkerMe(GoogleMap map, double lat, double lon,
                             String title, String snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(mIconPenumpang))
                .snippet(snippet));
    }

    private void addMarkerDriver(GoogleMap map, double lat, double lon,
                                 String title, String snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(mIconDriver))
                .snippet(snippet));
    }

    private void addMarkerDriverAccept(GoogleMap map, double lat, double lon,
                                       String title, String snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(mIconDriverAccept))
//                .draggable(true)
                .snippet(snippet));
    }

    private void addMarkerTo(GoogleMap map, double lat, double lon,
                             String title, String snippet) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(title)
                //.icon(BitmapDescriptorFactory.fromBitmap(R.drawable.places_ic_search))
                .draggable(true)
                .snippet(snippet));
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(getClass().getSimpleName(),
                String.format("%f:%f", location.getLatitude(),
                        location.getLongitude()));

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mAdapter != null)
            mAdapter.setGoogleApiClient(mGoogleApiClient);


        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPause() {
        super.onPause();
        ////Toast.makeText(getApplicationContext(),"14. onPause()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //'Toast.makeText(getApplicationContext(),"16. onDestroy()", Toast.LENGTH_SHORT).show();
        stopTimer();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        mWebsite = getResources().getString(R.string.url_source);
        //google ads
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1869511402643723~8811604696");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        initControls();
        builder = new PlacePicker.IntentBuilder();

        mJenis = "bike";
        if (getIntent().hasExtra("jenis")) {
            mJenis = getIntent().getStringExtra("jenis");
        }
        mIconPenumpang = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.penumpang);
        if (mJenis.equals("car")) {
            mIconDriver = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.driver_car);
            mIconDriverAccept = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.driver_accept_car);

        } else {
            mIconDriver = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.driver);
            mIconDriverAccept = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.driver_accept);

        }
        mWaitingDriver = false;
        mDriver = null;
        mMember = null;

        stopTimer();

        btnChat = (Button) findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat();
            }
        });

        mSetting = getSharedPreferences("setting_gojek", Context.MODE_PRIVATE);

        mNama = mSetting.getString("nama", "Guest");
        mNomorHp = mSetting.getString("no_hp", "0000000000");
        mDriverName = mSetting.getString("driver_name", "");
        mDriverHandphone = mSetting.getString("driver_handphone", "");
        mToLat = mSetting.getFloat("tujuan_lat", 0);
        mToLng = mSetting.getFloat("tujuan_lng", 0);
        myLat = mSetting.getFloat("my_lat", 0);
        myLng = mSetting.getFloat("my_lng", 0);
        myLatLng = new LatLng(myLat, myLng);

        mPlaceTujuanText = mSetting.getString("tujuan_name", "");
        if (mToLat == 0) {
            reset();
        }

        mMember = new User(this);
        mTarif = mSetting.getInt("tarif", 5000);

        mDepositVal = Integer.parseInt(mSetting.getString("deposit", "0"));
        DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"
        mDeposit.setText(df.format(mDepositVal));
        mMode = mSetting.getInt("mode", MODE_CARI_AlAMAT);

        divResult.setVisibility(View.GONE);

        if (mDriver != null) {
            // driver accepted
            txtDriverHp.setText("Posisi: " + mDriver.get_lat() + "/" + mDriver.get_lng());
            txtDriverName.setText("Nama: " + mDriver.get_user_name() + ", Hp: " + mDriver.get_handphone());
            divDriver.setVisibility(View.VISIBLE);

        }

        if (readyToGo()) {

            mPredictTextView = (AutoCompleteTextView) findViewById(R.id.txtSearch);

            mAdapter = new AutoCompleteAdapter(this);
            mPredictTextView.setAdapter(mAdapter);
            if (mToLat != 0) {
                mPredictTextView.setText(AddressFromLatLng(new LatLng(mToLat, mToLng)));
            }

            MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

            mapFrag.getMapAsync(this);
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            needsInit = true;


        }
        onClickControls();

        //get data from server thread
        new getServerDataAsync().execute();


    }

    private void onClickControls() {
        if (!mWaitingDriver) {
            mPredictTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AutoCompletePlace place = (AutoCompletePlace) parent.getItemAtPosition(position);
                    findPlaceById(place.getId());
                }
            });
        }
        btnTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topUpSaldo();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTujuan();
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshMap();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhone();
            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAntar();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAntar();
            }
        });
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateDriver();
            }
        });
        btnClikOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mModeClikOnMap = !mModeClikOnMap;
                try {

                    startActivityForResult(builder.build(intent), PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
        btnSearchInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtTujuan.getVisibility() == View.GONE) {
                    txtTujuan.setVisibility(View.VISIBLE);
                } else {
                    txtTujuan.setVisibility(View.GONE);
                }
            }
        });

    }

    private void startAntar() {

        submitTujuan();
    }

    private void stopAntar() {
        // if(mDriver!=null){
        //     Toast.makeText(this,"Sudah ada driver yang accept, tujuan anda tidak bisa dibatalkan.",
        //             Toast.LENGTH_LONG).show();
        //     return;
        //}
        Toast.makeText(this, "Tujuan anda sudah dibatalkan.", Toast.LENGTH_LONG).show();
        reset();
        stopTimer();
        finish();

    }

    private void topUpSaldo() {
        Intent intent = new Intent("DepositActivity");
        intent.putExtra("no_hp", mNomorHp);
        intent.putExtra("nama", mNama);
        startActivity(intent);
    }

    private void callPhone() {
        if (mDriver == null) {
            Toast.makeText(getBaseContext(), "Belum ada driver yang accept untuk mulai chating!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mDriver.get_handphone()));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    private void startChat() {
        if (mDriver == null) {
            Toast.makeText(getBaseContext(), "Belum ada driver yang accept untuk mulai chating!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(new Intent("ChatActivity"));

    }

    private void submitTujuan() {

        if (mDepositVal < mOngkosVal) {
            //Toast.makeText(getBaseContext(),"Deposit tidak mencukupi ! Silahkan lakukan pembayaran ke pengemudi.",Toast.LENGTH_LONG).show();
            //return;
        }
        if (SubmitOrder()) {
            divSearch.setVisibility(View.GONE);
            divResult.setVisibility(View.GONE);
            mWaitingDriver = true;
            mMode = MODE_WAIT_DRIVER;        //waiting driver
            //save to setting tujuan untuk menghindari buffer kosong
            SharedPreferences.Editor editor = mSetting.edit();
            editor.putInt("mode", mMode);
            editor.putFloat("tujuan_lat", mToLat);
            editor.putFloat("tujuan_lng", mToLng);
            editor.putString("tujuan_name", mPlaceTujuanText);
            editor.commit();
            //startTimer();

            Toast.makeText(getBaseContext(), "Order sudah masuk, tunggu respon dari driver disekitar anda.",
                    Toast.LENGTH_LONG).show();
            finish();
            Intent intent = new Intent("WaitingDriver");
            intent.putExtra("no_hp", mNomorHp);
            intent.putExtra("nama", mNama);
            startActivity(intent);
            txtTujuan.setText(mPlaceTujuanText);

        } else {
            Toast.makeText(getBaseContext(), "Gagal submit order anda, coba lagi nanti", Toast.LENGTH_LONG).show();
        }

    }

    private void refreshMap() {
        map.clear();
        if (mWaitingDriver) {
            addMarkerMe(map, myLatLng.latitude, myLatLng.longitude,
                    mMember.get_user_name(),
                    mMember.get_handphone());
            addMarkerTujuan();
            DrawRoutePoly();
        }
        getDriverCurrentLocation();
        addMarkerDrivers();
    }

    private void initControls() {

        divSearch = (LinearLayout) findViewById(R.id.divSearch);
        divResult = (LinearLayout) findViewById(R.id.section_result);

        mJarak = (TextView) findViewById(R.id.jarak);
        mAsal = (TextView) findViewById(R.id.asal);
        mTujuan = (TextView) findViewById(R.id.tujuan);
        mOngkos = (TextView) findViewById(R.id.ongkos);
        mDeposit = (TextView) findViewById(R.id.deposit);
        btnTopUp = (Button) findViewById(R.id.btnTopUp);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnCall = (Button) findViewById(R.id.btnCall);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnRate = (Button) findViewById(R.id.btnRate);
        txtDriverHp = (TextView) findViewById(R.id.txtDriverHp);
        txtDriverName = (TextView) findViewById(R.id.txtDriverName);
        divDriver = (LinearLayout) findViewById(R.id.divDriverAccepted);
        divDriver.setVisibility(View.GONE);
        txtMyLocation = (TextView) findViewById(R.id.txtMyLocation);
        txtTujuan = (TextView) findViewById(R.id.txtSearch);
        btnClikOnMap = (Button) findViewById(R.id.cmdTujuanMap);
        btnSearchInput = (Button) findViewById(R.id.cmdTujuanInput);
        txtCatatan = (TextView) findViewById(R.id.txtCatatan);


    }

    private boolean SubmitOrder() {
        boolean ret = false;
        String tujuan = mPlaceTujuanText;
        if (tujuan.isEmpty()) tujuan = AddressFromLatLng(new LatLng(mToLat, mToLng));
        if (mMember.newOrder(mNomorHp, myLatLng, new LatLng(mToLat, mToLng), mJarakVal,
                mOngkosVal, mJenis, txtCatatan.getText().toString(), tujuan)) {
            SharedPreferences.Editor editor = mSetting.edit();
            editor.putFloat("tujuan_lat", mToLat);
            editor.putFloat("tujuan_lng", mToLng);
            editor.putString("tujuan_name", tujuan);
            editor.putInt("order_id",mMember.get_order_id());
            editor.commit();
            ret = true;
        }
        return ret;
    }


    private void getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            if (locationManager == null) {
                Log.d(TAG, "locationManager == null");
            }

            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    6000, 1, (LocationListener) this);
            if (location == null) { //gps provider error try passive
                location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(criteria, false));
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                        2000, 1, (LocationListener) this);
            }
            if (location == null) {
                if (map != null) {
                    location = map.getMyLocation();
                }
            }
        } finally {
            Log.d("getLocation","Unable load getLcation()");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null ) {
            try {
                mGoogleApiClient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mAdapter.setGoogleApiClient( null );
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void findPlaceById( String id ) {
        if( TextUtils.isEmpty( id ) || mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
            return;

        Places.GeoDataApi.getPlaceById( mGoogleApiClient, id ) .setResultCallback(
                new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if( places.getStatus().isSuccess() ) {
                    Place place = places.get( 0 );
                    mPlaceTujuan=place;
                    // Check if no view has focus:
                    if (mPredictTextView.hasFocus()) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mPredictTextView.getWindowToken(), 0);
                    }
                    mPredictTextView.setText( "" );
                    mPredictTextView.clearFocus();

                    displayPlace( place );
                    mAdapter.clear();
                }
                //Release the PlaceBuffer to prevent a memory leak
                places.release();
            }
        } );

    }

    private void displayPlace( Place place ) {
        if( place == null ) return;

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
        Log.d("content",content);

        ///mCurrentPlace = String.valueOf(Places.GeoDataApi.getPlaceById(mGoogleApiClient,myLatLng.toString()));


        mAsal.setText(AddressFromLatLng(myLatLng));
        mTujuan.setText(content);

        mPlaceTujuan=place;
        mPlaceTujuanText=content;

        mToLat = (float) mPlaceTujuan.getLatLng().latitude;
        mToLng = (float) mPlaceTujuan.getLatLng().longitude;
        map.clear();
        addMarkerMe(map,myLatLng.latitude,myLatLng.longitude,
                mMember.get_user_name(),
                mMember.get_handphone());
        addMarkerDrivers();
        DrawRoute(myLatLng,place.getLatLng());
        LinearLayout section_result= (LinearLayout) findViewById(R.id.section_result);
        section_result.setVisibility(View.VISIBLE);

        //myLocation();
        //startTimer();

        mTujuan.setText(AddressFromLatLng(new LatLng(mToLat,mToLng)));
    }
    private void addMarkerTujuan(){
        mToLat = mSetting.getFloat("tujuan_lat", 0);
        mToLng = mSetting.getFloat("tujuan_lng",0);
        String tname = mSetting.getString("tujuan_name","");
        if(tname.contains("Unnamed Road"))tname.concat("Lat: "+mToLat+", Lon: "+mToLng);
        addMarkerTo(map, mToLat, mToLng, tname, mPlaceTujuanText);
    }

    private String AddressFromLatLng(LatLng myLatLng) {
        Geocoder geocoder;

        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String address="Current Location";
        try {
            addresses = geocoder.getFromLocation(myLatLng.latitude, myLatLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses.size()>0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            mPlaceTujuan=PlacePicker.getPlace( data, this );
            displayPlace(mPlaceTujuan);
        }
    }
    private void startTimer() {
        if (_timer == null) {
            _timer = new Timer();
            mDriverLagiNgantar=false;
        }



        _task = new TimerTask() {
            public void run() {
                _handler.post(new Runnable() {
                    public void run() {

                        Log.d("statTimer", "Start timer with mode : " + mMode);

                        map.clear();

                        //add my marker
                        addMarkerMe(map,myLatLng.latitude,myLatLng.longitude,
                                mMember.get_user_name(),
                                mMember.get_handphone());

                        addMarkerTujuan();                      //add marker tujuan
                        DrawRoutePoly();                        //gambar rute

                        getDriverCurrentLocation(); //driver yg ada disekitaran
                                                    //dan driver yg siap ambil penumpang ini
                        addMarkerDrivers();         //add marker semua driver

                        if( mWaitingDriver &&  mDriver!=null) {
                                //mulai cari driver setelah submit
                                //ok sudah dapat driver ...
                                getDriverPosition();
                                addMarkerDriverAccept(map, mDriver.get_lat(), mDriver.get_lng(),
                                        mDriver.get_user_name(), "Accepted By Driver");       //add marker driver saat ini

                                if(mDriver.get_status()==2) {       //driver siapp !!
                                    mDriverLagiNgantar=true;
                                }

                        }
                        if( mDriver != null ) {
                            //apabila driver lagi nganter tetapi status lagi nyari lagi
                            //artinya sudah selesai antar
                            //stop timer dan close order
                            if (mDriver.get_status() == 1 && mDriverLagiNgantar == true) {
                                //1-driver lagi cari penumpang lagi
                                //stop transaction and close status and reset
                                stopTimer();
                                rateDriver();
                                mWaitingDriver = false;
                                mDriver=null;
                            }

                        }
                    }
                });
            }};


        _timer.schedule(_task, 6000, 30000);
    }

    private void reset(){
        //reset tujuan
        mDriverName="";
        mDriverHandphone="";
        mToLat=0;
        mToLng=0;
        mPlaceTujuanText="";
        mDriver=null;

        SharedPreferences.Editor editor = mSetting.edit();
        editor.putFloat("tujuan_lat", 0);
        editor.putFloat("tujuan_lng", 0);
        editor.putString("tujuan_name", "");
        editor.putString("driver_handphone", "");
        editor.putString("driver_name","");
        editor.putInt("mode",MODE_CARI_AlAMAT);
        editor.commit();


    }
    private void rateDriver() {
        if( mDriver == null ){
            Toast.makeText(getBaseContext(),"Belum ada driver yang accept!",Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(new Intent("RateDriverActivity"));
        finish();
    }

    public void stopTimer() {
        if (_task != null) {
            Log.d("TIMER", "timer canceled");
            if(_timer != null ) _timer.cancel();
            _timer=null;
        }
    }

    private class getServerDataAsync extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");
            if(mDriver==null)mDriver=new Driver(getBaseContext());
            if(!mDriverHandphone.isEmpty()){
                mDriver.loadByPhoneJob(mDriverHandphone,"driver");
            }
            if(mMember==null)mMember=new User(getBaseContext());
            if(myLatLng!=null) {
                mMember.pushMyLatLng(mNomorHp, myLatLng.latitude, myLatLng.longitude);
            }
            mMember.loadByPhoneJob(mNomorHp,"penumpang");

            getDriverCurrentLocation();
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a){
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            Log.d(TAG + " onPostExecute", "" + result);
            DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"
            //mDeposit.setText(df.format(mDepositAmount));
            // driver accepted
            divDriver.setVisibility(View.VISIBLE);
            txtDriverHp.setText("Posisi: "+mDriver.get_lat()+"/"+mDriver.get_lng());
            txtDriverName.setText("Nama: " + mDriver.get_user_name()+ ", Hp: "+mDriver.get_handphone());
            addMarkerDrivers();
            mAsal.setText(AddressFromLatLng(myLatLng));


        }
    }
}
