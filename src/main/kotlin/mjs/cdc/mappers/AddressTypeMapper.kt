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

import mjs.database.customer_address.AddressTypes

/**
 * Map from a database `AddressesTypes` enum to the entity equivalent.
 */
object AddressTypeMapper : Mapper<AddressTypes, mjs.entities.AddressTypes> {
    override fun map(from: AddressTypes): mjs.entities.AddressTypes =
        when (from) {
            AddressTypes.RES -> mjs.entities.AddressTypes.Residential
            AddressTypes.POS -> mjs.entities.AddressTypes.Postal
        }
}
