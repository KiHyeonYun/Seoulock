package com.example.yun.seoulock.eve;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.yun.seoulock.air.AirData;

public class EveData implements Parcelable {
    //CODENAME
    String e_GENRE;
    //MAIN_IMG
    String e_IMG;
    //TITLE
    String e_TITLE;
    //STRTDATE
    String e_SDATE;
    //END_DATE
    String e_EDATE;
    //TIME
    String e_TIME;
    //PLACE
    String e_PLACE;
    //GCODE
    String e_LOCATION;
    //ORG_LINK
    String e_LINK;

    public EveData() {
    }

    public EveData(Parcel in) {
        readFromParcel(in);
    }

    public EveData(String genre, String img, String title, String sdate, String edate, String time, String place, String loc, String link) {
        this.e_GENRE = genre;
        this.e_IMG = img;
        this.e_TITLE = title;
        this.e_SDATE = sdate;
        this.e_EDATE = edate;
        this.e_TIME = time;
        this.e_PLACE= place;
        this.e_LOCATION = loc;
        this.e_LINK = link;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(e_GENRE);
        dest.writeString(e_IMG);
        dest.writeString(e_TITLE);
        dest.writeString(e_SDATE);
        dest.writeString(e_EDATE);
        dest.writeString(e_TIME);
        dest.writeString(e_PLACE);
        dest.writeString(e_LOCATION);
        dest.writeString(e_LINK);
    }

    private void readFromParcel(Parcel in){
        e_GENRE = in.readString();
        e_IMG = in.readString();
        e_TITLE = in.readString();
        e_SDATE = in.readString();
        e_EDATE = in.readString();
        e_TIME = in.readString();
        e_PLACE = in.readString();
        e_LOCATION = in.readString();
        e_LINK = in.readString();
    }

    public static final Parcelable.Creator<EveData> CREATOR = new Parcelable.Creator<EveData>() {
        public EveData createFromParcel(Parcel in) {
            return new EveData(in);
        }

        public EveData[] newArray(int size) {
            return new EveData[size];
        }
    };

    public String getE_EDATE() {
        return e_EDATE;
    }
    public String getE_GENRE() {
        return e_GENRE;
    }
    public String getE_IMG() {
        return e_IMG;
    }
    public String getE_LINK() {
        return e_LINK;
    }
    public String getE_LOCATION() {
        return e_LOCATION;
    }
    public String getE_PLACE() {
        return e_PLACE;
    }
    public String getE_SDATE() {
        return e_SDATE;
    }
    public String getE_TIME() {
        return e_TIME;
    }
    public String getE_TITLE() {
        return e_TITLE;
    }

    public void setE_EDATE(String e_EDATE) {
        this.e_EDATE = e_EDATE;
    }

    public void setE_GENRE(String e_GENRE) {
        this.e_GENRE = e_GENRE;
    }
    public void setE_IMG(String e_IMG) {
        this.e_IMG = e_IMG;
    }
    public void setE_LINK(String e_LINK) {
        this.e_LINK = e_LINK;
    }
    public void setE_LOCATION(String e_LOCATION) {
        this.e_LOCATION = e_LOCATION;
    }
    public void setE_PLACE(String e_PLACE) {
        this.e_PLACE = e_PLACE;
    }
    public void setE_SDATE(String e_SDATE) {
        this.e_SDATE = e_SDATE;
    }
    public void setE_TIME(String e_TIME) {
        this.e_TIME = e_TIME;
    }
    public void setE_TITLE(String e_TITLE) {
        this.e_TITLE = e_TITLE;
    }
}
