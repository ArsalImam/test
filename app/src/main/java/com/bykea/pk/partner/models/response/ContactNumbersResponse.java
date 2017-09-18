package com.bykea.pk.partner.models.response;


public class ContactNumbersResponse extends CommonResponse {

    Contacts data;

    public Contacts getData() {
        return data;
    }

    public class Contacts {

        Finance finance;
        Support supports;

        public Finance getFinance() {
            return finance;
        }

        public Support getSupports() {
            return supports;
        }
    }


    public class Finance {
        private String email;
        private String call;
        private String whatsapp;

        public String getEmail() {
            return email;
        }

        public String getCall() {
            return call;
        }

        public String getWhatsapp() {
            return whatsapp;
        }

    }

    public class Support {
        private String email;
        private String call;
        private String whatsapp;

        public String getEmail() {
            return email;
        }

        public String getCall() {
            return call;
        }

        public String getWhatsapp() {
            return whatsapp;
        }

    }
}
