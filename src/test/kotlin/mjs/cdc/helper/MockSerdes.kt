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
