package com.bykea.pk.partner.dal.source.remote.response

data class AssignLoadboardBookingResponseData(
        val subcode: Int?,
        val driver_id: String?,
        val id: String?,
        val state: String?,
        val trip_id: String?) {

    fun isTaken(): Boolean = (subcode == 4090)
}

/*

//Success response
{
    "code": 200,
    "data": {
        "driver_id": "5c9330641400c547b76ddcb3",
        "id": "46321",
        "trip_id": "5d074e153b16e800182407f9",
        "state": "assigned"
    }
}


//Booking taken response
{
    "statusCode": 422,
    "error": "Unprocessable Entity",
    "message": "state expected to be [open] but found [assigned]",
    "data": {
        "subcode": 4090
    }
}


*/