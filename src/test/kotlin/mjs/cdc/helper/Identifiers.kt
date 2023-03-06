package mjs.cdc.helper

import kotlin.random.Random
import kotlin.random.nextULong

fun randomTxnId(): String = Random.nextULong().toString(16)

fun randomId(): Long = Random.nextLong(0, 1_000_000_000_000)

fun randomKey(): String = Random.nextULong().toString(16)
