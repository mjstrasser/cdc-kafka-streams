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

import mjs.cdc.helper.instantNow
import mjs.cdc.helper.randomTxnId
import mjs.database.header.Headers
import mjs.database.header.operation

fun headers(
    txnId: String = randomTxnId(),
    op: operation = operation.INSERT,
    eventCounter: Long? = 1,
    lastEvent: Boolean? = false,
): Headers =
    Headers
        .newBuilder()
        .setTransactionId(txnId)
        .setOperation(op)
        .setTimestamp(instantNow().toString())
        .setTransactionEventCounter(eventCounter)
        .setTransactionLastEvent(lastEvent)
        .build()
