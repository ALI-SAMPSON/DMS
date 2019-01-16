package io.zentechgh.dms.mobile.app.model;

import com.google.firebase.database.Exclude;

public class Users {

    private String uid;
    private String username;
    private String email;
    private String phone;
    private String imageUrl;
    // field to help in search for username
    private String search;
    private String userType;
    private String key;

    public Users(){}

    public Users(String username, String uid, String email, String phone,String imageUrl,
                 String search, String usertype){
        this.username = username;
        this.uid = uid;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.search = search;
        this.userType = usertype;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
