package com.example.yun.seoulock.weat;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.yun.seoulock.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_weat extends Fragment {

    ProgressDialog progressDialog;

    final static String key = "4d69666c52616c73343872666f5367";
    final static String _WEAT = "WEAT_DATA";

    final static String s_tmp = " ℃";
    final static String s_hum = " %";
    final static String s_sun = " /h";
    final static String s_rain = " ㎜";
    final static String s_wind = " ㎧";

    WeatParser weatParser;
    ArrayList<WeatData> m_weatData = new ArrayList<WeatData>();
    WeatData myData;

    OwmParser owmParser;
    OwmData owmData;

    ImageView i_weat, i_tmp, i_hum, i_sun, i_rain, i_wind;
    TextView t_mainTmp, t_mmTmp, t_sky, t_tmp, t_hum, t_sun, t_rain, t_wind;
    String location = "종로";
    int sf;
    Bitmap bitmap;

    public Frag_weat() {
        // Required empty public constructor
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    if(m_weatData.isEmpty() || owmData == null ) {
                        Log.e("SET","Data is empty");
                    } else {
                        setting(location);
                    }
                    break;
                case 1:
                    if(bitmap != null) {
                        setBitmap();
                    } else {
                        Log.e("SET", "bitmap is null");
                    }
                    break;
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
            m_weatData = bundle.getParcelableArrayList(_WEAT);
            Log.e("CCC", m_weatData.get(0).getW_TEMP());
        } else {

        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_frag_weat, container, false);
        View wv = inflater.inflate(R.layout.fragment_frag_weat, container, false);

        i_weat = (ImageView) wv.findViewById(R.id.img_weat);
        i_tmp = (ImageView) wv.findViewById(R.id.img_tmp);
        i_hum = (ImageView) wv.findViewById(R.id.img_hum);
        i_sun = (ImageView) wv.findViewById(R.id.img_sun);
        i_rain = (ImageView) wv.findViewById(R.id.img_rain);
        i_wind = (ImageView) wv.findViewById(R.id.img_wind);

        t_mainTmp = (TextView) wv.findViewById(R.id.txt_main_tmp);
        t_mmTmp = (TextView) wv.findViewById(R.id.txt_min_max);
        t_sky = (TextView) wv.findViewById(R.id.txt_sky);
        t_tmp = (TextView) wv.findViewById(R.id.txt_tmp);
        t_hum = (TextView) wv.findViewById(R.id.txt_hum);
        t_sun = (TextView) wv.findViewById(R.id.txt_sun);
        t_rain = (TextView) wv.findViewById(R.id.txt_rain);
        t_wind = (TextView) wv.findViewById(R.id.txt_wind);

        bitmap = null;

        weatParser = new WeatParser(key);
        owmParser = new OwmParser();
        progressDialog = new ProgressDialog(getActivity());

        sf = 0;

        final Spinner loc_spin = (Spinner) wv.findViewById(R.id.w_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(wv.getContext(), R.array.loc_weather, android.R.layout.simple_spinner_item);
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
                    msg = handler.obtainMessage(0);
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

        getWeatData();
        getOwmData();

        return wv;
    }

    public void getWeatData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg;

                try {
                    if (m_weatData.isEmpty()) {
                        m_weatData = weatParser.GetWeatData();
                    }

                    Log.e("GET", m_weatData.get(0).getW_NAME());

                    if (!m_weatData.isEmpty()) {

                    } else {
                        Log.e("GET", "m_weatData is empty");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void getOwmData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg;

                try {
                    owmData = owmParser.GetOwmData();

                    Log.e("GET", owmData.cld_name);

                    if (owmData != null) {
                        msg = handler.obtainMessage(0);
                        handler.sendMessage(msg);

                        bitmap = getBitmap(owmData.whet_icon);
                        msg = handler.obtainMessage(1);
                        handler.sendMessage(msg);

                    } else {
                        Log.e("GET", "owmData is null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }

            }
        }).start();
    }

    public void setting(String s) {
        String loc = s;

        int size = m_weatData.size();

        for (int i = 0; i < size; i++) {
            //Log.e("for", s);
            if (m_weatData.get(i).getW_NAME().equals(loc)) {
                Log.e("SET", m_weatData.get(i).getW_NAME());

                myData = m_weatData.get(i);

                break;
            }
        }

        t_mainTmp.setText("전체 온도\n"+ owmData.tmp_cur + s_tmp);
        t_mmTmp.setText("MIN / MAX\n"+ owmData.tmp_min + " / " + owmData.tmp_max);
        t_sky.setText(owmData.cld_name);
        t_tmp.setText(myData.getW_TEMP() + s_tmp);
        t_hum.setText(myData.getW_HUMI() + s_hum);

        if(myData.w_SHINE.isEmpty()) {
            t_sun.setText("-");
        } else {
            t_sun.setText(myData.getW_SHINE() + s_sun);
        }

        t_rain.setText(myData.getW_RAIN() + s_rain);
        t_wind.setText(myData.getW_WINS() + s_wind);

    }

    private Bitmap getBitmap(String u) {
        HttpURLConnection connection = null;
        InputStream is = null;
        Bitmap bm = null;

        try {
            URL url = new URL(u);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            is = connection.getInputStream();
            bm = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            Log.e("BIT", "Bitmap Error...");
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            Log.e("BIT", "AAA");
            return bm;
        }
    }

    private void setBitmap() {
        Log.e("BIT","BBB");
        i_weat.setImageBitmap(bitmap);
        return;
    }

}
