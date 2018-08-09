package com.example.yun.seoulock.air;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.yun.seoulock.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_air extends Fragment {

    ProgressDialog progressDialog;

    final static String key = "4d69666c52616c73343872666f5367";
    final static String _AIR = "AIR_DATA";

    final static String s_oz = " ppm";
    final static String s_pm10 = " ㎍/㎡";
    final static String s_pm25 = " ㎍/㎡";

    final static String noData = "점검중";

    AirParser airParser;
    ArrayList<AirData> m_airData = new ArrayList<AirData>();
    AirData myData;

    TextView t_main, t_pm10, t_pm25, t_oz, t_date;
    String location = "종로구";
    int sf;
    int prog;

    ProgressBar p_main, p_pm10, p_pm25, p_oz;

    public Frag_air() {
        // Required empty public constructor
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (m_airData.isEmpty()) {
                Log.e("SET", "m_airData is empty");
            } else {
                setNew(location);
            }
        }

    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("CCC", "BUNDLE");
        Bundle bundle = getArguments();
        if(bundle != null) {
            Log.e("CCC", "NOT NULL");
            m_airData = bundle.getParcelableArrayList(_AIR);
            Log.e("CCC", m_airData.get(0).getA_GRADE());
        } else {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_frag_air, container, false);

        View av = inflater.inflate(R.layout.fragment_frag_air, container, false);

        t_main = (TextView) av.findViewById(R.id.txt_mindex);
        t_oz = (TextView) av.findViewById(R.id.txt_ozon);
        t_pm10 = (TextView) av.findViewById(R.id.txt_pm10);
        t_pm25 = (TextView) av.findViewById(R.id.txt_pm25);

        p_main = (ProgressBar) av.findViewById(R.id.prog_main);
        p_pm10 = (ProgressBar) av.findViewById(R.id.prog_pm10);
        p_pm25 = (ProgressBar) av.findViewById(R.id.prog_pm25);
        p_oz = (ProgressBar) av.findViewById(R.id. prog_oz);

        sf = 0;

        airParser = new AirParser(key);

        progressDialog = new ProgressDialog(getActivity());

        final Spinner loc_spin = (Spinner) av.findViewById(R.id.loc_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(av.getContext(), R.array.location, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loc_spin.setAdapter(adapter);

        loc_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = loc_spin.getSelectedItem().toString();
                Message msg;

                if(sf == 0) {
                    sf = 1;
                } else {
                    msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        getAirData();

        return av;
    }

    public void getAirData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg;

                try {
                    if (m_airData.isEmpty()) {
                        m_airData = airParser.GetAirData();
                    }

                    Log.e("GET", m_airData.get(0).getA_MSRSTENAME());

                    if (!m_airData.isEmpty()) {
                        msg = handler.obtainMessage();
                        handler.sendMessage(msg);
                    } else {
                        Log.e("GET", "m_airData is empty");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }

            }
        }).start();
    }

    public void setNew(String s) {
        String loc = s;
        int mi, p1, p2, oz;

        int size = m_airData.size();
        Log.e("CCC", ""+size);
        Log.e("CCC", loc);
        for (int i = 0; i < size; i++) {

            if (m_airData.get(i).getA_MSRSTENAME().equals(loc)) {
                Log.e("CCC", m_airData.get(i).getA_GRADE());

                myData = m_airData.get(i);
                Log.e("CCC", "myData");

                break;
            }
        }

        if(myData.getA_GRADE().equals(noData)) {
            t_main.setText(myData.getA_GRADE());
            p_main.setProgress(0);
        } else {
            t_main.setText("대기환경지수\n\n" + myData.getA_MAXINDEX() + "\n\n" + myData.getA_GRADE());
            mi = Integer.parseInt(myData.getA_MAXINDEX());
            setMainScore(mi);
        }

        if(myData.getA_PM10().equals(noData)) {
            t_pm10.setText(myData.getA_PM10());
            p_pm10.setProgress(0);
        } else {
            t_pm10.setText(myData.getA_PM10() + s_pm10);
            p1 = Integer.parseInt(myData.getA_PM10());
            p_pm10.setProgress(p1);
        }

        if(myData.getA_PM25().equals(noData)) {
            t_pm25.setText(myData.getA_PM25());
            p_pm25.setProgress(0);
        } else {
            t_pm25.setText(myData.getA_PM25() + s_pm25);
            p2 = Integer.parseInt(myData.getA_PM25());
            p_pm25.setProgress(p2);
        }

        if(myData.getA_OZONE().equals(noData)) {
            t_oz.setText(myData.getA_OZONE());
            p_oz.setProgress(0);
        } else {
            t_oz.setText(myData.getA_OZONE() + s_oz);
            oz = (int) (Double.parseDouble(myData.getA_OZONE()) * 1000);
            p_oz.setProgress(oz);
        }

        return;
    }

    public void setMainScore(int m) {
        prog = 0;
        final int i = m;

        new Thread() {
            @Override
            public void run() {
                try{
                    while (true) {
                        prog += 1;
                        sleep(10);
                        p_main.setProgress(prog);

                        Log.i("SET","PROGRESS");

                        if(prog >= i) break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


}
