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
package mjs.cdc.infra

/**
 * Function that upserts a value in a [MutableList]:
 *
 * - if the value is already present, as identified by the [key] function it is replaced;
 * - else it is added to the list.
 *
 * **NB** The order of list items may not be preserved (although testing shows that it is
 *        because Kotlin creates a LinkedHashMap that preserves key order).
 */
inline fun <T, K> MutableList<T>.upsert(item: T, key: T.() -> K) {
    val byKey = associateBy(key).toMutableMap()
    byKey[item.key()] = item
    clear()
    addAll(byKey.values)
}
