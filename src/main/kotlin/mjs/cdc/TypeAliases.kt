package mjs.cdc

import mjs.database.header.operation
import org.apache.avro.specific.SpecificRecord

typealias Headers = mjs.database.header.Headers

typealias CustomerData = mjs.database.customer.Data
typealias CustomerMessage = mjs.database.customer.DataRecord

typealias CustomerNameData = mjs.database.customer_name.Data
typealias CustomerNameMessage = mjs.database.customer_name.DataRecord

typealias CustomerAddressData = mjs.database.customer_address.Data
typealias CustomerAddressMessage = mjs.database.customer_address.DataRecord

typealias AddressData = mjs.database.address.Data
typealias AddressMessage = mjs.database.address.DataRecord

val SpecificRecord.headers: Headers?
    get() = when (this) {
        is CustomerMessage -> this.headers
        is CustomerNameMessage -> this.headers
        is CustomerAddressMessage -> this.headers
        is AddressMessage -> this.headers
        else -> null
    }

val SpecificRecord.transactionId: String?
    get() = this.headers?.transactionId

val SpecificRecord.operation: operation?
    get() = this.headers?.operation

val SpecificRecord.messageType: String?
    get() = when (this) {
        is CustomerMessage -> "Customer"
        is CustomerNameMessage -> "CustomerName"
        is CustomerAddressMessage -> "CustomerAddress"
        is AddressMessage -> "Address"

        else -> this.schema.fullName
    }