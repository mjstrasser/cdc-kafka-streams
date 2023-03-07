package mjs.cdc

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import mjs.cdc.helper.TopologyTestSpec
import mjs.cdc.helper.database.addressData
import mjs.cdc.helper.database.addressMessage
import mjs.cdc.helper.database.customerAddressData
import mjs.cdc.helper.database.customerAddressMessage
import mjs.cdc.helper.database.headers
import mjs.cdc.helper.randomId
import mjs.cdc.helper.randomTxnId
import mjs.database.header.operation
import mjs.kotest.description

class AddressAndCustomerTest : TopologyTestSpec({

    description(
        """
        Tests of inserts into Address and CustomerAddress tables in the same transaction. 
    """.trimIndent()
    )

    describe("Inserting Address and CustomerAddress records") {
        it("produces one `AddressCreatedEvent` and one `CustomerModifiedEvent`") {
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
                )
            )

            val events = readOutputs()

            events shouldHaveSize 2
            events.map { it.value::class.java.simpleName }.toSet().shouldContainExactly(
                setOf(
                    "AddressCreatedEvent",
                    "CustomerModifiedEvent",
                )
            )
        }
    }

})