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

import mjs.cdc.AddressData
import mjs.entities.Address

/**
 * Map from a database`Addresses` database row to an entity `Address`.
 */
object AddressMapper : Mapper<AddressData, Address> {
    override fun map(from: AddressData): Address =
        Address
            .newBuilder()
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
