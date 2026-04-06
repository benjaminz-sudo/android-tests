package com.example.tp3;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class User implements Parcelable {
    public long id;
    public String firstName;
    public String lastName;
    public int photo;
    public String gender;
    public String birthDate;
    public String nationality;
    public String domain;

    public User(String firstName, String lastName, int photo,
                String gender, String birthDate, String nationality, String domain) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.domain = domain;
    }

    public User(long id,String firstName, String lastName, int photo,
                String gender, String birthDate, String nationality, String domain) {
        this(firstName, lastName, photo, gender, birthDate, nationality, domain);
        this.id = id;
    }
    public long getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getPhoto() {
        return photo;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public String getDomain() {
        return domain;
    }
    public void setId(long id) {
        this.id = id;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setPhoto(int photo) {
        this.photo = photo;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeInt(photo);
        dest.writeString(gender);
        dest.writeString(birthDate);
        dest.writeString(nationality);
        dest.writeString(domain);
    }
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    public User(Parcel parcel){
        id=parcel.readLong();
        firstName=parcel.readString();
        lastName=parcel.readString();
        photo=parcel.readInt();
        gender=parcel.readString();
        birthDate=parcel.readString();
        nationality=parcel.readString();
        domain=parcel.readString();
    }

}
