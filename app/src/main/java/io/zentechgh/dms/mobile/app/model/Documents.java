package io.zentechgh.dms.mobile.app.model;

public class Documents {

    private String name;
    private String comment;
    private String tag;
    private String documentUrl;

    public Documents() {
    }

    public Documents(String name, String comment, String tag, String documentUrl) {
        this.name = name;
        this.comment = comment;
        this.tag = tag;
        this.documentUrl = documentUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
}
