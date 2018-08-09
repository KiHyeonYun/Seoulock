package com.example.yun.seoulock.weat;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.yun.seoulock.air.AirData;

public class WeatData implements Parcelable {

    //SAWS_OBS_TM
    String w_DATE;
    //STN_NM
    String w_NAME;
    //SAWS_TA_AVG
    String w_TEMP;
    //SAWS_HD
    String w_HUMI;
    //NAME
    String w_WIND;
    //SAWS_WS_AVG
    String w_WINS;
    //SAWS_RN_SUM
    String w_RAIN;
    //SAWS_SHINE
    String w_SHINE;

    public WeatData() {
    }

    public WeatData(Parcel in) {
        readFromParcel(in);
    }

    public WeatData(String date, String name, String temp, String humi, String wind, String wins, String rain, String shine) {
        this.w_DATE = date;
        this.w_NAME = name;
        this.w_TEMP = temp;
        this.w_HUMI = humi;
        this.w_WIND = wind;
        this.w_WINS = wins;
        this.w_RAIN= rain;
        this.w_SHINE = shine;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(w_DATE);
        dest.writeString(w_NAME);
        dest.writeString(w_TEMP);
        dest.writeString(w_HUMI);
        dest.writeString(w_WIND);
        dest.writeString(w_WINS);
        dest.writeString(w_RAIN);
        dest.writeString(w_SHINE);
    }

    private void readFromParcel(Parcel in){
        w_DATE = in.readString();
        w_NAME = in.readString();
        w_TEMP = in.readString();
        w_HUMI = in.readString();
        w_WIND = in.readString();
        w_WINS = in.readString();
        w_RAIN = in.readString();
        w_SHINE = in.readString();
    }

    public static final Parcelable.Creator<WeatData> CREATOR = new Parcelable.Creator<WeatData>() {
        public WeatData createFromParcel(Parcel in) {
            return new WeatData(in);
        }

        public WeatData[] newArray(int size) {
            return new WeatData[size];
        }
    };

    public String getW_DATE() {
        return w_DATE;
    }
    public String getW_HUMI() {
        return w_HUMI;
    }
    public String getW_NAME() {
        return w_NAME;
    }
    public String getW_RAIN() {
        return w_RAIN;
    }
    public String getW_SHINE() {
        return w_SHINE;
    }
    public String getW_TEMP() {
        return w_TEMP;
    }
    public String getW_WIND() {
        return w_WIND;
    }
    public String getW_WINS() {
        return w_WINS;
    }

    public void setW_DATE(String w_DATE) {
        this.w_DATE = w_DATE;
    }
    public void setW_HUMI(String w_HUMI) {
        this.w_HUMI = w_HUMI;
    }
    public void setW_NAME(String w_NAME) {
        this.w_NAME = w_NAME;
    }
    public void setW_RAIN(String w_RAIN) {
        this.w_RAIN = w_RAIN;
    }
    public void setW_SHINE(String w_SHINE) {
        this.w_SHINE = w_SHINE;
    }
    public void setW_TEMP(String w_TEMP) {
        this.w_TEMP = w_TEMP;
    }
    public void setW_WIND(String w_WIND) {
        this.w_WIND = w_WIND;
    }
    public void setW_WINS(String w_WINS) {
        this.w_WINS = w_WINS;
    }

}
