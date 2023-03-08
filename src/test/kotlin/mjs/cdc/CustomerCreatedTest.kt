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
package mjs.cdc

import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldBeTypeOf
import mjs.cdc.helper.TopologyTestSpec
import mjs.cdc.helper.database.customerData
import mjs.cdc.helper.database.customerMessage
import mjs.cdc.helper.database.headers
import mjs.cdc.helper.randomId
import mjs.cdc.helper.randomTxnId
import mjs.database.header.operation
import mjs.entities.CustomerCreatedEvent
import mjs.kotest.description

class CustomerCreatedTest : TopologyTestSpec({

    description(
        """
        Tests of inserting one or more Customer records in the the database in a single transaction.
        """.trimIndent(),
    )

    context("Inserting customer records into the database") {
        test("produces one `CustomerCreatedEvent` from a single-record transaction") {
            val txnId = randomTxnId()
            sendInput(
                customerMessage(headers(txnId, operation.INSERT, eventCounter = 1, lastEvent = true)),
            )

            val events = readOutputs()

            events shouldHaveSize 1
            events.first().value.shouldBeTypeOf<CustomerCreatedEvent>()
        }
        test("produces five `CustomerCreatedEvent`s from a five-record transaction") {
            val txnId = randomTxnId()
            sendInput(
                customerMessage(headers(txnId, operation.INSERT, eventCounter = 1, lastEvent = false)),
                customerMessage(headers(txnId, operation.INSERT, eventCounter = 2, lastEvent = false)),
                customerMessage(headers(txnId, operation.INSERT, eventCounter = 3, lastEvent = false)),
                customerMessage(headers(txnId, operation.INSERT, eventCounter = 4, lastEvent = false)),
                customerMessage(headers(txnId, operation.INSERT, eventCounter = 5, lastEvent = true)),
            )

            val events = readOutputs()

            events shouldHaveSize 5
            events.shouldForAll { it.value.shouldBeTypeOf<CustomerCreatedEvent>() }
        }
        test("produces a `CustomerCreatedEvent` when a customer is inserted and updated in the same transaction") {
            val txnId = randomTxnId()
            val customerId = randomId()
            sendInput(
                customerMessage(
                    headers(txnId, operation.INSERT, eventCounter = 1, lastEvent = false),
                    customerData(customerId),
                ),
                customerMessage(
                    headers(txnId, operation.UPDATE, eventCounter = 2, lastEvent = true),
                    customerData(customerId),
                ),
            )

            val events = readOutputs()

            events shouldHaveSize 1
            events.first().value.shouldBeTypeOf<CustomerCreatedEvent>()
        }
    }
})
