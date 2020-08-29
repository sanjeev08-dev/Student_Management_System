package com.example.studentmanagementsystem.Model;

public class Users {
    private String id;
    private String name;
    private String imageURL;
    private String email;
    private String gender;
    private String address;

    public Users() {
    }

    public Users(String id, String name, String imageURL, String email, String gender, String address) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.email = email;
        this.gender = gender;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
