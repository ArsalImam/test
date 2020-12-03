package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.Place;

public class GeocoderApi {

    private Results[] results;

    private String status;

    public Results[] getResults() {
        return results;
    }

    public void setResults(Results[] results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public class Results extends Place {
        private Address_components[] address_components;

        private String formatted_address;


        public Address_components[] getAddress_components() {
            return address_components;
        }

        public void setAddress_components(Address_components[] address_components) {
            this.address_components = address_components;
        }

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }
    }

    public class Address_components {
        private String long_name;

        private String[] types;

        private String short_name;

        public String getLong_name() {
            return long_name;
        }

        public void setLong_name(String long_name) {
            this.long_name = long_name;
        }

        public String[] getTypes() {
            return types;
        }

        public void setTypes(String[] types) {
            this.types = types;
        }

        public String getShort_name() {
            return short_name;
        }

        public void setShort_name(String short_name) {
            this.short_name = short_name;
        }
    }


}