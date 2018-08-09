package com.example.yun.seoulock.weat;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;

public class OwmParser {

    // 서울시 City ID
    final static String sid = "1835848";
    // Open Whether Map APIKey
    final static String key = "0babd94331c2a15af69e68dc3b79ded0";

    final static int tmp = 273;

    OwmData data;

    public OwmData GetOwmData() {

        OwmData data = null;
        boolean b_set = false;

        try{

            //http://api.openweathermap.org/data/2.5/weather?id=1835848&APPID=0babd94331c2a15af69e68dc3b79ded0&mode=xml&lang=kr
            String apiUrl = "http://api.openweathermap.org/data/2.5/weather?id="+sid+"&APPID="+key+"&mode=xml&lang=kr";
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
                        if(tag.equals("current")) {
                            b_set = true;
                        }

                        if (b_set && tag.compareTo("city") == 0) {
                            data = new OwmData();
                            String str1 = parser.getAttributeValue(null, "id");
                            data.city_id = str1;
                            String str2 = parser.getAttributeValue(null, "name");
                            data.city_name = str2;
                            Log.e("OWM", str1 + "   " + str2);
                        }
                        if (b_set && tag.compareTo("temperature") == 0) {
                            String str1 = String.valueOf((int)Double.parseDouble(parser.getAttributeValue(null, "value")) - tmp);
                            data.tmp_cur = str1;
                            String str2 = String.valueOf((int)Double.parseDouble(parser.getAttributeValue(null, "min")) - tmp);
                            data.tmp_min = str2;
                            String str3 = String.valueOf((int)Double.parseDouble(parser.getAttributeValue(null, "max")) - tmp);
                            data.tmp_max = str3;
                            Log.e("OWM", str1 + "   " + str2 + "   " + str3);
                        }
                        if (b_set && tag.compareTo("clouds") == 0) {
                            String str1 = parser.getAttributeValue(null, "value");
                            data.cld_amt = str1;
                            String str2 = parser.getAttributeValue(null, "name");
                            data.cld_name = str2;
                            Log.e("OWM", str1 + "   " +str2);
                        }
                        if (b_set && tag.compareTo("weather") == 0) {
                            String str1 = parser.getAttributeValue(null, "value");
                            data.whet_val = str1;
                            String str2 = "http://openweathermap.org/img/w/"+ parser.getAttributeValue(null, "icon") + ".png";
                            data.whet_icon = str2;
                            Log.e("OWM", str1 + "   " + str2);
                        }
                        break;
                }
                eventType = parser.next();
            }
            Log.e("OWM", "OwmParser End...");
        } catch (Exception e) {
            Log.e("OWM", "OwmParser Error!");
        }

        return data;
    }

}
