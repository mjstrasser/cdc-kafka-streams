package mjs.cdc

import io.kotest.matchers.collections.shouldHaveSize
import mjs.cdc.helper.TopologyTestSpec
import mjs.cdc.helper.database.addressMessage
import mjs.cdc.helper.database.headers
import mjs.cdc.helper.randomTxnId
import mjs.database.header.operation

class AddressCreatedTest : TopologyTestSpec({

    describe("Inserting address records into database") {
        it("produces one record out from a single-record transaction") {
            val txnId = randomTxnId()
            sendInput(
                addressMessage(headers(txnId, operation.INSERT, eventCounter = 1, lastEvent = true)),
            )

            val events = readOutputs()

            events shouldHaveSize 1
        }
        it("produces five records from a five-record transaction") {
            val txnId = randomTxnId()
            sendInput(
                addressMessage(headers(txnId, operation.INSERT, eventCounter = 1, lastEvent = false)),
                addressMessage(headers(txnId, operation.INSERT, eventCounter = 2, lastEvent = false)),
                addressMessage(headers(txnId, operation.INSERT, eventCounter = 3, lastEvent = false)),
                addressMessage(headers(txnId, operation.INSERT, eventCounter = 4, lastEvent = false)),
                addressMessage(headers(txnId, operation.INSERT, eventCounter = 5, lastEvent = true)),
            )

            val events = readOutputs()

            events shouldHaveSize 5
        }
    }

})