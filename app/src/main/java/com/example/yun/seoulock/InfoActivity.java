package com.example.yun.seoulock;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.yun.seoulock.air.AirData;
import com.example.yun.seoulock.air.Frag_air;
import com.example.yun.seoulock.eve.EveData;
import com.example.yun.seoulock.eve.Frag_event;
import com.example.yun.seoulock.weat.Frag_weat;
import com.example.yun.seoulock.weat.WeatData;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    final static String key = "4d69666c52616c73343872666f5367";
    final static String _AIR = "AIR_DATA";
    final static String _WEAT = "WEAT_DATA";
    final static String _EVE = "EVE_DATA";

    Button b_back;

    ArrayList<AirData> m_airData = new ArrayList<AirData>();
    ArrayList<WeatData> m_weatData = new ArrayList<WeatData>();
    ArrayList<EveData> m_eveData = new ArrayList<EveData>();

    Fragment fragA, fragW, fragE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

//        getFragmentManager().beginTransaction().add(R.id.frag_layout, new Frag_air()).commit();

        b_back = (Button) findViewById(R.id.btn_back);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Log.e("CCC", "INFO_START");
            m_airData = bundle.getParcelableArrayList(_AIR);
            Log.e("CCC", "INFO_" + m_airData.get(0).getA_GRADE());
            m_weatData = bundle.getParcelableArrayList(_WEAT);
            Log.e("CCC", "INFO_" + m_weatData.get(0).getW_TEMP());
            m_eveData = bundle.getParcelableArrayList(_EVE);
            Log.e("CCC", "INFO_" + m_eveData.get(0).getE_TITLE());
        } else {

        }

        fragA = new Frag_air();
        fragW = new Frag_weat();
        fragE = new Frag_event();

        if(!m_airData.isEmpty()) {
            Bundle bundleA = new Bundle();
            bundleA.putParcelableArrayList(_AIR, m_airData);
            fragA.setArguments(bundleA);
        }

        if(!m_weatData.isEmpty()) {
            Bundle bundleW = new Bundle();
            bundleW.putParcelableArrayList(_WEAT, m_weatData);
            fragW.setArguments(bundleW);
        }

        if(!m_eveData.isEmpty()) {
            Bundle bundleE = new Bundle();
            bundleE.putParcelableArrayList(_EVE, m_eveData);
            fragE.setArguments(bundleE);
        }

        getFragmentManager().beginTransaction().add(R.id.frag_layout, fragA).commit();

        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void switchFrag(View v) {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment frag = null;

        switch (v.getId()) {
            case R.id.btn_air:
                frag = fragA;
                break;
            case R.id.btn_waether:
                frag = fragW;
                break;
            case R.id.btn_event:
                frag = fragE;
                break;
        }
        if(frag == null) return;
        else transaction.replace(R.id.frag_layout, frag).commit();

        return;
    }
}
