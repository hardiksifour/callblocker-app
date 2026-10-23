package com.example.callblocker

import android.telecom.Call
import android.telecom.CallScreeningService

class SpamCallScreeningService : CallScreeningService() {

    override fun onScreenCall(details: Call.Details) {
        // Only screen incoming calls
        if (details.callDirection != Call.Details.DIRECTION_INCOMING) {
            respondToCall(details, CallResponse.Builder().build())
            return
        }

        // number is null for private/withheld caller ID -> let it through
        val number = details.handle?.schemeSpecificPart
        val block = number != null && PrefixStore(this).isBlocked(number)

        val response = CallResponse.Builder()
            .setDisallowCall(block)        // don't let it through
            .setRejectCall(block)          // reject it, so it never rings
            .setSkipNotification(block)    // no "missed call" notification
            .setSkipCallLog(false)         // keep in call log so you can review
            .build()

        respondToCall(details, response)
    }
}
