package mjs.cdc.helper.database

import mjs.cdc.AddressData
import mjs.cdc.AddressMessage
import mjs.cdc.Headers
import mjs.cdc.helper.randomAddressLine
import mjs.cdc.helper.randomId
import mjs.cdc.helper.randomPastInstant
import mjs.cdc.helper.randomPostcode
import mjs.cdc.helper.randomState
import mjs.cdc.helper.randomSuburb
import java.time.Instant

fun addressData(
    id: Long = randomId(),
    created: Instant = randomPastInstant(),
    lastUpdated: Instant = created,
    line1: String = randomAddressLine(),
    line2: String? = null,
    line3: String? = null,
    suburbTown: String? = randomSuburb(),
    stateProvince: String? = randomState(),
    country: String? = null,
    postalCode: String? = randomPostcode(),
): AddressData = AddressData.newBuilder()
    .setAddressId(id)
    .setCreated(created)
    .setLastUpdated(lastUpdated)
    .setLine1(line1)
    .setLine3(line2)
    .setLine2(line3)
    .setSuburbTown(suburbTown)
    .setStateProvince(stateProvince)
    .setCountry(country)
    .setPostalCode(postalCode)
    .build()

fun addressMessage(
    headers: Headers = headers(),
    data: AddressData = addressData(),
    beforeData: AddressData? = null,
): AddressMessage = AddressMessage.newBuilder().setHeaders(headers).setData(data).setBeforeData(beforeData).build()
