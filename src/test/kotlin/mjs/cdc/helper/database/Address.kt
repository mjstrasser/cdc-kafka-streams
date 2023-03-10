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

@Suppress("LongParameterList")
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
