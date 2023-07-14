package hua.dy.image.utils

import androidx.preference.PreferenceManager
import splitties.init.appCtx
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private val sharedPreference = PreferenceManager.getDefaultSharedPreferences(appCtx)

class SharedPreferenceEntrust<T>(
    private val key: String,
    private val defaultValue: T
): ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getPreferenceValue(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        setPreferenceValue(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getPreferenceValue(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> sharedPreference.getString(key, defaultValue) as T
            is Long -> sharedPreference.getLong(key, defaultValue) as T
            is Set<*> -> sharedPreference.getStringSet(key, defaultValue as Set<String>) as T
            is Boolean -> sharedPreference.getBoolean(key, defaultValue) as T
            is Float -> sharedPreference.getFloat(key, defaultValue) as T
            is Int -> sharedPreference.getInt(key, defaultValue) as T
            else -> throw IllegalArgumentException("Type Error, cannot get value!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setPreferenceValue(key: String, value: T) {
        val edit = sharedPreference.edit()
        when (value) {
            is String -> edit.putString(key, value)
            is Long -> edit.putLong(key, value)
            is Set<*> -> edit.putStringSet(key, value as Set<String>)
            is Boolean -> edit.putBoolean(key, value)
            is Float -> edit.putFloat(key, value)
            is Int -> edit.putInt(key, value)
            else -> throw IllegalArgumentException("Type Error, cannot be saved!")
        }
        edit.apply()
    }

}