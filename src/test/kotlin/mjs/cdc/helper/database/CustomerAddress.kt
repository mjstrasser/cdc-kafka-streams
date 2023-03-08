/*
   Copyright 2023 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
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
