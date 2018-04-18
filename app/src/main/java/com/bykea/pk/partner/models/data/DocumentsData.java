package com.bykea.pk.partner.models.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class DocumentsData implements Parcelable{

    private String urduName;
    private String name;
    private String image;
    private Uri imageUri;
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


    protected DocumentsData(Parcel in) {
        urduName = in.readString();
        name = in.readString();
        image = in.readString();
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        type = in.readString();
        isUploaded = in.readByte() != 0;
        isUploading = in.readByte() != 0;
    }

    public static final Creator<DocumentsData> CREATOR = new Creator<DocumentsData>() {
        @Override
        public DocumentsData createFromParcel(Parcel in) {
            return new DocumentsData(in);
        }

        @Override
        public DocumentsData[] newArray(int size) {
            return new DocumentsData[size];
        }
    };

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

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(urduName);
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeParcelable(imageUri, i);
        parcel.writeString(type);
        parcel.writeByte((byte) (isUploaded ? 1 : 0));
        parcel.writeByte((byte) (isUploading ? 1 : 0));
    }
}
