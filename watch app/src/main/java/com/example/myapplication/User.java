package com.example.myapplication;

public class User {
    private String name, surname, city;

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String name, String surname, String city) {
        this.name = name;
        this.surname = surname;
        this.city = city;
    }
}
