package com.example.yun.seoulock;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.yun.seoulock.air.AirData;
import com.example.yun.seoulock.air.AirParser;
import com.example.yun.seoulock.eve.EveData;
import com.example.yun.seoulock.eve.EveParser;
import com.example.yun.seoulock.weat.WeatData;
import com.example.yun.seoulock.weat.WeatParser;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    final static String key = "4d69666c52616c73343872666f5367";
    final static String _AIR = "AIR_DATA";
    final static String _WEAT = "WEAT_DATA";
    final static String _EVE = "EVE_DATA";


    AirParser airParser;
    WeatParser weatParser;
    EveParser eveParser;

    static ArrayList<AirData> m_airData;
    static ArrayList<WeatData> m_weatData;
    static ArrayList<EveData> m_eveData;

    boolean ab, wb, eb;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            Bundle bundle = new Bundle();
            if (ab&&wb&&eb) {
                Log.e("CCC", "SPL_SUCCESS");
                bundle.putParcelableArrayList(_AIR, m_airData);
                bundle.putParcelableArrayList(_WEAT, m_weatData);
                bundle.putParcelableArrayList(_EVE, m_eveData);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Log.e("CCC", "SPL_FAIL");
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
            finish();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        airParser = new AirParser(key);
        weatParser = new WeatParser(key);
        eveParser = new EveParser(key);

        ab = false;
        wb = false;
        eb = false;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startParsing();

    }

    public void startParsing() {
        final Thread t = new Thread() {
            @Override
            public void run() {

                Message msg;

                try {
                    m_airData = airParser.GetAirData();
                    if( !m_airData.isEmpty() )
                        ab = true;

                    m_weatData = weatParser.GetWeatData();
                    if( !m_weatData.isEmpty() )
                        wb = true;

                    m_eveData = eveParser.GetEveData();
                    if( !m_eveData.isEmpty() )
                        eb = true;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            }
        };
        t.start();
    }
}
