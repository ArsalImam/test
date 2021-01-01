package com.bykea.pk.partner.models.data;

import com.bykea.pk.partner.utils.Constants;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.bykea.pk.partner.utils.Constants.AMOUNT_LIMIT;
import static com.bykea.pk.partner.utils.Constants.BYKEA_CASH_MAX_AMOUNT;
import static com.bykea.pk.partner.utils.Constants.DIGIT_ZERO;
import static com.bykea.pk.partner.utils.Constants.HOW_IT_WORKS_WEB_URL;
import static com.bykea.pk.partner.utils.Constants.MAX_BATCH_BOOKING_LIMIT;
import static com.bykea.pk.partner.utils.Constants.MAX_FAHRENHEIT_VALUE;
import static com.bykea.pk.partner.utils.Constants.MIN_FAHRENHEIT_VALUE;
import static com.bykea.pk.partner.utils.Constants.OFFLINE_KAMAI_WEB_URL;
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

    /**
     * this is the configuration flag to manage calls for geocode or nominatum
     */
    @SerializedName("is_google_geocode_enable")
    private boolean isGoogleGeocodeEnable = false;
    /**
     * this key will manage whether calculations for easy paisa needs to be run on client or server side
     */
    @SerializedName("is_custom_calculations_allow_for_easy_paisa")
    private Boolean isCustomCalculationsAllowForEasyPaisa;
    private String notice;
    private String top_up_limit;
    private String amount_limit;

    @SerializedName("partner_topup_limit_positive")
    private String partnerTopUpLimitPositive;

    @SerializedName("terms_driver")
    private String terms;

    @SerializedName("proof_of_delivery_service_codes")
    private String podServiceCodes;

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

    @SerializedName("offline_delivery_enabled")
    private boolean offlineDeliveryEnable;

    @SerializedName("withdrawal_display")
    private boolean withdrawalDisplay;

    /**
     * trip fees percentage taken by bykea
     */
    @SerializedName("admin_fee")
    private String admin_fee;

    /**
     * List to get image source url according to priority.
     */
    @SerializedName("priority_list")
    private HashMap<String, String> priorityList;

    /**
     * Tello Talk Tags
     */
    @SerializedName("tello_talk_tags")
    private HashMap<String, String> telloTalkTags;

    /**
     * Toggle for ask temperature from partner
     */
    @SerializedName("partner_temperature_input_toggle")
    private boolean partnerTemperatureInputToggle;

    /**
     * Interval for ask temperature from partner
     */
    @SerializedName("partner_temperature_input_interval")
    private long partnerTemperatureInputInterval;

    /**
     * Min limit for temperature
     */
    @SerializedName("partner_temperature_min_limit")
    private String partnerTemperatureMinLimit;

    /**
     * Max limit for temperature
     */
    @SerializedName("partner_temperature_max_limit")
    private String partnerTemperatureMaxLimit;
    @SerializedName("partner_how_it_works_url")
    private String howItWorksUrl;

    /**
     * Max Batch Booking Limit
     */
    @SerializedName("batch_booking_limit")
    private String batchBookingLimit;

    /**
     * Partner Offline Ride Demo URL
     */
    @SerializedName("partner_offline_ride_demo")
    private String partnerOfflineRideDemo;

    /**
     * Cool Down Load Board Timer
     */
    @SerializedName("cool_down_loadboard_timer")
    private Long coolDownLoadboardTimer;

    @SerializedName("banner_update_distance")
    private String bannerUpdateDistance;

    public HashMap<String, String> getPriorityList() {
        return priorityList;
    }

    public boolean isOfflineDeliveryEnable() {
        return offlineDeliveryEnable;
    }

    public boolean isWithdrawalDisplay() {
        return withdrawalDisplay;
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

    public String getAdmin_fee() {
        return StringUtils.isEmpty(admin_fee) ? Constants.DEFAULT_ADMIN_FEE : admin_fee;
    }

    public void setAdmin_fee(String admin_fee) {
        this.admin_fee = admin_fee;
    }

    public List<String> getPodServiceCodes() {
        return StringUtils.isEmpty(podServiceCodes) ?
                Arrays.asList(String.valueOf(Constants.ServiceCode.SEND), String.valueOf(Constants.ServiceCode.SEND_COD),
                        String.valueOf(Constants.ServiceCode.OFFLINE_DELIVERY)) : Arrays.asList(podServiceCodes.split("\\s*,\\s*"));
    }

    public void setPodServiceCodes(String podServiceCodes) {
        this.podServiceCodes = podServiceCodes;
    }

    /**
     * Check whether partner temperature feature is enable or not
     *
     * @return true, if ask temperature else false
     */
    public boolean isPartnerTemperatureInputToggle() {
        return partnerTemperatureInputToggle;
    }

    /**
     * Getter for time interval (seconds) to ask input for temperature after this interval
     *
     * @return long: Time interval in seconds
     */
    public long getPartnerTemperatureInputInterval() {
        return partnerTemperatureInputInterval;
    }

    public double getPartnerTemperatureMinLimit() {
        try {
            return Double.parseDouble(partnerTemperatureMinLimit);
        } catch (Exception e) {
            return MIN_FAHRENHEIT_VALUE;
        }
    }

    public double getPartnerTemperatureMaxLimit() {
        try {
            return Double.parseDouble(partnerTemperatureMaxLimit);
        } catch (Exception e) {
            return MAX_FAHRENHEIT_VALUE;
        }
    }

    public int getBatchBookingLimit() {
        try {
            return Integer.parseInt(batchBookingLimit);
        } catch (Exception e) {
            return MAX_BATCH_BOOKING_LIMIT;
        }
    }

    /**
     * this key will manage whether calculations for easy paisa needs to be run on client or server side
     */
    public boolean isCustomCalculationsAllowForEasyPaisa() {
        return isCustomCalculationsAllowForEasyPaisa == null ? true : isCustomCalculationsAllowForEasyPaisa;
    }

    /**
     * this key will manage whether calculations for easy paisa needs to be run on client or server side
     */
    public void setCustomCalculationsAllowForEasyPaisa(boolean customCalculationsAllowForEasyPaisa) {
        isCustomCalculationsAllowForEasyPaisa = customCalculationsAllowForEasyPaisa;
    }

    public String getHowItWorksUrl() {
        return StringUtils.isEmpty(howItWorksUrl) ? HOW_IT_WORKS_WEB_URL : howItWorksUrl;
    }

    public void setHowItWorksUrl(String howItWorksUrl) {
        this.howItWorksUrl = howItWorksUrl;
    }

    /**
     * Get Offline Kamari Web Url
     *
     * @return String: If not recevied in setting return fallback
     */
    public String getPartnerOfflineRideDemo() {
        return StringUtils.isEmpty(partnerOfflineRideDemo) ? OFFLINE_KAMAI_WEB_URL : partnerOfflineRideDemo;
    }

    /**
     * this is the configuration flag to manage calls for geocode or nominatum
     */
    public boolean isGoogleGeocodeEnable() {
        return isGoogleGeocodeEnable;
    }

    /**
     * this is the configuration flag to manage calls for geocode or nominatum
     */
    public void setGoogleGeocodeEnable(boolean googleGeocodeEnable) {
        isGoogleGeocodeEnable = googleGeocodeEnable;
    }
    /**
     * Cool Down Load Board Timer
     *
     * @return if exit else null
     */
    public Long getCoolDownLoadboardTimer() {
        return coolDownLoadboardTimer == null ? DIGIT_ZERO : coolDownLoadboardTimer;
    }

    public HashMap<String, String> getTelloTalkTags() {
        return telloTalkTags;
    }

    /**
     * Banner Update Distance
     */
    public Long getBannerUpdateDistance() {
        return StringUtils.isEmpty(bannerUpdateDistance) ? Long.parseLong(bannerUpdateDistance) : Constants.MIN_FENCE_DISTANCE;
    }
}