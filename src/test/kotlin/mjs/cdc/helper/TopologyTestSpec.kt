package mjs.cdc.helper

import io.kotest.core.spec.style.DescribeSpec
import mjs.cdc.TopologyBuilder
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.TestRecord

abstract class TopologyTestSpec(
    body: TopologyTestSpec.() -> Unit,
) : DescribeSpec() {

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

