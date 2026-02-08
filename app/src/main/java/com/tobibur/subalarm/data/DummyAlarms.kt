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
                    time = Clock.System.now().toEpochMilliseconds(),
                    subAlarms = listOf(
                        SubAlarm(
                            id = 1,
                            title = "Sub Alarm 1",
                            time = System.currentTimeMillis()
                        )
                    )
                )
            )
        }
    }
}