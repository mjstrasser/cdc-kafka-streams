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
package mjs.cdc.infra

import io.klogging.noCoLogger
import org.apache.avro.specific.SpecificRecord
import org.apache.avro.specific.SpecificRecordBuilderBase
import java.util.Locale

/**
 * Reflection code for manipulating builders and other classes generated from Avro schemas.
 */
@Suppress("TooGenericExceptionCaught")
object Reflection {

    val logger = noCoLogger<Reflection>()

    /**
     * Call a function on a class, returning the result of the specified type, or null.
     */
    @Suppress("SpreadOperator")
    inline fun <T : Any, reified U> T.call(name: String, vararg args: Any): U? {
        val callable = this::class.members.firstOrNull { it.name == name }
        return if (callable == null) {
            logger.warn("Callable ${this::class.qualifiedName}.$name not found")
            null
        } else {
            val callArgs = arrayOf(this, *args)
            when (val result = callable.call(*callArgs)) {
                is U -> result
                else -> null
            }
        }
    }

    private fun capitalise(word: String): String =
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    fun setterName(name: String): String = "set${capitalise(name)}"
    fun getterName(name: String): String = "get${capitalise(name)}"
    fun hasName(name: String): String = "has${capitalise(name)}"

    /**
     * Check if a field has been set by calling its `has` checker. For example the [name] value
     * "firstName" causes a call to `hasFirstName()`.
     */
    fun <T : SpecificRecord> SpecificRecordBuilderBase<T>.hasValue(name: String): Boolean = try {
        call(hasName(name)) ?: false
    } catch (e: Exception) {
        logger.warn("Exception checking if builder value set", e)
        false
    }

    /**
     * Get the value of a field by name by calling its getter. For example, the [name] value
     * "serviceProductId" causes a call to `getServiceProductId()`.
     *
     *  @return the value of the field of type [U], or null if an error occurs.
     */
    inline fun <T : SpecificRecord, reified U> SpecificRecordBuilderBase<T>.getValue(name: String): U? = try {
        call(getterName(name))
    } catch (e: Exception) {
        logger.warn("Exception getting builder value", e)
        null
    }

    /**
     * Set the value of a field by calling its setter. For example, the [name] value
     * "timestamp" causes a call to `setTimestamp()`.
     */
    fun <T : SpecificRecord> SpecificRecordBuilderBase<T>.setValue(name: String, value: Any) {
        try {
            call(setterName(name), value)
        } catch (e: Exception) {
            logger.warn("Exception setting builder value", e)
        }
    }

    /**
     * Get a list field, initialising it to an empty list if it has not been set.
     * For example, the [name] value "licenceClasses" causes:
     *
     * 1. A call to `hasLicenceClasses` to check if the field has been set.
     * 2. A call to `setLicenceClasses` with an empty mutable list if it had not been set.
     * 3. A call to `getLicenceClasses` to return the value of the field.
     *
     * @return existing or new mutable list of [U], or an empty list if an error occurs.
     */
    inline fun <T : SpecificRecord, reified U> SpecificRecordBuilderBase<T>.getList(
        name: String,
    ): MutableList<U> = try {
        if (!hasValue(name)) {
            setValue(name, mutableListOf<U>())
        }
        getValue(name) ?: mutableListOf()
    } catch (e: Exception) {
        logger.warn("Exception getting mutable list", e)
        mutableListOf()
    }
}
