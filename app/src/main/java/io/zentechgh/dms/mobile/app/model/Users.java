package io.zentechgh.dms.mobile.app.model;

public class Users {

    private String uid;
    private String username;
    private String email;
    //private String password;
    private String phone;

    public Users(){}

    public Users(String username, String uid, String email, String phone){
        this.username = username;
        this.uid = uid;
        this.email = email;
        this.phone = phone;
    }

    // getter and setter methods for fields
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
