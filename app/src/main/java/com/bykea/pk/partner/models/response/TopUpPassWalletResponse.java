package com.bykea.pk.partner.models.response;


public class TopUpPassWalletResponse extends CommonResponse {

    private TopUpData data;

    public TopUpData getData() {
        return data;
    }

    public void setData(TopUpData data) {
        this.data = data;
    }


    public class TopUpData {
        private int amount;

        public int getAmount() {
            return amount;
        }
    }
}
