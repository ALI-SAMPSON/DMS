package io.zentechgh.dms.mobile.app.model;

public class Admin {

    private String uid;
    private String username;
    private String email;
    private String password;
    private String phone;
    // field to help in search for username
    private String userType;

    public Admin() {
    }

    public Admin(String uid, String username, String email, String password, String phone, String userType) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.userType = userType;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
