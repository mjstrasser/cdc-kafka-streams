package mjs.cdc.mappers

interface Mapper<F, T> {
    fun map(from: F): T
    fun mapIf(from: F, predicate: (F) -> Boolean): T? = if (predicate(from)) map(from) else null
    fun mapList(from: List<F>): List<T> = from.map { map(it) }
}