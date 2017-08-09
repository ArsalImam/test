package com.bykea.pk.partner.models.response;


public class ContactNumbersResponse extends CommonResponse {

    Contacts data;

    public Contacts getData() {
        return data;
    }

    public void setData(Contacts data) {
        this.data = data;
    }

    public class Contacts {

        Finance finance;
        Support supports;

        public Finance getFinance() {
            return finance;
        }

        public void setFinance(Finance finance) {
            this.finance = finance;
        }

        public Support getSupports() {
            return supports;
        }

        public void setSupports(Support supports) {
            this.supports = supports;
        }
    }


    public class Finance {
        private String email;
        private String call;
        private String whatsapp;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCall() {
            return call;
        }

        public void setCall(String call) {
            this.call = call;
        }

        public String getWhatsapp() {
            return whatsapp;
        }

        public void setWhatsapp(String whatsapp) {
            this.whatsapp = whatsapp;
        }
    }

    public class Support {
        private String email;
        private String call;
        private String whatsapp;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCall() {
            return call;
        }

        public void setCall(String call) {
            this.call = call;
        }

        public String getWhatsapp() {
            return whatsapp;
        }

        public void setWhatsapp(String whatsapp) {
            this.whatsapp = whatsapp;
        }
    }
}
