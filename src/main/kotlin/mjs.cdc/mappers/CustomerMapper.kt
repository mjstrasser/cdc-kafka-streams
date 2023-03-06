package mjs.cdc.mappers

import mjs.cdc.CustomerData
import mjs.entities.Customer

object CustomerMapper : Mapper<CustomerData, Customer> {
    override fun map(from: CustomerData): Customer = Customer.newBuilder()
        .setId(from.customerId)
        .setCreatedTimestamp(from.created)
        .setLastUpdateTimestamp(from.lastUpdated)
        .setGender(from.gender)
        .build()
}
