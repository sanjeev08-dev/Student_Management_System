package com.example.studentmanagementsystem.Model;

public class Students {
    private String rollno;
    private String name;
    private String fathername;
    private String mothername;
    private String gender;
    private String dateofbirth;
    private String phone;
    private String email;
    private String address;
    private String hspercentage;
    private String ispercentage;
    private String imageURL;

    public Students() {
    }

    public Students(String rollno, String name, String fathername, String mothername, String gender, String dateofbirth, String phone, String email, String address, String hspercentage, String ispercentage, String imageURL) {
        this.rollno = rollno;
        this.name = name;
        this.fathername = fathername;
        this.mothername = mothername;
        this.gender = gender;
        this.dateofbirth = dateofbirth;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.hspercentage = hspercentage;
        this.ispercentage = ispercentage;
        this.imageURL = imageURL;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getMothername() {
        return mothername;
    }

    public void setMothername(String mothername) {
        this.mothername = mothername;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHspercentage() {
        return hspercentage;
    }

    public void setHspercentage(String hspercentage) {
        this.hspercentage = hspercentage;
    }

    public String getIspercentage() {
        return ispercentage;
    }

    public void setIspercentage(String ispercentage) {
        this.ispercentage = ispercentage;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
