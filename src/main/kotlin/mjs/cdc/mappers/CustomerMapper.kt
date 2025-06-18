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
package mjs.cdc.mappers

import mjs.cdc.CustomerData
import mjs.entities.Customer

/**
 * Map from a database`Customers` database row to an entity `Customer`.
 */
object CustomerMapper : Mapper<CustomerData, Customer> {
    override fun map(from: CustomerData): Customer =
        Customer
            .newBuilder()
            .setId(from.customerId)
            .setCreatedTimestamp(from.created)
            .setLastUpdateTimestamp(from.lastUpdated)
            .setGender(from.gender)
            .build()
}
