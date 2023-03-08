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
package mjs.cdc.topology

import io.klogging.noCoLogger
import mjs.cdc.AddressData
import mjs.cdc.AddressMessage
import mjs.cdc.infra.Reflection.getValue
import mjs.cdc.infra.Reflection.setValue
import mjs.cdc.mappers.AddressMapper
import mjs.cdc.operation
import mjs.database.header.operation
import mjs.entities.AddressCreatedEvent
import mjs.entities.AddressModifiedEvent
import org.apache.avro.specific.SpecificRecord
import org.apache.avro.specific.SpecificRecordBuilderBase
import java.time.Instant
import java.util.UUID

val INSTANT_MIN = Instant.parse("0001-01-01T00:00:00Z")

object AddressEventBuilder {

    private val logger = noCoLogger<AddressEventBuilder>()

    fun build(messages: List<SpecificRecord>): SpecificRecord? {
        if (messages.isEmpty()) {
            logger.warn("No messages were supplied to build an address event")
            return null
        }
        val builder =
            if (isNewAddress(messages)) {
                AddressCreatedEvent.newBuilder()
            } else {
                AddressModifiedEvent.newBuilder()
            }

        return buildEvent(builder, messages)
    }

    private fun isNewAddress(messages: List<SpecificRecord>): Boolean = messages.any { message ->
        message is AddressMessage && message.operation == operation.INSERT
    }

    private fun <T : SpecificRecord> buildEvent(
        builder: SpecificRecordBuilderBase<T>,
        messages: List<SpecificRecord>,
    ): T {
        val lastUpdated = messages.fold(INSTANT_MIN) { lastUpdated, message ->
            when (message) {
                is AddressMessage -> maxOf(lastUpdated, addAddress(builder, message.data))
                else -> INSTANT_MIN
            }
        }
        builder.setValue("id", UUID.randomUUID())
        builder.setValue("timestamp", lastUpdated)
        return builder.build()
    }

    private fun <T : SpecificRecord> addAddress(
        builder: SpecificRecordBuilderBase<T>,
        address: AddressData,
    ): Instant {
        val addressId = address.addressId
        val builderId: Long? = builder.getValue("addressId") // Returns 0 if not set
        if (builderId != 0L && addressId != builderId) {
            logger.warn(
                "Attempted to add message with ID {messageId} to event for ID {eventId}",
                addressId,
                builderId,
            )
        }
        builder.setValue("addressId", addressId)
        val mappedAddress = AddressMapper.map(address)
        builder.setValue("address", mappedAddress)

        return mappedAddress.lastUpdateTimestamp
    }
}
