package com.example.yun.seoulock.air;

import android.os.Parcel;
import android.os.Parcelable;

public class AirData implements Parcelable {

    String a_MSRDATE;
    String a_MSRADMCODE;
    String a_MSRSTENAME;
    String a_MAXINDEX;
    String a_OZONE;
    String a_PM10;
    String a_PM25;
    String a_GRADE;

    public AirData() {
    }

    public AirData(Parcel in) {
        readFromParcel(in);
    }

    public AirData(String date, String code, String name, String index, String ozone, String pm10, String pm25, String grade) {
        this.a_MSRDATE = date;
        this.a_MSRADMCODE = code;
        this.a_MSRSTENAME = name;
        this.a_MAXINDEX = index;
        this.a_OZONE = ozone;
        this.a_PM10 = pm10;
        this.a_PM25= pm25;
        this.a_GRADE = grade;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(a_MSRDATE);
        dest.writeString(a_MSRADMCODE);
        dest.writeString(a_MSRSTENAME);
        dest.writeString(a_MAXINDEX);
        dest.writeString(a_OZONE);
        dest.writeString(a_PM10);
        dest.writeString(a_PM25);
        dest.writeString(a_GRADE);
    }

    private void readFromParcel(Parcel in){
        a_MSRDATE = in.readString();
        a_MSRADMCODE = in.readString();
        a_MSRSTENAME = in.readString();
        a_MAXINDEX = in.readString();
        a_OZONE = in.readString();
        a_PM10 = in.readString();
        a_PM25 = in.readString();
        a_GRADE = in.readString();
    }

    public static final Parcelable.Creator<AirData> CREATOR = new Parcelable.Creator<AirData>() {
        public AirData createFromParcel(Parcel in) {
            return new AirData(in);
        }

        public AirData[] newArray(int size) {
            return new AirData[size];
        }
    };

    public String getA_GRADE() {
        return a_GRADE;
    }
    public String getA_MAXINDEX() {
        return a_MAXINDEX;
    }
    public String getA_MSRADMCODE() {
        return a_MSRADMCODE;
    }
    public String getA_MSRDATE() {
        return a_MSRDATE;
    }
    public String getA_MSRSTENAME() {
        return a_MSRSTENAME;
    }
    public String getA_OZONE() {
        return a_OZONE;
    }
    public String getA_PM10() {
        return a_PM10;
    }
    public String getA_PM25() {
        return a_PM25;
    }

    public void setA_GRADE(String a_GRADE) {
        this.a_GRADE = a_GRADE;
    }
    public void setA_MAXINDEX(String a_MAXINDEX) {
        this.a_MAXINDEX = a_MAXINDEX;
    }
    public void setA_MSRADMCODE(String a_MSRADMCODE) {
        this.a_MSRADMCODE = a_MSRADMCODE;
    }
    public void setA_MSRDATE(String a_MSRDATE) {
        this.a_MSRDATE = a_MSRDATE;
    }
    public void setA_MSRSTENAME(String a_MSRSTENAME) {
        this.a_MSRSTENAME = a_MSRSTENAME;
    }
    public void setA_OZONE(String a_OZONE) {
        this.a_OZONE = a_OZONE;
    }
    public void setA_PM10(String a_PM10) {
        this.a_PM10 = a_PM10;
    }
    public void setA_PM25(String a_PM25) {
        this.a_PM25 = a_PM25;
    }

}
