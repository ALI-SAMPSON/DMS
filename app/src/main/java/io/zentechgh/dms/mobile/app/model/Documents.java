package io.zentechgh.dms.mobile.app.model;

public class Documents {

    private String Title;
    private String Comment;
    private String Tag;
    private String DocumentUrl;

    public Documents() {
    }

    public Documents(String title, String comment, String tag, String documentUrl) {
        this.Title = title;
        this.Comment = comment;
        this.Tag = tag;
        this.DocumentUrl = documentUrl;
    }

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
}
