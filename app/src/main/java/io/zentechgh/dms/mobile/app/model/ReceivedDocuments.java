package io.zentechgh.dms.mobile.app.model;

import com.google.firebase.database.Exclude;

public class ReceivedDocuments {

    private String Title;
    private String Comment;
    private String Tag;
    private String DocumentUrl;
    private String Distributor;
    private String Sender;
    // help to search in lower and upper case
    private String search;
    private String key;

    public ReceivedDocuments() {
    }

    public ReceivedDocuments(String title, String comment, String tag,
                             String documentUrl, String distributor, String sender, String search) {
        this.Title = title;
        this.Comment = comment;
        this.Tag = tag;
        this.DocumentUrl = documentUrl;
        this.Distributor = distributor;
        this.Sender = sender;
        this.search = search;
    }

    // getters and setters for document
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
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

    public String getDistributor() {
        return Distributor;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public void setDistributor(String distributor) {
        Distributor = distributor;
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
