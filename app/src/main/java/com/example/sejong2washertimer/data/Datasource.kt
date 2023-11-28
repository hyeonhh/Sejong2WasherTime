package com.example.sejong2washertimer.data

import com.example.sejong2washertimer.R
import com.example.sejong2washertimer.model.Dryer
import com.example.sejong2washertimer.model.Washer

class Datasource() {
    val washers = listOf(
        Washer(R.drawable.washing_machine,R.string.washer1,50),
        Washer(R.drawable.washing_machine,R.string.washer2,50),
        Washer(R.drawable.washing_machine,R.string.washer3,50),
        Washer(R.drawable.washing_machine,R.string.washer4,50),
        Washer(R.drawable.washing_machine,R.string.washer5,50),
        Washer(R.drawable.washing_machine,R.string.washer6,50),
        Washer(R.drawable.washing_machine,R.string.washer7,50),

        )
    val dryers = listOf(
        Dryer(R.drawable.dryer,R.string.dryer1,50),
        Dryer(R.drawable.dryer,R.string.dryer2,50),
        Dryer(R.drawable.dryer,R.string.dryer3,50),


        )
    
}