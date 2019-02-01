package io.zentechgh.dms.mobile.app.model;

import com.google.firebase.database.Exclude;

public class ReceivedDocuments {

    private String Title;
    private String Type;
    private String Comment;
    private String Tag;
    private String DocumentUrl;
    private String Distributee;
    private String Sender;
    // help to search in lower and upper case
    private String search;
    private String key;

    public ReceivedDocuments() {
    }

    public ReceivedDocuments(String title, String type,String comment, String tag,
                             String documentUrl, String distributee, String sender, String search) {
        Title = title;
        Type = type;
        Comment = comment;
        Tag = tag;
        DocumentUrl = documentUrl;
        Distributee = distributee;
        Sender = sender;
        search = search;
    }

    // getters and setters for document
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getDocumentUrl() {
        return DocumentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        DocumentUrl = documentUrl;
    }

    public String getDistributee() {
        return Distributee;
    }

    public void setDistributee(String distributee) {
        Distributee = distributee;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
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
