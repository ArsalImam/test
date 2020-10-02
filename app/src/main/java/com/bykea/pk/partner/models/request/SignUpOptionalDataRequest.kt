package com.bykea.pk.partner.models.request

/**
 * Created by Sibtain Raza on 9/10/2020.
 */
data class SignUpOptionalDataRequest(
        var email: String? = null,
        var ref_number: String? = null
)