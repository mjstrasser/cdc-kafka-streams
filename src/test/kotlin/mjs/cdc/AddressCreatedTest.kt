package mjs.cdc

import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldBeTypeOf
import mjs.cdc.helper.TopologyTestSpec
import mjs.cdc.helper.database.addressMessage
import mjs.cdc.helper.database.headers
import mjs.cdc.helper.randomTxnId
import mjs.database.header.operation
import mjs.entities.AddressCreatedEvent
import mjs.kotest.description

class AddressCreatedTest : TopologyTestSpec({

    description(
        """
        Tests of inserting one or more Address records in the the database in a single transaction.
    """.trimIndent()
    )

    context("Inserting address records into the database") {
        test("produces one `AddressCreatedEvent` from a single-record transaction") {
            val txnId = randomTxnId()
            sendInput(
                addressMessage(headers(txnId, operation.INSERT, eventCounter = 1, lastEvent = true)),
            )

            val events = readOutputs()

            events shouldHaveSize 1
            events.first().value.shouldBeTypeOf<AddressCreatedEvent>()
        }
        test("produces five `AddressCreatedEvent`s from a five-record transaction") {
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
            events.shouldForAll { it.value.shouldBeTypeOf<AddressCreatedEvent>() }
        }
    }
})