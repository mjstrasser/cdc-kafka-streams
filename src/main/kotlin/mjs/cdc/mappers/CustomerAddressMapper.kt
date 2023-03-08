package mjs.cdc.mappers

import mjs.cdc.CustomerAddressData
import mjs.entities.CustomerAddress

object CustomerAddressMapper : Mapper<CustomerAddressData, CustomerAddress> {
    override fun map(from: CustomerAddressData): CustomerAddress = CustomerAddress.newBuilder()
        .setCustomerId(from.customerId)
        .setAddressId(from.addressId)
        .setCreatedTimestamp(from.created)
        .setLastUpdateTimestamp(from.lastUpdated)
        .setAddressType(AddressTypeMapper.map(from.addressType))
        .build()
}
