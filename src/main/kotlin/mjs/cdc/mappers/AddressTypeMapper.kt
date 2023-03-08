package mjs.cdc.mappers

import mjs.database.customer_address.AddressTypes

object AddressTypeMapper : Mapper<AddressTypes, mjs.entities.AddressTypes> {
    override fun map(from: AddressTypes): mjs.entities.AddressTypes = when (from) {
        AddressTypes.RES -> mjs.entities.AddressTypes.Residential
        AddressTypes.POS -> mjs.entities.AddressTypes.Postal
    }
}
