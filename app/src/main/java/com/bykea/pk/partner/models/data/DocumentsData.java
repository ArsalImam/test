package com.bykea.pk.partner.models.data;

public class DocumentsData {

    private String urduName;
    private String name;
    private String image;
    private String type;
    private boolean isUploaded;
    private boolean isUploading;

    public DocumentsData() {
    }

    public DocumentsData(String urduName, String name, String image, String type, boolean isUploaded) {
        this.urduName = urduName;
        this.name = name;
        this.type = type;
        this.image = image;
        this.isUploaded = isUploaded;
    }

    public String getUrduName() {
        return urduName;
    }

    public void setUrduName(String urduName) {
        this.urduName = urduName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUploading() {
        return isUploading;
    }

    public void setUploading(boolean uploading) {
        isUploading = uploading;
    }
}
