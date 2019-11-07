package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import static com.bykea.pk.partner.utils.Constants.AMOUNT_LIMIT;
import static com.bykea.pk.partner.utils.Constants.BYKEA_CASH_MAX_AMOUNT;
import static com.bykea.pk.partner.utils.Constants.PARTNER_TOP_UP_NEGATIVE_LIMIT_FALLBACK;
import static com.bykea.pk.partner.utils.Constants.PARTNER_TOP_UP_POSITIVE_LIMIT_FALLBACK;

public class Settings {
    @SerializedName("cancel_time_driver")
    private String cancel_time;
    private String videos;
    private String heatmap_refresh_timer;
    private String arrived_min_dist;

    @SerializedName("driver_app_version")
    private String app_version;

    @SerializedName("trip_support_link_driver")
    private String trip_support_link;

    @SerializedName("trip_support_max_days_driver")
    private String trip_support_max_days;
    private String demand;
    private String notice;
    private String top_up_limit;
    private String amount_limit;

    @SerializedName("partner_topup_limit_positive")
    private String partnerTopUpLimitPositive;

    @SerializedName("terms_driver")
    private String terms;
    private String cih_range;
    private String partner_topup_limit;
    private String van_partner_topup_limit;
    private String partner_signup_url;

    @SerializedName("withdraw_partner_min_limit")
    private double withdrawPartnerMinLimit;

    @SerializedName("offline_ride_display")
    private boolean offlineRideDisplay;
    @SerializedName("bykea_cash_max_amount")
    private String bykeaCashMaxAmount;

    @SerializedName("bykea_support_helpline")
    private String BykeaSupportHelpline;

    @SerializedName("bykea_support_contact")
    private String BykeaSupportContact;

    @SerializedName("offline_delivery_enable")
    private boolean offlineDeliveryEnable;

    public boolean isOfflineDeliveryEnable() {
        return offlineDeliveryEnable;
    }

    /**
     * getter bykea cash max amount
     *
     * @return bykeaCashMaxAmount
     */
    public int getBykeaCashMaxAmount() {
        return StringUtils.isNotBlank(bykeaCashMaxAmount) ? Integer.parseInt(bykeaCashMaxAmount) : BYKEA_CASH_MAX_AMOUNT;
    }

    /**
     * getter partner topup limit positive
     *
     * @return partnerTopUpLimitPositive
     */
    public int getPartnerTopUpLimitPositive() {
        return StringUtils.isNotBlank(partnerTopUpLimitPositive) ? Integer.parseInt(partnerTopUpLimitPositive) : PARTNER_TOP_UP_POSITIVE_LIMIT_FALLBACK;
    }

    /**
     * getter offline ride display to show or not
     *
     * @return offlineRideDisplay
     */
    public boolean getOfflineRideDisplay() {
        return offlineRideDisplay;
    }

    /**
     * getter withdraw partner max limit
     *
     * @return withdrawPartnerMinLimit
     */
    public double getWithdrawPartnerMinLimit() {
        return withdrawPartnerMinLimit;
    }

    /**
     * setter of withdrawPartnerMinLimit
     *
     * @param withdrawPartnerMinLimit withdrawPartnerMinLimit
     */
    public void setWithdrawPartnerMinLimit(double withdrawPartnerMinLimit) {
        this.withdrawPartnerMinLimit = withdrawPartnerMinLimit;
    }

    /**
     * getter withdraw partner max limit
     *
     * @return withdrawPartnerMaxLimit
     */
    public double getWithdrawPartnerMaxLimit() {
        return withdrawPartnerMaxLimit;
    }

    /**
     * setter of withdrawPartnerMaxLimit
     *
     * @param withdrawPartnerMaxLimit withdrawPartnerMaxLimit
     */
    public void setWithdrawPartnerMaxLimit(double withdrawPartnerMaxLimit) {
        this.withdrawPartnerMaxLimit = withdrawPartnerMaxLimit;
    }

    @SerializedName("withdraw_partner_max_limit")
    private double withdrawPartnerMaxLimit;

    public String getCancel_time() {
        return cancel_time;
    }

    public String getVideos() {
        return videos;
    }

    public String getHeatmap_refresh_timer() {
        return heatmap_refresh_timer;
    }

    public String getTrip_support_link() {
        return StringUtils.isNotBlank(trip_support_link) ? trip_support_link : "http://www.bykea.com/issues/partner.php?";
    }

    public int getTrip_support_max_days() {
        return StringUtils.isNotBlank(trip_support_max_days) ? Integer.parseInt(trip_support_max_days) : 10;
    }

    public String getDemand() {
        return StringUtils.isNotBlank(demand) ? demand : "http://www.bykea.com";

    }

    public String getNotice() {
        return StringUtils.isNotBlank(notice) ? notice : "http://www.bykea.com";

    }

    /**
     * getter partner topup limit negative
     *
     * @return top_up_limit
     */
    public int getTop_up_limit() {
        return StringUtils.isNotBlank(top_up_limit) ? Integer.parseInt(top_up_limit) : PARTNER_TOP_UP_NEGATIVE_LIMIT_FALLBACK;
    }

    public int getAmount_limit() {
        return StringUtils.isNotBlank(amount_limit) ? Integer.parseInt(amount_limit) : AMOUNT_LIMIT;
    }

    public String getTerms() {
        return StringUtils.isNotBlank(terms) ? terms : "https://www.bykea.com/partner-terms";
    }

    public String getApp_version() {
        return app_version;
    }

    public String getCih_range() {
        return cih_range;
    }

    public void setCih_range(String cih_range) {
        this.cih_range = cih_range;
    }

    public int getArrived_min_dist() {
        return Integer.parseInt(arrived_min_dist);
    }

    public String getPartner_topup_limit() {
        return partner_topup_limit;
    }

    public void setPartner_topup_limit(String partner_topup_limit) {
        this.partner_topup_limit = partner_topup_limit;
    }

    public String getPartner_signup_url() {
        return partner_signup_url;
    }

    public void setPartner_signup_url(String partner_signup_url) {
        this.partner_signup_url = partner_signup_url;
    }

    public String getVan_partner_topup_limit() {
        return van_partner_topup_limit;
    }

    public void setVan_partner_topup_limit(String van_partner_topup_limit) {
        this.van_partner_topup_limit = van_partner_topup_limit;
    }

    public String getBykeaSupportHelpline() {
        return BykeaSupportHelpline;
    }

    public void setBykeaSupportHelpline(String bykeaSupportHelpline) {
        BykeaSupportHelpline = bykeaSupportHelpline;
    }


    public String getBykeaSupportContact() {
        return BykeaSupportContact;
    }

    public void setBykeaSupportContact(String bykeaSupportContact) {
        BykeaSupportContact = bykeaSupportContact;
    }
}