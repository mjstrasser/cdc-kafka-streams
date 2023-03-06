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
