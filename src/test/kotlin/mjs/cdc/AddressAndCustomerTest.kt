package mjs.cdc

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

class AddressAndCustomerTest : TopologyTestSpec({

    describe("Inserting address and customer address records") {
        it("produces one event for address and for customer") {
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
        }
    }

})