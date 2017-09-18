package com.bykea.pk.partner.models;

import com.google.gson.annotations.SerializedName;

public class Sender {
        @SerializedName("first_name")
        String firstName;
        @SerializedName("_id")
        String senderId;
        @SerializedName("last_name")
        String lastName;

        @SerializedName("img_id")
        String image;

        @SerializedName("user_name")
        String username;

        public void setUsername(String username) {
            this.username = username;
        }
        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }
        public void setImage(String image) {
            this.image = image;
        }
    }