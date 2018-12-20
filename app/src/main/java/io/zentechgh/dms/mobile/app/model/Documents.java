package io.zentechgh.dms.mobile.app.model;

public class Documents {

    private String Title;
    private String Comment;
    private String Tag;
    private String DocumentUrl;
    private String Distributee;
    // help to search in lower and upper case
    private String search;

    public Documents() {
    }

    public Documents(String title, String comment, String tag,
                     String documentUrl, String distributee, String search) {
        this.Title = title;
        this.Comment = comment;
        this.Tag = tag;
        this.DocumentUrl = documentUrl;
        this.Distributee = distributee;
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

    public String getDistributee() {
        return Distributee;
    }

    public void setDistributee(String distributee) {
        Distributee = distributee;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
