package mjs.cdc.topology

import io.klogging.noCoLogger
import mjs.cdc.CustomerAddressData
import mjs.cdc.CustomerAddressMessage
import mjs.cdc.CustomerData
import mjs.cdc.CustomerMessage
import mjs.cdc.CustomerNameData
import mjs.cdc.CustomerNameMessage
import mjs.cdc.EntityBuilder.customerIdFrom
import mjs.cdc.infra.Reflection.getList
import mjs.cdc.infra.Reflection.getValue
import mjs.cdc.infra.Reflection.setValue
import mjs.cdc.infra.upsert
import mjs.cdc.mappers.CustomerAddressMapper
import mjs.cdc.mappers.CustomerMapper
import mjs.cdc.mappers.CustomerNameMapper
import mjs.cdc.operation
import mjs.cdc.pk
import mjs.database.header.operation
import mjs.entities.CustomerAddress
import mjs.entities.CustomerCreatedEvent
import mjs.entities.CustomerModifiedEvent
import mjs.entities.CustomerName
import org.apache.avro.specific.SpecificRecord
import org.apache.avro.specific.SpecificRecordBuilderBase
import java.time.Instant
import java.util.UUID

object CustomerEventBuilder {

    private val logger = noCoLogger<CustomerEventBuilder>()

    fun build(messages: List<SpecificRecord>): SpecificRecord? {
        if (messages.isEmpty()) {
            logger.warn("No messages were supplied to build a customer event")
            return null
        }
        val builder =
            if (isNewCustomer(messages)) CustomerCreatedEvent.newBuilder()
            else CustomerModifiedEvent.newBuilder()

        return buildEvent(builder, messages)
    }

    private fun isNewCustomer(messages: List<SpecificRecord>): Boolean = messages.any { message ->
        message is CustomerMessage && message.operation == operation.INSERT
    }

    private fun <T : SpecificRecord> buildEvent(
        builder: SpecificRecordBuilderBase<T>,
        messages: List<SpecificRecord>,
    ): T {
        val lastUpdated = messages.fold(INSTANT_MIN) { lastUpdated, message ->
            val customerId = customerIdFrom(message)
            val builderId: Long? = builder.getValue("customerId") // Returns 0 if not set
            if (builderId != 0L && customerId != builderId) {
                logger.warn(
                    "Attempted to add message with ID {messageId} to event for ID {eventId}",
                    customerId, builderId
                )
                INSTANT_MIN
            } else {
                builder.setValue("customerId", customerId)
                when (message) {
                    is CustomerMessage -> maxOf(lastUpdated, addCustomer(builder, message.data))
                    is CustomerNameMessage -> maxOf(lastUpdated, addCustomerName(builder, message.data))
                    is CustomerAddressMessage -> maxOf(lastUpdated, addCustomerAddress(builder, message.data))
                    else -> INSTANT_MIN
                }
            }
        }
        builder.setValue("id", UUID.randomUUID())
        builder.setValue("timestamp", lastUpdated)
        return builder.build()
    }

    private fun <T : SpecificRecord> addCustomer(
        builder: SpecificRecordBuilderBase<T>,
        customer: CustomerData,
    ): Instant {
        val mappedCustomer = CustomerMapper.map(customer)
        builder.setValue("customer", mappedCustomer)

        return mappedCustomer.lastUpdateTimestamp
    }

    private fun <T : SpecificRecord> addCustomerName(
        builder: SpecificRecordBuilderBase<T>,
        customerName: CustomerNameData,
    ): Instant {
        val mappedCustomerName = CustomerNameMapper.map(customerName)
        builder.getList<T, CustomerName>("customerNames")
            .upsert(mappedCustomerName, CustomerName::pk)

        return mappedCustomerName.lastUpdateTimestamp
    }

    private fun <T : SpecificRecord> addCustomerAddress(
        builder: SpecificRecordBuilderBase<T>,
        customerAddress: CustomerAddressData,
    ): Instant {
        val mappedCustomerAddress = CustomerAddressMapper.map(customerAddress)
        builder.getList<T, CustomerAddress>("customerAddresses")
            .upsert(mappedCustomerAddress, CustomerAddress::pk)

        return mappedCustomerAddress.lastUpdateTimestamp
    }
}
