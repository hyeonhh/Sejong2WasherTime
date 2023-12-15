package com.example.sejong2washertimer.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Washer(
    val washerId: String,
    @DrawableRes val washerImageResourceId : Int,
    @StringRes val washerStringResourceId: Int,
    var isAvailable: Boolean ,

)
