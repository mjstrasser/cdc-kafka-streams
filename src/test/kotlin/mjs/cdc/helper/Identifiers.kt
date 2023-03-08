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

import kotlin.random.Random
import kotlin.random.nextULong

fun randomTxnId(): String = Random.nextULong().toString(16)

fun randomId(): Long = Random.nextLong(0, 1_000_000_000_000)

fun randomKey(): String = Random.nextULong().toString(16)
