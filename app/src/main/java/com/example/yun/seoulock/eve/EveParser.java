package com.example.yun.seoulock.eve;

import android.util.Log;

import com.example.yun.seoulock.eve.EveData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;

public class EveParser {

    String key;
    ArrayList<EveData> m_eveData;

    public EveParser(String k) {
        key = k;
    }

    public ArrayList<EveData> GetEveData() {

        m_eveData = new ArrayList<EveData>();
        EveData eveData = null;
        boolean b_set = false;

        try {

            //String apiUrl = "http://openapi.seoul.go.kr:8088/4d69666c52616c73343872666f5367/xml/SearchConcertDetailService/1/20/";
            String apiUrl = "http://openapi.seoul.go.kr:8088/" + key + "/xml/SearchConcertDetailService/1/20/";
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

                        if (b_set && tag.compareTo("CODENAME") == 0) {
                            eveData = new EveData();
                            String str = parser.nextText();
                            eveData.setE_GENRE(str);
                            Log.e("EVE", str);
                        }
                        if (b_set && tag.compareTo("MAIN_IMG") == 0) {
                            String str = parser.nextText();
                            eveData.setE_IMG(str);
                            Log.e("EVE", str);

                        }
                        if (b_set && tag.compareTo("TITLE") == 0) {
                            String str = parser.nextText();
                            eveData.setE_TITLE(str);
                            Log.e("EVE", str);
                        }
                        if (b_set && tag.compareTo("STRTDATE") == 0) {
                            String str = parser.nextText();
                            eveData.setE_SDATE(str);
                            Log.e("EVE", str);
                        }
                        if (b_set && tag.compareTo("END_DATE") == 0) {
                            String str = parser.nextText();
                            eveData.setE_EDATE(str);
                            Log.e("EVE", str);
                        }
                        if (b_set && tag.compareTo("TIME") == 0) {
                            String str = parser.nextText();
                            eveData.setE_TIME(str);
                            Log.e("EVE", str);
                        }
                        if (b_set && tag.compareTo("PLACE") == 0) {
                            String str = parser.nextText();
                            eveData.setE_PLACE(str);
                            Log.e("EVE", str);
                        }
                        if (b_set && tag.compareTo("GCODE") == 0) {
                            String str = parser.nextText();
                            eveData.setE_LOCATION(str);
                            Log.e("EVE", str);
                        }
                        if (b_set && tag.compareTo("ORG_LINK") == 0) {
                            String str = parser.nextText();
                            eveData.setE_LINK(str);
                            Log.e("EVE", str);
                            m_eveData.add(eveData);
                        }

                        break;
                }
                eventType = parser.next();
            }
            Log.e("EVE", "EveParser End...");
        } catch (Exception e) {
            Log.e("EVE", "EveParser Error!");
        }

        return m_eveData;
    }
}
