package com.bykea.pk.partner.models;

import com.google.gson.annotations.SerializedName;

public class Receiver {
        @SerializedName("first_name")
        String firstName;
        @SerializedName("_id")
        String receiverId;
        @SerializedName("last_name")
        String lastName;

        @SerializedName("user_name")
        String username;

        @SerializedName("img_id")
        String image;

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }