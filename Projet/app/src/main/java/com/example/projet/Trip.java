package com.example.projet;

import android.os.Parcel;
import android.os.Parcelable;

public class Trip implements Parcelable {
    private long id;
    private String city;
    private String startDate;
    private String endDate;

    public Trip(long id, String city, String startDate, String endDate) {
        this.id = id;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Trip(String city, String startDate, String endDate) {
        this(-1, city, startDate, endDate);
    }

    protected Trip(Parcel in) {
        id = in.readLong();
        city = in.readString();
        startDate = in.readString();
        endDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(city);
        dest.writeString(startDate);
        dest.writeString(endDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
