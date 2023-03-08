package mjs.cdc

import mjs.entities.Address
import mjs.entities.Customer
import mjs.entities.CustomerAddress
import mjs.entities.CustomerName
import org.apache.avro.specific.SpecificRecord
import java.time.Instant

interface PK

data class AddressPk(val id: Long) : PK

inline val AddressMessage.pk: AddressPk
    get() = AddressPk(this.data.addressId)

inline val Address.pk: AddressPk
    get() = AddressPk(this.id)

data class CustomerPk(val id: Long) : PK

inline val CustomerMessage.pk: CustomerPk
    get() = CustomerPk(this.data.customerId)

inline val Customer.pk: CustomerPk
    get() = CustomerPk(this.id)

data class CustomerNamePk(val id: Long, val created: Instant) : PK

inline val CustomerNameMessage.pk: CustomerNamePk
    get() = CustomerNamePk(this.data.id, this.data.created)

inline val CustomerName.pk: CustomerNamePk
    get() = CustomerNamePk(this.id, this.createdTimestamp)

data class CustomerAddressPk(val customerId: Long, val addressId: Long) : PK

inline val CustomerAddressMessage.pk: CustomerAddressPk
    get() = CustomerAddressPk(this.data.customerId, this.data.addressId)

inline val CustomerAddress.pk: CustomerAddressPk
    get() = CustomerAddressPk(this.customerId, this.addressId)

inline val SpecificRecord.pk: PK?
    get() = when (this) {
        is AddressMessage -> this.pk
        is CustomerMessage -> this.pk
        is CustomerNameMessage -> this.pk
        is CustomerAddressMessage -> this.pk
        is Address -> this.pk
        is Customer -> this.pk
        is CustomerName -> this.pk
        is CustomerAddress -> this.pk
        else -> null
    }
