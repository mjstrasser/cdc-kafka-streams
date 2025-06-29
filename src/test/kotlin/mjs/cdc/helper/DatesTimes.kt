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

import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random

fun instantNow(): Instant = Instant.now().truncatedTo(ChronoUnit.MICROS)

fun randomPastInstant(maxYears: Long = 1): Instant =
    instantNow()
        .minusSeconds(randomSecondsForYears(maxYears))

private fun randomSecondsForYears(maxYears: Long): Long = Random.nextLong(maxYears * 365 * 86_400)
