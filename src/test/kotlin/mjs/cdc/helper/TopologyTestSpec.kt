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

import io.kotest.core.spec.style.FunSpec
import mjs.cdc.TopologyBuilder
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.TestRecord

abstract class TopologyTestSpec(
    body: TopologyTestSpec.() -> Unit,
) : FunSpec() {

    private lateinit var testDriver: TopologyTestDriver
    private lateinit var sourceTopic: TestInputTopic<String, SpecificRecord>
    private lateinit var sinkTopic: TestOutputTopic<String, SpecificRecord>

    init {
        beforeEach {
            setupTopology()
        }
        afterEach { testDriver.close() }
        body()
    }

    fun sendInput(vararg cdcMessages: SpecificRecord) = cdcMessages.forEach { cdc ->
        sourceTopic.pipeInput(randomKey(), cdc)
    }

    fun readOutputs(): MutableList<TestRecord<String, SpecificRecord>> = sinkTopic.readRecordsToList()

    private fun setupTopology() {
        val topology = TopologyBuilder(
            "database-cdc-messages",
            "domain-event-messages",
            MockSerdes.SpecificRecord,
            MockSerdes.Transaction,
            MockSerdes.SpecificRecord,
        ).build()

        testDriver = TopologyTestDriver(topology)

        sourceTopic = testDriver.createInputTopic(
            "database-cdc-messages",
            MockSerdes.String.serializer(),
            MockSerdes.SpecificRecord.serializer(),
        )
        sinkTopic = testDriver.createOutputTopic(
            "domain-event-messages",
            MockSerdes.String.deserializer(),
            MockSerdes.SpecificRecord.deserializer(),
        )
    }
}
