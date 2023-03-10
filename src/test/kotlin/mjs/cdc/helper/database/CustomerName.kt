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

import mjs.cdc.CustomerNameData
import mjs.cdc.CustomerNameMessage
import mjs.cdc.Headers
import mjs.cdc.helper.randomId
import mjs.cdc.helper.randomName
import mjs.cdc.helper.randomPastInstant
import java.time.Instant

@Suppress("LongParameterList")
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
