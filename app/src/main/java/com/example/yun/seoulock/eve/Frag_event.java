package com.example.yun.seoulock.eve;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yun.seoulock.R;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_event extends Fragment {

    ProgressDialog progressDialog;

    private ImageView thumbnail;
    private TextView title_tv, time_tv, location_tv;
    private ListView listView;
    private LinearLayout link_page;
    final static String key = "4d69666c52616c73343872666f5367";
    Bitmap [] bitmap = new Bitmap[20];
    int bitmap_count =0;
    EveParser eveParser;
    ArrayList<EveData> m_eveData;
    CustomList adapter = null;


    public Frag_event() {
        // Required empty public constructor
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if(m_eveData.isEmpty()) {
                Log.e("SET","m_eveData is empty");
            } else {

                if(adapter == null) {
                    adapter = new CustomList(getContext(), R.layout.fragment_frag_event, m_eveData);
                    listView.setAdapter(adapter);
                } else {
                    adapter.items = m_eveData;
                    adapter.notifyDataSetChanged();
                }


            }

        }

    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_frag_event, container, false);

        View ev = inflater.inflate(R.layout.fragment_frag_event, container, false);

        eveParser = new EveParser(key);
        link_page = ev.findViewById(R.id.eve_linearLayout);
        thumbnail =ev.findViewById(R.id.eve_imageView);
        title_tv = ev.findViewById(R.id.eve_title);
        time_tv = ev.findViewById(R.id.eve_date);
        location_tv = ev.findViewById(R.id.eve_location);
        listView = ev.findViewById(R.id.eve_list);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        getEveData();

        return ev;
    }


    public class CustomList extends ArrayAdapter<Object>{

        public ArrayList<EveData> items;
        View view;

        public CustomList(Context context, int textViewSrcId, ArrayList items) {
            super(context, textViewSrcId, items);

            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            view = convertView;

            if(view == null) {
                LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.eve_list_item, null);
            }

            int last = this.getCount();

            if((last-1) == position) {
                thumbnail = view.findViewById(R.id.eve_imageView);
                title_tv = view.findViewById(R.id.eve_title);
                time_tv = view.findViewById(R.id.eve_date);
                location_tv = view.findViewById(R.id.eve_location);

//                title_tv.setText("10개 더보기");
//                time_tv.setText("");
//                location_tv.setText("");
            } else {
                final EveData eveData = (EveData) items.get(position);

                if(eveData != null) {
                    thumbnail = view.findViewById(R.id.eve_imageView);
                    title_tv = view.findViewById(R.id.eve_title);
                    time_tv = view.findViewById(R.id.eve_date);
                    location_tv = view.findViewById(R.id.eve_location);
                    link_page = view.findViewById(R.id.eve_linearLayout);

                    link_page.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(eveData.e_LINK)));
                            }
                    });

                    eveData.e_TITLE =   eveData.e_TITLE.replaceAll("&quot;","\"");
                    title_tv.setText(eveData.e_TITLE);
                    title_tv.setSelected(true);
                    time_tv.setText(eveData.e_SDATE+" ~ "+eveData.e_EDATE);
                    location_tv.setText(eveData.e_PLACE);
                    thumbnail.setImageBitmap(bitmap[position]);
                }
            }

            progressDialog.dismiss();

            return view;
        }
    }

    public void getEveData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg;
                m_eveData = eveParser.GetEveData();

                Log.e("GET", m_eveData.get(0).e_TITLE);

                if( !m_eveData.isEmpty() ) {
                    while(bitmap_count !=20){
                        try {
                            bitmap[bitmap_count] = getthumbnailBitmap(m_eveData.get(bitmap_count).e_IMG);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        bitmap_count++;
                    }

                    msg = handler.obtainMessage();
                    handler.sendMessage(msg);

                } else {
                    Log.e("GET", "m_eveData is empty");
                }

            }
        }).start();
    }

    private Bitmap getthumbnailBitmap(String u) {
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
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            return bm;
        }
    }


}