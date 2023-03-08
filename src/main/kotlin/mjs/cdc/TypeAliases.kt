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
