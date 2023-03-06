package mjs.cdc.mappers

import mjs.cdc.AddressData
import mjs.entities.Address

object AddressMapper : Mapper<AddressData, Address> {
    override fun map(from: AddressData): Address = Address.newBuilder()
        .setId(from.addressId)
        .setCreatedTimestamp(from.created)
        .setLastUpdateTimestamp(from.lastUpdated)
        .setLine1(from.line1)
        .setLine2(from.line2)
        .setLine3(from.line3)
        .setSuburbOrTown(from.suburbTown)
        .setPostalCode(from.postalCode)
        .setStateOrProvince(from.stateProvince)
        .setCountry(from.country)
        .build()
}
