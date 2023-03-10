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

import io.klogging.noCoLogger
import mjs.entities.AddressCreatedEvent
import mjs.entities.AddressModifiedEvent
import mjs.entities.CustomerCreatedEvent
import mjs.entities.CustomerModifiedEvent
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue

/**
 * Builder for domain entities from completed [Transaction] objects.
 */
object EntityBuilder {

    private val logger = noCoLogger<EntityBuilder>()

    /**
     * Build some address or customer domain entities from the database rows in a single transaction.
     *
     * Each entity is for a single database primary key value: thus, if a transaction contains an
     * insert and an update to the same table, there will be one entity event message produced.
     */
    fun build(transactionId: String, transaction: Transaction): List<KeyValue<String, SpecificRecord>> {
        logger.debug("Building events for transaction {transactionId}", transactionId)

        val addressEvents = addressEntityMessages(transaction)
            .mapNotNull { AddressEventBuilder.build(it) }
            .map { KeyValue(eventIdFrom(it), it) }

        val customerEvents = customerEntityMessages(transaction)
            .mapNotNull { CustomerEventBuilder.build(it) }
            .map { KeyValue(eventIdFrom(it), it) }

        return addressEvents + customerEvents
    }

    private fun eventIdFrom(eventMessage: SpecificRecord): String = when (eventMessage) {
        is AddressCreatedEvent -> eventMessage.addressId.toString()
        is AddressModifiedEvent -> eventMessage.addressId.toString()
        is CustomerCreatedEvent -> eventMessage.customerId.toString()
        is CustomerModifiedEvent -> eventMessage.customerId.toString()
        else -> {
            logger.warn("Unknown ID from event message of type {messageType}", eventMessage.messageType)
            "0"
        }
    }

    /**
     * Split a list into lists, grouped by the selector function that returns the key of a record.
     */
    private fun <T : SpecificRecord, K> Iterable<T>.splitIntoOrderedLists(selector: (T) -> K) =
        groupBy(selector).values.toList().map { inTransactionOrder(it) }

    /**
     * Sort a list of CDC messages into transaction order as specified in the message header.
     */
    private fun <T : SpecificRecord> inTransactionOrder(messages: List<T>): List<T> = messages
        .sortedBy { it.headers?.transactionEventCounter }

    /**
     * Extract any address CDC messages from the transaction, into lists by address ID.
     */
    private fun addressEntityMessages(transaction: Transaction): List<List<SpecificRecord>> =
        transaction.addresses.splitIntoOrderedLists { it.data.addressId }

    /**
     * Extract any customer CDC messages from the transaction, into lists by customer ID.
     *
     * This includes messages from the `Customers`, `CustomerNames` and `CustomerAddresses` tables.
     */
    private fun customerEntityMessages(aggregation: Transaction): List<List<SpecificRecord>> =
        (aggregation.customers + aggregation.customerNames + aggregation.customerAddresses).splitIntoOrderedLists {
            customerIdFrom(it)
        }

    /**
     * Extract the customer ID from a customer CDC message.
     */
    fun customerIdFrom(message: SpecificRecord): Long = when (message) {
        is CustomerMessage -> message.data.customerId
        is CustomerNameMessage -> message.data.customerId
        is CustomerAddressMessage -> message.data.customerId
        else -> 0
    }
}
