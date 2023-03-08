package mjs.cdc.mappers

import mjs.cdc.CustomerNameData
import mjs.entities.CustomerName

object CustomerNameMapper : Mapper<CustomerNameData, CustomerName> {
    override fun map(from: CustomerNameData): CustomerName = CustomerName.newBuilder()
        .setId(from.id)
        .setCreatedTimestamp(from.created)
        .setLastUpdateTimestamp(from.lastUpdated)
        .setCustomerId(from.customerId)
        .setTitle(from.title)
        .setName(from.name)
        .setSortName(from.sortName)
        .build()
}
