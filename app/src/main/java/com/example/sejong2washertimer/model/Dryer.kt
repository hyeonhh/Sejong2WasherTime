package com.example.sejong2washertimer.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Dryer(
    @DrawableRes val dryerImageResourceId: Int,
    @StringRes val dryerResourceId : Int,
    var dryerRemainedTime:Int
)