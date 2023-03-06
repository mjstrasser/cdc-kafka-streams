package mjs.cdc.helper

import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random

fun instantNow(): Instant = Instant.now().truncatedTo(ChronoUnit.MICROS)

fun randomPastInstant(maxYears: Long = 1): Instant = instantNow()
    .minusSeconds(randomSecondsForYears(maxYears))

private fun randomSecondsForYears(maxYears: Long): Long =
    Random.nextLong(maxYears * 365 * 86_400)
