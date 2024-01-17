package com.nabil.submission1_appstory.Data

import com.google.gson.annotations.SerializedName

data class GetResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
