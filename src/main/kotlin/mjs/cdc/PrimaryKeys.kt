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
