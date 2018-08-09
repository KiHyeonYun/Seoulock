package com.example.yun.seoulock;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yun.seoulock.air.AirData;
import com.example.yun.seoulock.eve.EveData;
import com.example.yun.seoulock.weat.WeatData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.altbeacon.beacon.Beacon;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.Vector;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    final static String _AIR = "AIR_DATA";
    final static String _WEAT = "WEAT_DATA";
    final static String _EVE = "EVE_DATA";

    final static String daLocation = "종로구";
    final static String dwLocation = "종로";

    ArrayList<AirData> m_airData = new ArrayList<AirData>();
    ArrayList<WeatData> m_weatData = new ArrayList<WeatData>();
    ArrayList<EveData> m_eveData = new ArrayList<EveData>();

    private FloatingActionButton flobut;
    private ImageView lock_img;
    private Button logout;
    private Button b_info;
    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    private boolean doorlockstate = false;
    double meter,avg;

    int count = 0;
//푸시 알림을 보내기위해 시스템에 권한을 요청하여 생성

    private BluetoothSPP bt;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothLeScanner;
    BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private static final int PERMISSIONS = 100;

    Vector<Beacon> beacon;
    ScanSettings.Builder mScanSettings;
    List<ScanFilter> scanFilters;
    //
    ScanSettings scanSettings;
 //   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREAN);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notification("pushIn");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
      //  Toast.makeText(getApplicationContext(),mUser.getUid(),Toast.LENGTH_SHORT).show();

        bt = new BluetoothSPP(this); //Initializing
        mAuth = FirebaseAuth.getInstance();
        flobut = findViewById(R.id.floatingButton);
        logout = findViewById(R.id.logout);
        lock_img = findViewById(R.id.img_lock);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            m_airData = bundle.getParcelableArrayList(_AIR);
            m_weatData = bundle.getParcelableArrayList(_WEAT);
            m_eveData = bundle.getParcelableArrayList(_EVE);
        }

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                //아두이노에서 메세지를 받아서 푸쉬알람 메소드로 전달해줌.
                notification(message);
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
                //
                mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
                //
                mBluetoothLeScanner.stopScan(mScanCallback);
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });




        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        beacon = new Vector<>();
        mScanSettings = new ScanSettings.Builder();
        mScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
//        ScanSettings scanSettings = mScanSettings.build();
        scanSettings = mScanSettings.build();

        scanFilters = new Vector<>();
        ScanFilter.Builder scanFilter = new ScanFilter.Builder();
        scanFilter.setDeviceAddress("0C:B2:B7:7B:C5:44"); //ex) 00:00:00:00:00:00
        ScanFilter scan = scanFilter.build();
        scanFilters.add(scan);
//        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);


        lock_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(doorlockstate == false) {
                    lock_img.setImageResource(R.drawable.unlock);
                    doorlockstate = true;
                    Toast.makeText(getApplicationContext(),"문이 열렸습니다.", Toast.LENGTH_SHORT).show();
                    if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                        bt.disconnect();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                    }
                }
                else {
                    lock_img.setImageResource(R.drawable.lock);
                    doorlockstate = false;
                    Toast.makeText(getApplicationContext(),"문이 닫혔습니다.", Toast.LENGTH_SHORT).show();

                }
            }
        });

     logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            Bundle bundle = new Bundle();

            if( !m_airData.isEmpty() && !m_weatData.isEmpty() && !m_eveData.isEmpty()) {
                bundle.putParcelableArrayList(_AIR, m_airData);
                bundle.putParcelableArrayList(_WEAT, m_weatData);
                bundle.putParcelableArrayList(_EVE, m_eveData);
                intent.putExtras(bundle);
            }

            startActivity(intent);

        }
    });

        b_info = (Button) findViewById(R.id.btn_info);

        b_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                Bundle bundle = new Bundle();

                if( !m_airData.isEmpty() && !m_weatData.isEmpty() && !m_eveData.isEmpty()) {
                    bundle.putParcelableArrayList(_AIR, m_airData);
                    bundle.putParcelableArrayList(_WEAT, m_weatData);
                    bundle.putParcelableArrayList(_EVE, m_eveData);
                    intent.putExtras(bundle);
                }

                startActivity(intent);
            }
        });



    }

    ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            super.onScanResult(callbackType, result);
            try {
                ScanRecord scanRecord = result.getScanRecord();
                Log.d("getTxPowerLevel()", scanRecord.getTxPowerLevel() + "");
                Log.d("onScanResult()", result.getDevice().getAddress() + "\n" + result.getRssi() + "\n" + result.getDevice().getName()
                        + "\n" + result.getDevice().getBondState() + "\n" + result.getDevice().getType());

                final ScanResult scanResult = result;
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                                for(int i=0;i<5;i++)
//                                {
//                                    meter+=calculateDistance(scanResult.getRssi());
//
//                                    try {
//                                        Thread.sleep(100);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                avg=meter/5;
//                                if(avg < 0.3){
//                                   // bt.send(,true);
//                                    bt.send(String.valueOf(avg).getBytes(),true);
//                                }
//                                meter=0;
//                                avg=0;
//                    }
//                }).start();

                meter = calculateDistance(scanResult.getRssi());

                count++;
                Log.e("SCAN", "count_"+count);

                bt.send(String.valueOf(meter).getBytes(),true);
                Log.e("SCAN", "meter_"+meter);

                if(count==100) {
                    mBluetoothLeScanner.stopScan(mScanCallback);
                    count=0;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d("onBatchScanResults", results.size() + "");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("onScanFailed()", errorCode + "");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeScanner.stopScan(mScanCallback);
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public static double calculateDistance(int rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi*1.0/-59;
        if (ratio < 1.0) {
            return Math.round(Math.pow(ratio,10)*100)/100.0;
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return Math.round(accuracy*100)/100.0;

        }
    }

    protected void notification(String message){

        //Notification 객체 생성
        final NotificationManager notificationManager = (NotificationManager)MainActivity.this.getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
        final Notification.Builder builder = new Notification.Builder(getApplicationContext());
        final Intent intent = new Intent(MainActivity.this.getApplicationContext(),InfoActivity.class);


        //푸시 알림을 터치하여 실행할 작업에 대한 Flag 설정 (현재 액티비티를 최상단으로 올린다 | 최상단 액티비티를 제외하고 모든 액티비티를 제거한다)
       intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Snackbar.make(getWindow().getDecorView().getRootView(), "더 보기를 원하신다면 푸시알람을 클릭해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                //앞서 생성한 작업 내용을 Notification 객체에 담기 위한 PendingIntent 객체 생성
                PendingIntent pendnoti = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if(message.equals("pushIn")){
                //푸시 알림에 대한 각종 설정
                builder.setSmallIcon(R.drawable.lock_yellow)
                        .setTicker("ㄴㅇㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ")
                        .setWhen(System.currentTimeMillis())
                        .setNumber(1).setContentTitle("뀨뀨")
                        .setContentText("시영했다.")
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setContentIntent(pendnoti)
                        .setAutoCancel(true)
                        .setOngoing(true);
                }
                else if(message.equals("pushOut"))
                {
                    builder.setSmallIcon(R.drawable.unlock)
                            .setTicker("sdsdsdds")
                            .setWhen(System.currentTimeMillis())
                            .setNumber(1).setContentTitle("sddssd")
                            .setContentText("요셉스했다.")
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setContentIntent(pendnoti)
                            .setAutoCancel(true)
                            .setOngoing(true);
                }
                //NotificationManager를 이용하여 푸시 알림 보내기
                notificationManager.notify(1, builder.build());
            }
}

