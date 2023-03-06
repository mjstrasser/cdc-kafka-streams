package mjs.cdc

import io.klogging.noCoLogger
import mjs.cdc.topology.AddressEventBuilder
import mjs.cdc.topology.CustomerEventBuilder
import mjs.entities.AddressCreatedEvent
import mjs.entities.CustomerCreatedEvent
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue

object EntityBuilder {

    private val logger = noCoLogger<EntityBuilder>()

    fun build(txnId: String, aggregation: Transaction): List<KeyValue<String, SpecificRecord>> {
        logger.debug("Building events for transaction {transactionId}", txnId)

        val addressEvents = addressEntityMessages(aggregation)
            .mapNotNull { AddressEventBuilder.build(it) }
            .map { KeyValue(eventIdFrom(it), it) }

        val customerEvents = customerEntityMessages(aggregation)
            .mapNotNull { CustomerEventBuilder.build(it) }
            .map { KeyValue(eventIdFrom(it), it) }

        return addressEvents + customerEvents
    }

    private fun eventIdFrom(eventMessage: SpecificRecord): String = when (eventMessage) {
        is AddressCreatedEvent -> eventMessage.addressId.toString()
        is CustomerCreatedEvent -> eventMessage.customerId.toString()
        else -> "0"
    }

    private fun <T : SpecificRecord, K> Iterable<T>.splitIntoOrderedLists(selector: (T) -> K) =
        groupBy(selector).values.toList().map { inTransactionOrder(it) }

    private fun <T : SpecificRecord> inTransactionOrder(messages: List<T>): List<T> = messages
        .sortedBy { it.headers?.transactionEventCounter }

    private fun addressEntityMessages(aggregation: Transaction): List<List<SpecificRecord>> =
        aggregation.addresses.splitIntoOrderedLists { it.data.addressId }

    private fun customerEntityMessages(aggregation: Transaction): List<List<SpecificRecord>> =
        (aggregation.customers + aggregation.customerNames + aggregation.customerAddresses).splitIntoOrderedLists {
            customerIdFrom(it)
        }

    fun customerIdFrom(message: SpecificRecord): Long = when (message) {
        is CustomerMessage -> message.data.customerId
        is CustomerNameMessage -> message.data.customerId
        is CustomerAddressMessage -> message.data.customerId
        else -> 0
    }
}
