package com.example.yun.seoulock.weat;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;

public class WeatParser {

    String key;
    ArrayList<WeatData> m_weatData;

    public WeatParser(String k) {
        key = k;
    }

    public ArrayList<WeatData> GetWeatData() {

        m_weatData = new ArrayList<WeatData>();
        WeatData weatData = null;
        boolean b_set = false;

        try {

            //String apiUrl = "http://openapi.seoul.go.kr:8088/4d69666c52616c73343872666f5367/xml/RealtimeWeatherStation/1/25/";
            String apiUrl = "http://openAPI.seoul.go.kr:8088/" + key + "/xml/RealtimeWeatherStation/1/25/";
            URL url = new URL(apiUrl);

            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();

            url.openConnection().getInputStream();
            parser.setInput(url.openStream(), "UTF-8");
            int eventType = parser.getEventType();

            while ( eventType != XmlPullParser.END_DOCUMENT ) {

                switch (eventType) {

                    case XmlPullParser.START_TAG:

                        String tag = parser.getName();
                        if(tag.equals("row")) {
                            b_set = true;
                        }

                        if (b_set && tag.compareTo("SAWS_OBS_TM") == 0) {
                            weatData = new WeatData();
                            String str = parser.nextText();
                            weatData.w_DATE = str;
                            Log.e("WEAT", str);
                        }
                        if (b_set && tag.compareTo("STN_NM") == 0) {
                            String str = parser.nextText();
                            weatData.w_NAME = str;
                            Log.e("WEAT", str);

                        }
                        if (b_set && tag.compareTo("SAWS_TA_AVG") == 0) {
                            String str = parser.nextText();
                            weatData.w_TEMP = str;
                            Log.e("WEAT", str);
                        }
                        if (b_set && tag.compareTo("SAWS_HD") == 0) {
                            String str = parser.nextText();
                            weatData.w_HUMI = str;
                            Log.e("WEAT", str);
                        }
                        if (b_set && tag.compareTo("NAME") == 0) {
                            String str = parser.nextText();
                            weatData.w_WIND = str;
                            Log.e("WEAT", str);
                        }
                        if (b_set && tag.compareTo("SAWS_WS_AVG") == 0) {
                            String str = parser.nextText();
                            weatData.w_WINS = str;
                            Log.e("WEAT", str);
                        }
                        if (b_set && tag.compareTo("SAWS_RN_SUM") == 0) {
                            String str = parser.nextText();
                            weatData.w_RAIN = str;
                            Log.e("WEAT", str);
                        }
                        if (b_set && tag.compareTo("SAWS_SHINE") == 0) {
                            String str = parser.nextText();
                            weatData.w_SHINE = str;
                            Log.e("WEAT", str);
                            m_weatData.add(weatData);
                        }

                        break;
                }
                eventType = parser.next();
            }
            Log.e("WEAT", "WeatParser End...");
        } catch (Exception e) {
            Log.e("WEAT", "WeatParser Error!");
        }

        return m_weatData;
    }
}
