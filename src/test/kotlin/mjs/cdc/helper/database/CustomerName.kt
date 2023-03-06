package mjs.cdc.helper.database

import mjs.cdc.CustomerNameData
import mjs.cdc.CustomerNameMessage
import mjs.cdc.Headers
import mjs.cdc.helper.randomId
import mjs.cdc.helper.randomName
import mjs.cdc.helper.randomPastInstant
import java.time.Instant

fun customerNameData(
    customerId: Long = randomId(),
    name: String = randomName(),
    sortName: String? = name.split(" ").lastOrNull(),
    id: Long = randomId(),
    created: Instant = randomPastInstant(),
    lastUpdated: Instant = created,
): CustomerNameData = CustomerNameData.newBuilder()
    .setId(id)
    .setCreated(created)
    .setLastUpdated(lastUpdated)
    .setCustomerId(customerId)
    .setName(name)
    .setSortName(sortName)
    .build()

fun customerNameMessage(
    headers: Headers = headers(),
    data: CustomerNameData = customerNameData(),
    beforeDate: CustomerNameData? = null,
): CustomerNameMessage =
    CustomerNameMessage.newBuilder().setHeaders(headers).setData(data).setBeforeData(beforeDate).build()
