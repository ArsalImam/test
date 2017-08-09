package com.bykea.pk.partner.models.data;

public class WalletData
{
    private String total;

    private String last_received_via;

    private String referral_status;

    private String __v;

    private boolean isAdded;

    private String _id;

    private String trip_no;

    private String promo_status;

    private String created_at;

    private String debit;

    private String passenger_id;

    private String trip_status;

    private String credit;

    private String comments;
    private String transfer;
    private String title;
    private String balance;


    public String getTotal ()
    {
        return total;
    }

    public void setTotal (String total)
    {
        this.total = total;
    }

    public String getLast_received_via ()
    {
        return last_received_via;
    }

    public void setLast_received_via (String last_received_via)
    {
        this.last_received_via = last_received_via;
    }

    public String getReferral_status ()
    {
        return referral_status;
    }

    public void setReferral_status (String referral_status)
    {
        this.referral_status = referral_status;
    }

    public String get__v ()
    {
        return __v;
    }

    public void set__v (String __v)
    {
        this.__v = __v;
    }

    public boolean getIsAdded ()
    {
        return isAdded;
    }

    public void setIsAdded (boolean isAdded)
    {
        this.isAdded = isAdded;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getTrip_no ()
    {
        return trip_no;
    }

    public void setTrip_no (String trip_no)
    {
        this.trip_no = trip_no;
    }

    public String getPromo_status ()
    {
        return promo_status;
    }

    public void setPromo_status (String promo_status)
    {
        this.promo_status = promo_status;
    }

    public String getCreated_at ()
    {
        return created_at;
    }

    public void setCreated_at (String created_at)
    {
        this.created_at = created_at;
    }

    public String getDebit ()
    {
        return debit;
    }

    public void setDebit (String debit)
    {
        this.debit = debit;
    }

    public String getPassenger_id ()
    {
        return passenger_id;
    }

    public void setPassenger_id (String passenger_id)
    {
        this.passenger_id = passenger_id;
    }

    public String getTrip_status ()
    {
        return trip_status;
    }

    public void setTrip_status (String trip_status)
    {
        this.trip_status = trip_status;
    }

    public String getCredit ()
    {
        return credit;
    }

    public void setCredit (String credit)
    {
        this.credit = credit;
    }

    public String getComments ()
    {
        return comments;
    }

    public void setComments (String comments)
    {
        this.comments = comments;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [total = "+total+", last_received_via = "+last_received_via+", referral_status = "+referral_status+", __v = "+__v+", isAdded = "+isAdded+", _id = "+_id+", trip_no = "+trip_no+", promo_status = "+promo_status+", created_at = "+created_at+", debit = "+debit+", passenger_id = "+passenger_id+", trip_status = "+trip_status+", credit = "+credit+", comments = "+comments+"]";
    }

    public String getTransfer() {
        return transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}