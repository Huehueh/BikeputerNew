package com.example.bikeputernew.ble

import java.util.*

//object BikePuterUuids {
//    const val NOTIFICATION = "00002902-0000-1000-8000-00805f9b34fb"
//    const val TURN_SIGNAL = "b4a2ba47-25f7-46b8-b104-821f859f8071"
//    const val MOVEMENT = "b4a2ba47-25f7-46b8-b104-821f859f8072"
//    const val RESET = "b4a2ba47-25f7-46b8-b104-821f859f8073"
//
//    const val VEL_FREQ = "b4a2ba47-25f7-46b8-b104-821f859f8074"
//    const val TIME = "b4a2ba47-25f7-46b8-b104-821f859f8075"
//    const val DIAMETER = "b4a2ba47-25f7-46b8-b104-821f859f8076"
//    const val IND_FREQ = "b4a2ba47-25f7-46b8-b104-821f859f8077"
//    const val SLEEP_TIME = "b4a2ba47-25f7-46b8-b104-821f859f8078"
//}

enum class Uuid(val stringValue: String) {
    NOTIFICATION("00002902-0000-1000-8000-00805f9b34fb"),
    TURN_SIGNAL("b4a2ba47-25f7-46b8-b104-821f859f8071"),
    MOVEMENT("b4a2ba47-25f7-46b8-b104-821f859f8072"),
    RESET("b4a2ba47-25f7-46b8-b104-821f859f8073");

    fun ToUUID(): UUID {
        return UUID.fromString(stringValue)
    }

}