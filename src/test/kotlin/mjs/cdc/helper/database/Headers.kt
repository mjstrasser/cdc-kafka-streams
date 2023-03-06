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
): Headers = Headers.newBuilder()
    .setTransactionId(txnId)
    .setOperation(op)
    .setTimestamp(instantNow().toString())
    .setTransactionEventCounter(eventCounter)
    .setTransactionLastEvent(lastEvent)
    .build()
