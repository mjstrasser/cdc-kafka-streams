package mjs.cdc.helper.database

import mjs.cdc.CustomerData
import mjs.cdc.CustomerMessage
import mjs.cdc.Headers
import mjs.cdc.helper.randomId
import mjs.cdc.helper.randomPastInstant
import java.time.Instant

fun customerData(
    id: Long = randomId(),
    created: Instant = randomPastInstant(),
    lastUpdated: Instant = created,
    gender: String? = null,
): CustomerData = CustomerData.newBuilder()
    .setCustomerId(id)
    .setCreated(created)
    .setLastUpdated(lastUpdated)
    .setGender(gender)
    .build()

fun customerMessage(
    headers: Headers = headers(),
    data: CustomerData = customerData(),
    beforeData: CustomerData? = null,
): CustomerMessage = CustomerMessage.newBuilder().setHeaders(headers).setData(data).setBeforeData(beforeData).build()
