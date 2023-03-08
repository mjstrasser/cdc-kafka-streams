# CDC and Kafka Streams example code

This repository contains code that illustrates the technique described in [Combining CDC
Transactional Messages Using Kafka
Streams](https://www.confluent.io/blog/cdc-kafka-for-scalable-microservices-messaging/).
It is a completely new code base derived from that used in the client work described in that
article. It uses CDC message formats based on those used in that client work.

## The topology

The [Kafka Streams](https://kafka.apache.org/documentation/streams) topology is defined
in [TopologyBuilder.kt](src/main/kotlin/mjs/cdc/TopologyBuilder.kt). It is tested using the
[TopologyTestDriver](https://kafka.apache.org/34/documentation/streams/developer-guide/testing.html)
with current test results [published here](https://cdc-kafka-streams.michaelstrasser.com).

This example does not include code for detecting incomplete transactions and controlling state store
size.

## Avro serialization

All message values are serialized using [Apache Avro](https://avro.apache.org). Messages are
first defined in Avro, then Java classes are generated from those definitions.

For example:

* [transaction.avsc](src/main/avro/transaction.avsc) defines the `Transaction` class.
* The Gradle task `generateAvroJava` generates `Transaction.java` that is used in the code.
* Kafka Streams serializes `Transaction` objects for storing in the state store and deserializes
  them when needed.

## Other code

Supporting code includes:

* [Kotlin type aliases](src/main/kotlin/mjs/cdc/TypeAliases.kt) that make working with CDC message
  classes easier.
* [Test helpers](src/test/kotlin/mjs/cdc/helper) that make writing clear tests simpler.

## Other libraries

* Testing uses the amazing and flexible [Kotest framework for Kotlin](https://kotest.io).
  (This example code does not include unit tests; only topology tests.)
* Test reports are generated using [Kotest HTML
  Reporter](https://github.com/mjstrasser/kotest-html-reporter).
* Logging uses [Klogging framework for Kotlin](https://klogging.io).
