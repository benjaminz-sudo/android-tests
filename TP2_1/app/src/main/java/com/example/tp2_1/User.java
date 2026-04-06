package com.example.tp2_1;

import java.io.Serializable;

public class User implements Serializable {
    public String firstName;
    public String lastName;
    public int photo;
    public String gender;
    public String birthDate;
    public String nationality;
    public String domain;

    public User(String firstName, String lastName, int photo, String gender, String birthDate, String nationality, String domain) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.domain = domain;
    }
}
