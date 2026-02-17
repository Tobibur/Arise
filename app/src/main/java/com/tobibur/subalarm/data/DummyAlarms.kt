package com.tobibur.subalarm.data

import com.tobibur.subalarm.domain.model.Alarm
import com.tobibur.subalarm.domain.model.SubAlarm

object DummyAlarms {

    val alarms = mutableListOf<Alarm>()

    init {
        for (i in 0..10) {
            alarms.add(
                Alarm(
                    id = i.toLong(),
                    title = "Alarm no $i",
                    time = System.currentTimeMillis(),
                    subAlarms = listOf(
                        SubAlarm(
                            id = 1L,
                            title = "Sub Alarm 1",
                            time = System.currentTimeMillis().plus(600_000)
                        )
                    )
                )
            )
        }
    }
}