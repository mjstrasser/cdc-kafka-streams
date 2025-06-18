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
): CustomerData =
    CustomerData
        .newBuilder()
        .setCustomerId(id)
        .setCreated(created)
        .setLastUpdated(lastUpdated)
        .setGender(gender)
        .build()

fun customerMessage(
    headers: Headers = headers(),
    data: CustomerData = customerData(),
    beforeData: CustomerData? = null,
): CustomerMessage =
    CustomerMessage
        .newBuilder()
        .setHeaders(headers)
        .setData(data)
        .setBeforeData(beforeData)
        .build()
