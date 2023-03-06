package mjs.cdc.helper.database

import mjs.cdc.CustomerAddressData
import mjs.cdc.CustomerAddressMessage
import mjs.cdc.Headers
import mjs.cdc.helper.randomId
import mjs.cdc.helper.randomPastInstant
import mjs.database.customer_address.AddressTypes
import java.time.Instant

fun customerAddressData(
    customerId: Long = randomId(),
    addressId: Long = randomId(),
    addressType: AddressTypes = AddressTypes.RES,
    created: Instant = randomPastInstant(),
    lastUpdated: Instant = created,
): CustomerAddressData = CustomerAddressData.newBuilder()
    .setCreated(created)
    .setLastUpdated(lastUpdated)
    .setCustomerId(customerId)
    .setAddressId(addressId)
    .setAddressType(addressType)
    .build()

fun customerAddressMessage(
    headers: Headers = headers(),
    data: CustomerAddressData = customerAddressData(),
    beforeData: CustomerAddressData? = null,
): CustomerAddressMessage =
    CustomerAddressMessage.newBuilder().setHeaders(headers).setData(data).setBeforeData(beforeData).build()