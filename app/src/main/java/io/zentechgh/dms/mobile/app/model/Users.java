package io.zentechgh.dms.mobile.app.model;

public class Users {

    private String uid;
    private String username;
    private String email;
    private String phone;
    private String imageUrl;
    // field to help in search for username
    private String searchName;
    private String role;

    // document fields
    private String documentName;
    private String documentComment;
    private String documentTag;
    private String documentUrl;

    public Users(){}

    public Users(String username, String uid, String email, String phone,String imageUrl,
                 String searchName, String role){
        this.username = username;
        this.uid = uid;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.searchName = searchName;
        this.role = role;
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

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
