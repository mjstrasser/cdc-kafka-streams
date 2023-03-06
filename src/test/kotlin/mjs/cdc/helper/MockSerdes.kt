package mjs.cdc.helper

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import mjs.cdc.Transaction
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes

object MockSerdes {
    val String: Serde<String> = Serdes.String()
    val SpecificRecord: Serde<SpecificRecord> = valueSerde()
    val Transaction: Serde<Transaction> = valueSerde()

    private fun <T : SpecificRecord> valueSerde(): SpecificAvroSerde<T> {
        val serde = SpecificAvroSerde<T>(MockSchemaRegistryClient())
        serde.configure(
            mapOf(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "mock://dummy"),
            false,
        )
        return serde
    }
}