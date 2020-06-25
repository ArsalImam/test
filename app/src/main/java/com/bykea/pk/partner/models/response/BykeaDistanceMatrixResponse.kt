package com.bykea.pk.partner.models.response

import com.bykea.pk.partner.models.data.BykeaDistanceMatrix

data class BykeaDistanceMatrixResponse (
        val data: BykeaDistanceMatrix
) : CommonResponse()