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

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import mjs.cdc.helper.TopologyTestSpec
import mjs.cdc.helper.database.addressData
import mjs.cdc.helper.database.addressMessage
import mjs.cdc.helper.database.customerAddressData
import mjs.cdc.helper.database.customerAddressMessage
import mjs.cdc.helper.database.customerData
import mjs.cdc.helper.database.customerMessage
import mjs.cdc.helper.database.headers
import mjs.cdc.helper.randomId
import mjs.cdc.helper.randomTxnId
import mjs.database.header.operation
import mjs.entities.AddressCreatedEvent
import mjs.entities.CustomerCreatedEvent
import mjs.entities.CustomerModifiedEvent
import mjs.kotest.description

class AddressAndCustomerTest : TopologyTestSpec({

    description(
        """
        Tests of inserts into `Customer`, `Address` and `CustomerAddress` tables in the same transaction.
        """.trimIndent(),
    )

    test(
        "Inserting one Address and one CustomerAddress records produces one `AddressCreatedEvent`" +
            " and one `CustomerModifiedEvent`",
    ) {
        val txnId = randomTxnId()
        val customerId = randomId()
        val addressId = randomId()
        sendInput(
            addressMessage(
                headers(txnId, operation.INSERT, eventCounter = 1, lastEvent = false),
                addressData(addressId),
            ),
            customerAddressMessage(
                headers(txnId, operation.INSERT, eventCounter = 2, lastEvent = true),
                customerAddressData(customerId, addressId),
            ),
        )

        val events = readOutputs()

        events shouldHaveSize 2
        val sortedEvents = events.sortedBy { it.value::class.java.simpleName }
        with(sortedEvents.first()) {
            key shouldBe addressId.toString()
            value.shouldBeTypeOf<AddressCreatedEvent>()
        }
        with(sortedEvents.last()) {
            key shouldBe customerId.toString()
            value.shouldBeTypeOf<CustomerModifiedEvent>()
        }
    }

    test(
        "Inserting `Customer`, `Address` and `CustomerAddress` records produces one `AddressCreatedEvent`" +
            " and one `CustomerCreatedEvent`",
    ) {
        val txnId = randomTxnId()
        val customerId = randomId()
        val addressId = randomId()
        sendInput(
            addressMessage(
                headers(txnId, operation.INSERT, eventCounter = 1, lastEvent = false),
                addressData(addressId),
            ),
            customerMessage(
                headers(txnId, operation.INSERT, eventCounter = 2, lastEvent = false),
                customerData(customerId),
            ),
            customerAddressMessage(
                headers(txnId, operation.INSERT, eventCounter = 3, lastEvent = true),
                customerAddressData(customerId, addressId),
            ),
        )

        val events = readOutputs()

        events shouldHaveSize 2
        val sortedEvents = events.sortedBy { it.value::class.java.simpleName }
        with(sortedEvents.first()) {
            key shouldBe addressId.toString()
            value.shouldBeTypeOf<AddressCreatedEvent>()
        }
        with(sortedEvents.last()) {
            key shouldBe customerId.toString()
            value.shouldBeTypeOf<CustomerCreatedEvent>()
        }
    }
})
