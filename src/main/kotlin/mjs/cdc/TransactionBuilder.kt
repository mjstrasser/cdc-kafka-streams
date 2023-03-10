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
import org.apache.avro.specific.SpecificRecord

/**
 * Object that builds and manages [Transaction] objects, that collect database row objects belonging to the same
 * transaction.
 */
object TransactionBuilder {

    private val logger = noCoLogger<TransactionBuilder>()

    /**
     * A new, empty transaction object. If the Avro definition has required fields without defaults, they
     * must be set here.
     */
    fun newTransaction(): Transaction = Transaction.newBuilder()
        .build()

    /**
     * Add a database row to a [Transaction], returning a new instance if it was added successfully.
     *
     * In case of error, return the existing instance.
     *
     * This example code omits some sense checks, e.g. that this message is from the same transaction.
     */
    @Suppress("ReturnCount")
    fun addEvent(
        transactionId: String,
        message: SpecificRecord,
        transaction: Transaction,
    ): Transaction {
        val headers = message.headers
        if (headers == null) {
            logger.warn("No headers in {messageType} message", message.messageType)
            return transaction
        }

        val builder = transaction.copyBuilder(transactionId)
        when (message) {
            is AddressMessage -> builder.addresses.add(message)
            is CustomerMessage -> builder.customers.add(message)
            is CustomerNameMessage -> builder.customerNames.add(message)
            is CustomerAddressMessage -> builder.customerAddresses.add(message)
            else -> {
                logger.warn("Unknown {operation} message {messageType}", message.operation, message.messageType)
                return transaction
            }
        }

        // If this is the last event, we know how many messages are expected.
        // Otherwise, copy the previous value (default value is -1).
        builder.totalMessages =
            if (headers.transactionLastEvent == true) {
                headers.transactionEventCounter
            } else {
                transaction.totalMessages
            }
        builder.messageCounter = transaction.messageCounter + 1

        return builder.build()
    }

    /**
     * Extension function on [Transaction] that creates a new builder with current values set.
     */
    private fun Transaction.copyBuilder(transactionId: String) = Transaction.newBuilder()
        .setTransactionId(transactionId)
        .setMessageCounter(messageCounter)
        .setTotalMessages(totalMessages)
        .setCustomers(customers)
        .setCustomerNames(customerNames)
        .setCustomerAddresses(customerAddresses)
        .setAddresses(addresses)

    /**
     * A transaction is complete when the number of messages added is the same as the number expected.
     *
     * NB: In this example code, there is no checking for duplicate values.
     */
    fun isComplete(transaction: Transaction): Boolean =
        transaction.messageCounter == transaction.totalMessages
}
