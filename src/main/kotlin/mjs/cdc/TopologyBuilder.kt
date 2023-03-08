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

import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Grouped
import org.apache.kafka.streams.kstream.KGroupedStream
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.state.KeyValueStore

const val STATE_STORE = "transactions"

/**
 * Definition of the Kafka Streams topology for combining CDC messages
 * into transactional domain event messages.
 *
 * See https://www.confluent.io/blog/cdc-kafka-for-scalable-microservices-messaging/
 */
class TopologyBuilder(
    private val sourceTopic: String,
    private val sinkTopic: String,
    private val sourceSerde: Serde<SpecificRecord>,
    private val transactionSerde: Serde<Transaction>,
    private val sinkSerde: Serde<SpecificRecord>,
) {

    fun build(): Topology {
        val streamsBuilder = StreamsBuilder()

        /**
         * Stage one: group (repartition) messages by transaction ID, so they can be aggregated.
         *
         * This is necessary because the CDC tool cannot create message keys from transaction IDs
         */
        val grouped: KGroupedStream<String, SpecificRecord> = streamsBuilder
            .stream(
                sourceTopic,
                Consumed.with(Serdes.String(), sourceSerde),
            )
            .groupBy(
                { _, message -> transactionIdFrom(message) },
                Grouped.with(Serdes.String(), sourceSerde),
            )

        /**
         * Stage two: aggregate messages by transaction ID into an instance of [Transaction], that is
         * materialized into a state store.
         */
        val aggregated: KTable<String, Transaction> = grouped
            .aggregate(
                { TransactionBuilder.newTransaction() },
                { key, value, trans ->
                    TransactionBuilder.addEvent(key, value, trans)
                },
                Materialized.`as`<String, Transaction, KeyValueStore<Bytes, ByteArray>>(STATE_STORE)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(transactionSerde),
            )

        /**
         * Stage three: transform complete [Transaction] objects into domain event entities.
         */
        val completeAggregations: KStream<String, Transaction> = aggregated
            .filter { _, trans -> TransactionBuilder.isComplete(trans) }
            .toStream()

        completeAggregations
            .flatMap { key, trans -> transformTransaction(key, trans) }
            .to(sinkTopic, Produced.with(Serdes.String(), sinkSerde))

        return streamsBuilder.build()
    }

    /**
     * Return the transaction ID of a message, if present, by delegating to the extension
     * property.
     */
    private fun transactionIdFrom(message: SpecificRecord): String? = message.transactionId

    /**
     * Transform a [Transaction] into zero or more domain entity objects, by delegating
     * to [EntityBuilder.build].
     */
    private fun transformTransaction(
        transactionId: String,
        transaction: Transaction,
    ): List<KeyValue<String, SpecificRecord>> = EntityBuilder.build(transactionId, transaction)
}
