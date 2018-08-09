package com.example.yun.seoulock.air;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;

public class AirParser {

    String key;
    ArrayList<AirData> m_airData;

    public AirParser(String k) {
        key = k;
    }

    public ArrayList<AirData> GetAirData() {

        m_airData = new ArrayList<AirData>();
        AirData airData = null;
        boolean b_set = false;

        try {

            //String apiUrl = "http://openapi.seoul.go.kr:8088/4d69666c52616c73343872666f5367/xml/ListAirQualityByDistrictService/1/25";
            String apiUrl = "http://openapi.seoul.go.kr:8088/" + key + "/xml/ListAirQualityByDistrictService/1/25";
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

                        if (b_set && tag.compareTo("MSRDATE") == 0) {
                            airData = new AirData();
                            String date = parser.nextText();
                            airData.setA_MSRDATE(date);
                            Log.e("AIR", "측정날짜 : " + date);
                        }
                        if (b_set && tag.compareTo("MSRADMCODE") == 0) {
                            String code = parser.nextText();
                            airData.setA_MSRADMCODE(code);
                            Log.e("AIR", "행정코드 : " + code);

                        }

                        if (b_set && tag.compareTo("MSRSTENAME") == 0) {
                            String name = parser.nextText();
                            airData.setA_MSRSTENAME(name);
                            Log.e("AIR", "측정소명 : " + name);
                        }
                        if (b_set && tag.compareTo("MAXINDEX") == 0) {
                            String index = parser.nextText();
                            airData.setA_MAXINDEX(index);
                            Log.e("AIR", "대기환경지수 : " + index);
                        }
                        if (b_set && tag.compareTo("OZONE") == 0) {
                            String oz = parser.nextText();
                            airData.setA_OZONE(oz);
                            Log.e("AIR", "오존지수 : " + oz);
                        }
                        if (b_set && tag.compareTo("PM10") == 0) {
                            String pm10 = parser.nextText();
                            airData.setA_PM10(pm10);
                            Log.e("AIR", "미세먼지 : " + pm10);
                        }
                        if (b_set && tag.compareTo("PM25") == 0) {
                            String pm25 = parser.nextText();
                            airData.setA_PM25(pm25);
                            Log.e("AIR", "초미세먼지 : " + pm25);
                        }
                        if (b_set && tag.compareTo("GRADE") == 0) {
                            String grd = parser.nextText();
                            airData.setA_GRADE(grd);
                            Log.e("AIR", "등급 : " + grd);
                            m_airData.add(airData);
                        }

                        break;
                }
                eventType = parser.next();
            }
            Log.e("AIR", "AirParser End...");
        } catch (Exception e) {
            Log.e("AIR", "AirParser Error!");
        }

        return m_airData;
    }
}
