package com.example.projet;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    private long id;
    private String title;
    private String description;
    private String date;
    private String time;
    private String address;
    private String phone;
    private String photo;
    private boolean visited;

    public Place(long id, String title, String description, String date, String time, String address, String phone, String photo, boolean visited) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.address = address;
        this.phone = phone;
        this.photo = photo;
        this.visited = visited;
    }

    public Place(String title, String description, String date, String time, String address, String phone, String photo, boolean visited) {
        this(-1, title, description, date, time, address, phone, photo, visited);
    }

    protected Place(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        date = in.readString();
        time = in.readString();
        address = in.readString();
        phone = in.readString();
        photo = in.readString();
        visited = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(photo);
        dest.writeByte((byte) (visited ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
}
