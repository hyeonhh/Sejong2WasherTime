package com.example.sejong2washertimer.data

import com.example.sejong2washertimer.R
import com.example.sejong2washertimer.model.Dryer
import com.example.sejong2washertimer.model.Washer

class Datasource() {
    val washers = listOf(
        Washer("1",R.drawable.washing_machine,R.string.washer1,true),
        Washer("2",R.drawable.washing_machine,R.string.washer2,true),
        Washer("3",R.drawable.washing_machine,R.string.washer3,true),
        Washer("4",R.drawable.washing_machine,R.string.washer4,true),
        Washer("5",R.drawable.washing_machine,R.string.washer5,true),
        Washer("6",R.drawable.washing_machine,R.string.washer6,true),
        Washer("7",R.drawable.washing_machine,R.string.washer7,true),

        )
    val dryers = listOf(
        Dryer(R.drawable.dryer,R.string.dryer1,50),
        Dryer(R.drawable.dryer,R.string.dryer2,50),
        Dryer(R.drawable.dryer,R.string.dryer3,50),


        )
    
}