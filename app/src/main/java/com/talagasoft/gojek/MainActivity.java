package com.talagasoft.gojek;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.talagasoft.gojek.controller.ItemMasterController;
import com.talagasoft.gojek.libs.AlertDialogManager;
import com.talagasoft.gojek.libs.ConnectionDetector;
import com.talagasoft.gojek.libs.GPSTracker;
import com.talagasoft.gojek.model.Deposit;
import com.talagasoft.gojek.model.SettingServer;
import com.talagasoft.gojek.model.Supplier;
import com.talagasoft.gojek.model.User;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener  {

    SharedPreferences mSetting = null;
    Boolean mLoggedIn=false;
    int mTarif=0,mDepositAmount=0,mOrderId=0;
    String mNama="",mNoHp="",mNamaDriver="",mHpDriver="";
    TextView mDeposit,mPoint,mDriver;
    SettingServer mSetServer;
    AdView mAdView;
    // Connection detector class
    ConnectionDetector cd;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    // handle timer schedule mulai jalan ke tujuan
    TimerTask _task;
    final Handler _handler = new Handler();
    Timer _timer;

    float myLat,myLng;
    private float mToLat,mToLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //initialize controls
        mDriver = (TextView) findViewById(R.id.driver);
        mDeposit = (TextView) findViewById(R.id.deposit);

        //google ads
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1869511402643723~8811604696");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //load local setting
        loadSession();

        //ambil data dari web thread
        new getServerDataAsync().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Loading Panggil Tukang Ojek", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cd = new ConnectionDetector(getApplicationContext());
        // flag for Internet connection status
        Boolean isInternetPresent = false;
        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(this, "Internet Connection Error","Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
        Button cmdRating= (Button) findViewById(R.id.cmdRating);
        cmdRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("RateDriverActivity"));
            }
        });
        startTimer();
    }

    private void loadSession() {
        mSetting = getSharedPreferences(getResources().getString(R.string.setting), Context.MODE_WORLD_READABLE);
        mLoggedIn = mSetting.getBoolean("logged_in", false);
        mNama = mSetting.getString("nama", "Guest");
        mNoHp = mSetting.getString("no_hp", "0000");
        mHpDriver = mSetting.getString("driver_handphone","");
        mNamaDriver = mSetting.getString("driver_name","");
        mTarif=mSetting.getInt("tarif",0);
        mToLat = mSetting.getFloat("tujuan_lat", 0);
        mToLng = mSetting.getFloat("tujuan_lng", 0);
        mOrderId=mSetting.getInt("order_id",0);

        String sDepo=mSetting.getString("deposit","0");
        if(sDepo.isEmpty())sDepo="0";
        mDepositAmount = Integer.parseInt(sDepo);
        this.setTitle("Hai " + mNama);

        GPSTracker gps=new GPSTracker(getBaseContext());
        myLat= (float) gps.getLatitude();
        myLng= (float) gps.getLongitude();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        callMenu(id);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        this.setTitle("Hai " + mNama);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        callMenu(id);
        return true;
    }
    private void callMenu(int id){
        loadSession();
        Intent intent=null;
        if (id == R.id.nav_antar || id == R.id.cmdBike) {
            if(mOrderId==0 ){
                intent = new Intent("MapsActivity");
            } else {
                if(mHpDriver==""){
                    intent = new Intent("WaitingDriver");

                } else {
                    intent = new Intent("MapsActivity");

                }
            }
            intent.putExtra("no_hp", mNoHp);
            intent.putExtra("nama", mNama);
            intent.putExtra("jenis","bike");
            startActivity(intent);

        } else if (id == R.id.nav_jemput || id == R.id.cmdCar) {
            if(mOrderId==0){
                intent = new Intent("MapsActivity");
            } else {
                intent = new Intent("WaitingDriver");
            }
            intent.putExtra("no_hp", mNoHp);
            intent.putExtra("nama", mNama);
            intent.putExtra("jenis","car");
            startActivity(intent);

        } else if (id == R.id.nav_deposit  ) {
            intent = new Intent("DepositActivity");
            intent.putExtra("no_hp", mNoHp);
            intent.putExtra("nama", mNama);
            startActivity(intent);

        } else if (id == R.id.nav_daftar) {
            intent = new Intent("AccountActivity");
            intent.putExtra("no_hp", mNoHp);
            intent.putExtra("nama", mNama);
            startActivity(intent);

        } else if (id == R.id.nav_argo  ) {
            intent = new Intent("MapsActivity");
            intent.putExtra("no_hp", mNoHp);
            intent.putExtra("nama", mNama);
            intent.putExtra("jenis","argo");
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }else if (id == R.id.action_settings) {
            intent = new Intent("AccountActivity");
            intent.putExtra("no_hp", mNoHp);
            intent.putExtra("nama", mNama);
            intent.putExtra("jenis","argo");
            startActivity(intent);
        }else if (id == R.id.cmdBike) {
            intent = new Intent("DompetActivity");
            intent.putExtra("no_hp", mNoHp);
            intent.putExtra("nama", mNama);
            startActivity(intent);

        } else if (id == R.id.action_logout ||  id == R.id.cmdBike) {

            SharedPreferences.Editor editor = mSetting.edit();
            //Adding values to editor
            editor.putBoolean("logged_in", false);
            editor.putString("no_hp", "0000");
            editor.putString("nama", "Guest");
            editor.putInt("mode",0);
            editor.putFloat("tujuan_lat",0);
            editor.putFloat("tujuan_lng",0);
            editor.putString("tujuan_name","");
            //Saving values to editor
            editor.commit();
            Toast.makeText(this, "Anda sudah logout, terimakasih. ", Toast.LENGTH_LONG);
            //alert.showAlertDialog(this, "Berhasil logout.","Silahkan dijalankan lagi aplikasi dan masukkan user baru anda.", false ) ;
            restart(this, 2);
        } else if (id == R.id.cmdFood){
            Intent supplier=new Intent("SupplierActivity");
            supplier.putExtra("type","food");
            startActivity(supplier);
        } else if (id == R.id.cmdPaket){
            Intent paket=new Intent("PaketActivity");
            startActivity(paket);
        }

    }
    @Override
    public void onClick(View view) {
        callMenu(view.getId());
    }

    public static void restart(Context context, int delay) {
        if (delay == 0) {
            delay = 1;
        }
        Log.e("", "restarting app");
        Intent restartIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName() );
        PendingIntent intent = PendingIntent.getActivity(context, 0,restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
        System.exit(2);
    }
    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        stopTimer();
        super.onDestroy();
    }
    private void startTimer() {
        if (_timer == null) {
            _timer = new Timer();
        }
        Log.d("TIMER", "timer start.");

        _task = new TimerTask() {
            public void run() {
                _handler.post(new Runnable() {
                    public void run() {

                        if (hasDriverAccept()) {
                            addNotifyDriverAccept();
                        }

                        if(hasInboxMessage()){
                            addNotifyInbox();
                        }
                        //ambil data dari web thread
                        new getServerDataAsync().execute();

                        mHpDriver = mSetting.getString("driver_handphone","");
                        mNamaDriver = mSetting.getString("driver_name","");
                        mDriver = (TextView) findViewById(R.id.driver);
                        mDriver.setText(mNamaDriver+ ", "+mHpDriver);

                    }
                });
            }
        };
        _timer.schedule(_task, 6000, 30000);
    }
    public void stopTimer() {
        if (_task != null) {
            Log.d("TIMER", "timer stop !");
            if(_timer != null ) _timer.cancel();
            _timer=null;
        }
    }
    private Boolean hasDriverAccept(){
        String hpDriver,namaDriver,lokasiDriver;
        float latDriver,lonDriver;
        mNamaDriver = mSetting.getString("driver_name","");
        mHpDriver = mSetting.getString("driver_handphone","");
        if(!mHpDriver.isEmpty()) return false;
        User user=new User(getBaseContext());
        user.loadByPhoneJob(mNoHp,"penumpang");
        hpDriver=user.get_driver();
        if( !hpDriver.isEmpty()){
            user.loadByPhoneJob(hpDriver,"driver");
            mNamaDriver=user.get_user_name();
            mHpDriver=hpDriver;
            latDriver=user.get_lat();
            lonDriver=user.get_lng();
            lokasiDriver=user.get_location();
            //save to setting tujuan untuk menghindari buffer kosong
            SharedPreferences.Editor editor = mSetting.edit();
            editor.putString("driver_handphone", hpDriver);
            editor.putString("driver_name",mNamaDriver);
            editor.commit();

            return true;
        } else {
            return false;
        }
    }
    private Boolean hasInboxMessage(){

        return false;
    }
    private void addNotifyDriverAccept(){
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.driver_accept)
                        .setContentTitle("Anda mendapatkan pengemudi.")
                        .setContentText("Nama: "+mNamaDriver+", Handphone: "+mHpDriver);

        Intent notificationIntent = new Intent(this, NotificationView.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
    private void addNotifyInbox(){

    }
    private class getServerDataAsync extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            mDepositAmount=new Deposit(getBaseContext()).Saldo(mNoHp);
            mSetServer=new SettingServer(getBaseContext());
            mTarif=mSetServer.tarif();
            Supplier vItem=new Supplier(getBaseContext());
            vItem.downloadUpdate();
            ItemMasterController itemMaster=new ItemMasterController(getBaseContext());
            itemMaster.downloadUpdate();

            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a){
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            Log.d(TAG + " onPostExecute", "" + result);

            SharedPreferences.Editor editor = mSetting.edit();
            editor.putString("deposit", String.valueOf(mDepositAmount));
            editor.putInt("tarif",mTarif);
            if(myLat!=0) editor.putFloat("my_lat",myLat);
            if(myLng!=0) editor.putFloat("my_lng",myLng);
            editor.commit();

            DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"
            mDeposit.setText(df.format(mDepositAmount));
        }
    }
}
