package io.zentechgh.dms.mobile.app.model;

public class Users {

    private String uid;
    private String username;
    private String email;
    private String phone;

    // document fields
    private String documentName;
    private String documentComment;
    private String documentTag;
    private String documentUrl;

    public Users(){}

    public Users(String username, String uid, String email, String phone,
                 String documentName,String documentComment, String documentTag,
                 String documentUrl){
        this.username = username;
        this.uid = uid;
        this.email = email;
        this.phone = phone;
        this.documentName = documentName;
        this.documentComment = documentComment;
        this.documentTag = documentTag;
        this.documentUrl = documentUrl;
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

    // getters and setters for user's document
    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentComment() {
        return documentComment;
    }

    public void setDocumentComment(String documentComment) {
        this.documentComment = documentComment;
    }

    public String getDocumentTag() {
        return documentTag;
    }

    public void setDocumentTag(String documentTag) {
        this.documentTag = documentTag;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
}
