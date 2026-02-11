package com.tobibur.subalarm.data

import kotlin.time.Clock

object DummyAlarms {

    val alarms = mutableListOf<Alarm>()
    init {
        for(i in 0..10){
            alarms.add(
                Alarm(
                    id = i,
                    title = "Alarm no $i",
                    time = System.currentTimeMillis(),
                    subAlarms = listOf(
                        SubAlarm(
                            id = 1,
                            title = "Sub Alarm 1",
                            time = System.currentTimeMillis().plus(600_000)
                        )
                    )
                )
            )
        }
    }
}