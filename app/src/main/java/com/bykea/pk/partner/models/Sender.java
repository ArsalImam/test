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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }