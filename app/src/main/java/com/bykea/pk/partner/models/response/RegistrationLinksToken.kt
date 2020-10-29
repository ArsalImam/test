package com.bykea.pk.partner.models.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Sibtain Raza on 9/9/2020.
 */
data class RegistrationLinksToken(
        @SerializedName("signup_settings")
        var signupSettings: String? = null,
        @SerializedName("signup_add_number")
        var signupAddNumber: String? = null,
        @SerializedName("signup_documents")
        var signupDocuments: String? = null,
        @SerializedName("signup_complete")
        var signupComplete: String? = null,
        var token: String? = null
)